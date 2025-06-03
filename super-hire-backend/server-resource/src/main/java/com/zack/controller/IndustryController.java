package com.zack.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.zack.base.BaseInfoProperties;
import com.zack.common.CommonResult;
import com.zack.common.GraceJSONResult;
import com.zack.domain.Industry;
import com.zack.mq.MQDelayConfig_industry;
import com.zack.service.IndustryService;
import com.zack.utils.GsonUtils;
import com.zack.utils.LocalDateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("industry")
public class IndustryController extends BaseInfoProperties {
   @Autowired
   private IndustryService industryService;
    @Autowired
    private RabbitTemplate rabbitTemplate;


    /**
     * 获得顶级分类列表
     * @return
     */
    @GetMapping("app/initTopList")
    public CommonResult initTopList() {

        //先从redis中查询，如果没有，再从db中查询后并且放入到redis中
        String topIndustryListStr = redis.get(TOP_INDUSTRY_LIST);
        List<Industry> topIndustryList = null;
        if (StrUtil.isNotBlank(topIndustryListStr)) {
            topIndustryList = GsonUtils.stringToListAnother(topIndustryListStr,
                    Industry.class);
        } else {
            topIndustryList = industryService.getTopIndustryList();
            redis.set(TOP_INDUSTRY_LIST, GsonUtils.object2String(topIndustryList));
        }

        return CommonResult.success(topIndustryList);
       // return CommonResult.success(industryService.getTopIndustryList());
    }

    /**
     * 获得三级分类列表
     * @return
     */
    @GetMapping("app/getThirdListByTop/{topIndustryId}")
    public CommonResult getThirdListByTop(
            @PathVariable("topIndustryId") String topIndustryId) {

        String thirdKey = THIRD_INDUSTRY_LIST + ":byTopId:" + topIndustryId;

        // 先从redis中查询，如果没有，再从db中查询后并且放入到redis中
        String thirdIndustryListStr = redis.get(thirdKey);
        List<Industry> thirdIndustryList = null;
        if (StrUtil.isNotBlank(thirdIndustryListStr)) {
            thirdIndustryList = GsonUtils.stringToListAnother(thirdIndustryListStr, Industry.class);
        } else {
            thirdIndustryList = industryService.getThirdListByTop(topIndustryId);
//            redis.set(thirdKey, GsonUtils.object2String(thirdIndustryList));
            // 避免缓存穿透，增加设空机制
            if (!CollUtil.isEmpty(thirdIndustryList)) {
                redis.set(thirdKey, GsonUtils.object2String(thirdIndustryList));
            } else {
                redis.set(thirdKey, "[]", 10 * 60);
            }
        }
//
        return CommonResult.success(thirdIndustryList);
//        return CommonResult.success(industryService.getThirdListByTop(topIndustryId));
    }


    /****************************** 以上提供给app端使用 ******************************/


    // 业务公用，接口解耦 原则
    /**
     * 为什么要解耦接口，要分开多个？
     * 1. 松耦合，降低前端的依赖
     * 2. 不能把鸡蛋都放在同一个篮子里
     * 3. 不易于维护
     * 4. if else 太多，高并发增加负荷
     */

    /****************************** 以下提供给运营平台使用 ******************************/

    @PostMapping("createNode")
    public CommonResult createNode(@RequestBody Industry industry) {

        // 判断节点是否已经存在
        if (industryService.getIndustryIsExistByName(industry.getName()))
            return CommonResult.error("该行业已经存在！请重新命名~");

        // 节点创建
        industryService.createIndustry(industry);

       // resetRedisIndustry(industry);

        return CommonResult.success();
    }

    /**
     * 获得顶级分类列表
     * @return
     */
    @GetMapping("getTopList")
    public CommonResult getTopList() {
        return CommonResult.success(industryService.getTopIndustryList());
    }

    /**
     * 获得当前分类下的子分类列表
     * 如果是默认的，则查询顶级分类
     * @param industryId
     * @return
     */
    @GetMapping("children/{industryId}")
    public CommonResult getChildrenIndustryList(
            @PathVariable("industryId") String industryId) {

        List<Industry> list = industryService.getChildrenIndustryList(industryId);
        return CommonResult.success(list);
    }


    /**
     * 修改分类节点
     * @param industry
     * @return
     */
    @PostMapping("updateNode")
    public CommonResult updateNode(@RequestBody Industry industry) {
        industryService.updateIndustry(industry);
       // resetRedisIndustry(industry);
        return CommonResult.success();
    }

    @DeleteMapping("deleteNode/{industryId}")
    public CommonResult deleteNode(@PathVariable("industryId") String industryId) {

        // 判断如果是一级或二级节点，则需要保证没有子节点才能删除，三级节点可以直接删除
        Industry temp = industryService.getById(industryId);
        if (temp.getLevel() == 1 || temp.getLevel() == 2) {
            // 查询该节点下是否含有子节点
            long counts = industryService.getChildrenIndustryCounts(industryId);
            if (counts > 0) {
                return CommonResult.error("请保证该节点下无任何子节点后再删除！！！");
            }
        }

        // resetRedisIndustry(temp);
        industryService.removeById(industryId);


        return CommonResult.success();
    }


    private void resetRedisIndustry(Industry industry) {
        if (industry.getLevel() == 1) {
            // 一级节点的增删改，则删除Redis中现有的TOP_INDUSTRY_LIST
            redis.del(TOP_INDUSTRY_LIST);

            // 删除之后，再次把最新的数据设置到Redis中
            List<Industry> topIndustryList = industryService.getTopIndustryList();
            redis.set(TOP_INDUSTRY_LIST, GsonUtils.object2String(topIndustryList));

            // 缓存双删
            try {
                Thread.sleep(200);
                redis.del(TOP_INDUSTRY_LIST);
                redis.set(TOP_INDUSTRY_LIST, GsonUtils.object2String(topIndustryList));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else if (industry.getLevel() == 3) {

            // 根据当前三级节点的id，获得对应的一级节点id
            String topIndustryId = industryService.getTopIndustryId(industry.getId());

            // 三级节点的增删改，则删除Redis中现有的THIRD_INDUSTRY_LIST
            String thirdKey = THIRD_INDUSTRY_LIST + ":byTopId:" + topIndustryId;
            redis.del(thirdKey);

            // 删除之后，再次把最新的数据设置到Redis中
            List<Industry> thirdIndustryList = industryService.getThirdListByTop(topIndustryId);
            redis.set(thirdKey, GsonUtils.object2String(thirdIndustryList));

            // 缓存双删
            try {
                Thread.sleep(300);
                redis.del(thirdKey);
                redis.set(thirdKey, GsonUtils.object2String(thirdIndustryList));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        // 新增与修改二级节点，没有任何影响，因为在该基础上的三级节点的总数并没有增减。
        // 删除二级节点，必须先删除三级节点，如此则会删除对应top下的所有三级列表，所以也无需操作
    }



    @PostMapping("refreshIndustryCache")
    public CommonResult refreshIndustryCache(){
        //计算明天凌晨的时间
        LocalDateTime futureTime = LocalDateUtils.parseLocalDateTime(LocalDateUtils.getTomorrow() + " 03:00:00", LocalDateUtils.DATETIME_PATTERN);
        
        //计算现在到明天凌晨的时间差
        Long publishTime = LocalDateUtils.getChronoUnitBetween(LocalDateTime.now(), futureTime, ChronoUnit.MILLIS, true);
        // int time = publishTime.intValue();
        Integer time=10*1000;
        rabbitTemplate.convertAndSend(
                MQDelayConfig_industry.DELAY_EXCHANGE,
                MQDelayConfig_industry.DELAY_ROUTING_KEY,
                "refresh",
                MQDelayConfig_industry.messagePostProcessor(time) // 延时10秒
                );
        log.info("延时消息发送成功，时间为：{}", LocalDateTime.now());
        return CommonResult.success();
    }
}
