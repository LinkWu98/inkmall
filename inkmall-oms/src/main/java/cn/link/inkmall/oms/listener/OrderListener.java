package cn.link.inkmall.oms.listener;

import cn.link.inkmall.oms.mapper.OrderMapper;
import com.rabbitmq.client.Channel;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @Author: Link
 * @Date: 2020/6/7 1:27
 * @Version 1.0
 */
@Component
public class OrderListener {

    @Autowired
    OrderMapper orderMapper;

    @Autowired
    RabbitTemplate rabbitTemplate;

    /**
     * 订单超时关闭订单
     */
    @RabbitListener(queues = {"order-turnoff-queue"})
    public void listenAndTurnOffOrder(String orderToken, Channel channel, Message message) throws IOException {

        try {


            //关闭订单（修改订单状态为 4）
            if (StringUtils.isNotBlank(orderToken)) {

                int result = orderMapper.closeOrder(orderToken);
                if (result == 1) {

                    //若关闭订单成功，还要发送消息回滚库存
                    rabbitTemplate.convertAndSend("order-exchange", "ware.rollback", orderToken);

                }

            }

            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            //业务出现异常
            if (message.getMessageProperties().getRedelivered()) {
                //若重新入过队就拒绝消息，消息会被废弃或进入该业务队列绑定的死信队列
                channel.basicReject(message.getMessageProperties().getDeliveryTag(), false);
            } else {
                //若未重新入过队就重试
                channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
            }

        }

    }

    /**
     * 监听支付订单成功后修改订单状态并发送消息修改库存
     */
    public void listenPaySuccess(String orderToken, Channel channel, Message message) throws IOException {
        try {
            int result = orderMapper.paySuccess(orderToken);
            if (result == 1) {
                //若订单状态修改成功，发送消息修改库存
                rabbitTemplate.convertAndSend("order-exchange", "ware.minus", orderToken);
            } else {
                //TODO 若状态修改失败，退款操作等等
            }
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);

        } catch (Exception e) {
            //业务出现异常
            if (message.getMessageProperties().getRedelivered()) {
                //若重新入过队就拒绝消息，消息会被废弃或进入该业务队列绑定的死信队列
                channel.basicReject(message.getMessageProperties().getDeliveryTag(), false);
            } else {
                //若未重新入过队就重试
                channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
            }

        }
    }

}
