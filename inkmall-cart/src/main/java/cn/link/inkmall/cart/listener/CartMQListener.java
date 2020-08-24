package cn.link.inkmall.cart.listener;

import cn.link.inkmall.cart.feign.PmsFeign;
import cn.link.inkmall.pms.entity.SkuEntity;
import com.alibaba.fastjson.JSON;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @Author: Link
 * @Date: 2020/6/5 11:52
 * @Version 1.0
 */
@Component
public class CartMQListener {

    private static final String CURRENT_PRICE_PREFIX = "cart:currentPrice:";

    private static final String CART_PREFIX = "cart:";

    @Autowired
    PmsFeign pmsFeign;

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    /**
     * 监听商品的修改，然后修改缓存中对应商品的实时价格
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "cart.currentPrice.queue", durable = "true"),
            exchange = @Exchange(value = "pms.item.exchange", type = ExchangeTypes.TOPIC, ignoreDeclarationExceptions = "true"),
            key = {"item.update"}
    ))
    public void currentPriceListener(Long spuId, Channel channel, Message message) {

        if (spuId == null) {
            return;
        }

        List<SkuEntity> skuEntities = pmsFeign.getSkuBySpuId(spuId).getData();
        if (CollectionUtils.isEmpty(skuEntities)) {

            skuEntities.forEach(skuEntity -> {
                BigDecimal currentPrice = skuEntity.getPrice();
                if (currentPrice != null) {
                    stringRedisTemplate.opsForValue().set(CURRENT_PRICE_PREFIX + skuEntity.getId(), currentPrice.toString());
                }
            });

        }


    }

    /**
     * 删除购物车监听器
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "cart-delete-queue", durable = "true"),
            exchange = @Exchange(name = "order-exchange", type = ExchangeTypes.TOPIC, ignoreDeclarationExceptions = "true"),
            key = {"cart.delete"}
    ))
    public void listenAndDeleteCart(Map<String ,Object> deleteCartInfoMap, Channel channel, Message message) throws IOException {

        Long userId = (Long) deleteCartInfoMap.get("userId");
        String skuIds = (String) deleteCartInfoMap.get("skuIds");
        List<String> skuIdList = JSON.parseArray(skuIds, String.class);
        BoundHashOperations<String, Object, Object> cartOps = stringRedisTemplate.boundHashOps(CART_PREFIX + userId);
        //可变参数，必须是数组而不是集合哦
        cartOps.delete(skuIdList.toArray());

        try {
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            e.printStackTrace();
            if (message.getMessageProperties().getRedelivered()) {
                channel.basicReject(message.getMessageProperties().getDeliveryTag(), false);
            } else {
                channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
            }
        }


    }

}
