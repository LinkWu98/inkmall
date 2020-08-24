package cn.link.inkmall.mq.listener;

import cn.link.inkmall.mq.feign.UmsFeign;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

/**
 * @Author: Link
 * @Date: 2020/6/1 10:37
 * @Version 1.0
 */
@Component
public class UmsListener {

    @Autowired
    UmsFeign umsFeign;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "ums-code-queue", durable = "true"),
            exchange = @Exchange(name = "ums-code-exchange"),
            key = {"code.send"}
         )
    )
    public void codeListener(String phone, Channel channel, Message message) throws IOException {

        try {
             String code = UUID.randomUUID().toString().substring(0, 5);

            //发送验证码
            System.out.println("发送验证码:" + code);

            //远程调用 ums 保存验证码
            umsFeign.saveCode(code, phone);

            //确认消息
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);

        } catch (Exception e) {

            if (message.getMessageProperties().getRedelivered()) {
                //拒绝消息
                channel.basicReject(message.getMessageProperties().getDeliveryTag(), false);
            }else {
                //重新入队
                channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
            }

        }
    }


}
