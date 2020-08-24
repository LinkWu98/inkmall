package cn.link.inkmall.oms.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: Link
 * @Date: 2020/6/7 1:22
 * @Version 1.0
 */
@Configuration
public class RabbitConfig {

    /**
     * 声明延时队列
     * @return
     */
    @Bean
    public Queue ttlQueue() {
        Map<String, Object> arguments = new HashMap<>();
//         生存时间，单位：毫秒
        arguments.put("x-message-ttl", 60000);
//         绑定死信交换机
        arguments.put("x-dead-letter-exchange", "order-exchange");
//         死信路由key
        arguments.put("x-dead-letter-routing-key", "order.turnoff");
        return new Queue("order-timeout-queue", true, false, false, arguments);
    }

    /**
     * 延时队列绑定交换机
     */
    @Bean
    public Binding ttlBinding() {

        return new Binding("order-timeout-queue", Binding.DestinationType.QUEUE, "order-exchange", "order.timeout", null);

    }

    /**
     * 声明死信队列
     */
    @Bean
    public Queue deadQueue() {

        return new Queue("order-turnoff-queue", true, false, false);

    }

    /**
     * 死信队列绑定交换机
     */
    @Bean
    public Binding deadBinding() {

        return new Binding("order-turnoff-queue", Binding.DestinationType.QUEUE, "order-exchange", "order.turnoff", null);

    }

}
