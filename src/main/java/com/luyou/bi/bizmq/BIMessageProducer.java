package com.luyou.bi.bizmq;

import com.luyou.bi.constant.BIMQConstant;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author 鹿又笑
 * @create 2024/7/26-10:40
 * @description
 */
@Component
public class BIMessageProducer {

    @Resource
    private RabbitTemplate rabbitTemplate;

    /**
     *
     * @param message
     */
    public void sendMessage(String message) {
        // 发送消息
        rabbitTemplate.convertAndSend(BIMQConstant.EXCHANGENAME, BIMQConstant.ROUTINGKEY, message);
    }

}
