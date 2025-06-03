package com.zack.mq;

import com.rabbitmq.client.Channel;
import com.zack.base.BaseInfoProperties;
import com.zack.domain.Industry;
import com.zack.service.IndustryService;
import com.zack.utils.GsonUtils;
import com.zack.utils.SMSUtils;
import com.zack.vo.TopIndustryWithThirdListVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
public class MQDelayConsumer extends BaseInfoProperties {
    @Autowired
    private IndustryService industryService;

    @RabbitListener(queues = {MQDelayConfig_industry.DELAY_QUEUE})
    public void receive(String payload, Message message, Channel channel) throws Exception {
        try {
            //获取routerkey
            String routingKey = message.getMessageProperties().getReceivedRoutingKey();
            if (routingKey.equals(MQDelayConfig_industry.DELAY_ROUTING_KEY)) {
                log.info("收到延时消息：" + payload, LocalDateTime.now());

                //删除一级节点缓存数据
                redis.del(TOP_INDUSTRY_LIST);
                //查询数据库
                List<Industry> topIndustryList = industryService.getTopIndustryList();
                //更新缓存数据
                redis.set(TOP_INDUSTRY_LIST, GsonUtils.object2String(topIndustryList));


                //删除三级节点的缓存数据
                String thirdKeyMulti= THIRD_INDUSTRY_LIST + ":byTopId:";
                redis.allDel(thirdKeyMulti);

                // 删除之后，再次把最新的数据设置到Redis中
                List<TopIndustryWithThirdListVO> thirdIndustryList = industryService.getAllThirdIndustryList();

                //更新缓存数据
                for (TopIndustryWithThirdListVO topIndustryWithThirdListVO : thirdIndustryList) {
                    String thirdKey = THIRD_INDUSTRY_LIST + ":byTopId:" + topIndustryWithThirdListVO.getTopId();
                    redis.set(thirdKey, GsonUtils.object2String(topIndustryWithThirdListVO.getThirdIndustryList()));
                }

            }
            //手动消费消息
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            //手动拒绝消息
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
            throw new RuntimeException(e);
        }
    }
}
