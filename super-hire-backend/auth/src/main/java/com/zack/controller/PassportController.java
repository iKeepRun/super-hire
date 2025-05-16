package com.zack.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.DesensitizedUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.gson.Gson;
import com.zack.base.BaseInfoProperties;
import com.zack.common.CommonResult;
import com.zack.domain.Users;
import com.zack.dto.LoginDTO;
import com.zack.enums.Sex;
import com.zack.enums.ShowWhichName;
import com.zack.enums.UserRole;
import com.zack.exceptions.ErrorCode;
import com.zack.exceptions.ThrowUtil;
import com.zack.feign.WorkMicroFeign;
import com.zack.mapper.UsersMapper;
import com.zack.mq.MQConfig;
import com.zack.mq.SMSContentQO;
import com.zack.utils.GsonUtils;
import com.zack.utils.IPUtil;
import com.zack.utils.JWTUtils;
import com.zack.utils.RedisOperator;
import com.zack.vo.UsersVO;
import io.seata.core.context.RootContext;
import io.seata.core.exception.TransactionException;
import io.seata.spring.annotation.GlobalTransactional;
import io.seata.tm.api.GlobalTransactionContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Date;

/**
 * @description:App端注册登录接口
 * @param:
 * @return:
 **/
@RestController
@RequestMapping("/passport")
@Slf4j
public class PassportController extends BaseInfoProperties {

    @Autowired
    private JWTUtils jwtUtils;
    @Autowired
    private RedisOperator redisOperator;
    @Autowired
    private UsersMapper usersMapper;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private WorkMicroFeign workMicroFeign;

    @PostMapping("/getSMSCode")
    public CommonResult getSMSCode(String mobile,
                                   HttpServletRequest request) {
        ThrowUtil.throwIf(StrUtil.isBlank(mobile), ErrorCode.PARAMS_ERROR);
        String ip = IPUtil.getRequestIp(request);
        log.info("用户ip:{}", ip);
        // 用于防止频繁的访问短信接口
//        redisOperator.setnx60s(ip, mobile);
        SMSContentQO smsContentQO=new SMSContentQO();
        smsContentQO.setMobile(mobile);
        smsContentQO.setContent("1234");
        //TODO  对接第三方短信平台
        //添加confirm回调函数
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            // log.info("correlationData",correlationData.getId());
            if(ack){
                log.info("交换机成功接收到消息",cause);
            }else{
                log.info("交换机接收消息失败",cause);
            }
        });
        //添加return回调函数
        rabbitTemplate.setReturnCallback((message, replyCode, replyText, exchange, routingKey) -> {
            log.info("消息从交换机到队列失败,消息内容:{}",new String(message.getBody()));
            log.info("replyCode:{}",replyCode);
            log.info("replyText:{}",replyText);
            log.info("exchange:{}",exchange);
            log.info("routingKey:{}",routingKey);

        });
        // 配置消息处理器
        MessagePostProcessor messagePostProcessor = new MessagePostProcessor() {
            @Override
            public Message postProcessMessage(Message message) throws AmqpException {
                message.getMessageProperties().setExpiration("20000");
                return message;
            }
        };

          //使用mq异步解耦短信发送
        for (int i = 0; i < 10; i++) {
            rabbitTemplate.convertAndSend(MQConfig.SMS_EXCHANGE,MQConfig.SMS_ROUTING_KEY, GsonUtils.object2String(smsContentQO), messagePostProcessor);
        }

        //存储手机验证码
        redisOperator.set(mobile, "1234", 60L);

        return CommonResult.success();
    }

    @PostMapping("/login")
    @GlobalTransactional
    public CommonResult<UsersVO> login(@Valid @RequestBody LoginDTO loginDTO,
                              HttpServletRequest request) {
        String mobile = loginDTO.getMobile();
        String code = loginDTO.getSmsCode();

        // 1. 从redis中获得验证码进行校验判断是否匹配
        String redisCode = redisOperator.get(mobile);
        if (StrUtil.isBlank(redisCode)) {
            return CommonResult.error("验证码已过期");
        }

        if (!redisCode.equalsIgnoreCase(code)) {
            return CommonResult.error("验证码不正确");
        }


        Users users = usersMapper.selectOne(new QueryWrapper<Users>().eq("mobile", mobile));
        // 用户不存在，就注册
        if (users == null) {
            users = new Users();
            // String mobilePhone = DesensitizedUtil.mobilePhone(mobile);
            users.setMobile(mobile);
            users.setNickname("昵称:"+mobile);
            users.setReal_name("真名:"+mobile);
            users.setShow_which_name(ShowWhichName.nickname.type);
            users.setSex(Sex.man.type);
            users.setFace("");
            users.setEmail("xxx@163.com");
            users.setBirthday(new Date());
            users.setCountry("中国");
            users.setProvince("");
            users.setCity("");
            users.setDistrict("");
            users.setDescription("这家伙喜欢coding,不喜欢说话");
            users.setStart_work_date(new Date());
            users.setPosition("底层码农");
            users.setRole(UserRole.CANDIDATE.type);
            users.setHr_in_which_company_id("");
            users.setHr_signature("");
            users.setHr_tags("");
            users.setCreated_time(new Date());
            users.setUpdated_time(new Date());

            usersMapper.insert(users);

            log.info("获取用户userId {}",users.getId());
            log.info("插入后的用户 {}",users);
           //调用简历服务，初始化用户简历
            CommonResult commonResult = workMicroFeign.init(users.getId());
            if (commonResult.getStatus() != 200) {
                String xid = RootContext.getXID();
                if (StrUtil.isNotBlank(xid)) {
                    try {
                        GlobalTransactionContext.reload(RootContext.getXID()).rollback();
                    } catch (TransactionException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            // int i=1/0;
        }

        String jwt = jwtUtils.createJWTWithPrefix(new Gson().toJson(users), TOKEN_USER_PREFIX);
        UsersVO usersVO = BeanUtil.copyProperties(users, UsersVO.class);
        usersVO.setUserToken(jwt);
        return CommonResult.success(usersVO);
    }

    @PostMapping("logout")
    public CommonResult logout(@RequestParam String userId,
                                  HttpServletRequest request) throws Exception {

        // 后端只需要清除用户的token信息即可，前端也需要清除相关的用户信息
//        redis.del(REDIS_USER_TOKEN + ":" + userId);

        return CommonResult.success();
    }
}
