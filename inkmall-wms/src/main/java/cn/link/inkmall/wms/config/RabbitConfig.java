package cn.link.inkmall.wms.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: Link
 * @Date: 2020/6/8 14:20
 * @Version 1.0
 */
@Configuration
public class RabbitConfig {

    /**
     * 声明库存延时队列
     */
    @Bean
    public Queue stockTtlQueue() {

        Map<String, Object> arguments = new HashMap<>();
//         生存时间，单位：毫秒
        arguments.put("x-message-ttl", 90000);
//         绑定死信交换机
        arguments.put("x-dead-letter-exchange", "order-exchange");
//         死信路由key
        arguments.put("x-dead-letter-routing-key", "ware.rollback");
        return new Queue("ware-ttl-queue", true, false, false, arguments);
    }

    /**
     * 库存延时队列绑定已有的交换机
     * @return
     */
    @Bean
    public Binding stockTtlBinding() {

        return new Binding("ware-ttl-queue", Binding.DestinationType.QUEUE, "order-exchange", "ware.timeout", null);

    }


    //死信队列就使用已有的回滚库存的队列即可
    //死信队列绑定交换机已经声明了

}
