package com.luyou.bi.bizmq;

import com.luyou.bi.constant.BIMQConstant;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @author 鹿又笑
 * @create 2024/7/26-15:11
 * @description
 */
public class BIMQInitMain {

    public static void main(String[] args) {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        try {
            Connection connection = connectionFactory.newConnection();
            Channel channel = connection.createChannel();
            channel.exchangeDeclare(BIMQConstant.EXCHANGENAME, "direct");
            channel.queueDeclare(BIMQConstant.QUEUENAME, true, false, false, null);
            channel.queueBind(BIMQConstant.QUEUENAME, BIMQConstant.EXCHANGENAME, BIMQConstant.ROUTINGKEY);
        } catch (IOException | TimeoutException e) {
            throw new RuntimeException(e);
        }
    }

}
