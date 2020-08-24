package cn.link.inkmall.wms.listener;

import cn.link.inkmall.wms.mapper.WareSkuMapper;
import cn.link.inkmall.wms.vo.OrderLockWareSkuVo;
import com.alibaba.fastjson.JSON;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.List;

/**
 * @Author: Link
 * @Date: 2020/6/7 1:04
 * @Version 1.0
 */
@Component
public class WareListener {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private WareSkuMapper wareSkuMapper;

    private static final String WARE_ORDER_INFO_PREFIX = "wms:orderInfo:";

    /**
     * 监听消息，获取缓存中库存信息，回滚库存
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "ware-rollback-queue", durable = "true"),
            exchange = @Exchange(name = "order-exchange", ignoreDeclarationExceptions = "true", type = ExchangeTypes.TOPIC),
            key = {"ware.rollback"}
    ))
    public void listenAndRollbackStock(String orderToken, Channel channel, Message message) throws IOException {

        try {

            String wareInfoJsonStr = stringRedisTemplate.opsForValue().get(WARE_ORDER_INFO_PREFIX + orderToken);
            List<OrderLockWareSkuVo> orderLockWareSkuVos = JSON.parseArray(wareInfoJsonStr, OrderLockWareSkuVo.class);
            if (!CollectionUtils.isEmpty(orderLockWareSkuVos)) {
                orderLockWareSkuVos.forEach(orderLockWareSkuVo -> {
                    wareSkuMapper.rollbackStockLocked(orderLockWareSkuVo.getWareSkuId(), orderLockWareSkuVo.getCount());
                });
            }
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


    /**
     * 支付成功后减库存
     */
    public void listenAndMinusStock(String orderToken, Channel channel, Message message) throws IOException {

        try {
            //获取缓存中的库存信息
            String wareLockStr = stringRedisTemplate.opsForValue().get(WARE_ORDER_INFO_PREFIX + orderToken);
            List<OrderLockWareSkuVo> lockWareSkuVos = JSON.parseArray(wareLockStr, OrderLockWareSkuVo.class);

            if (!CollectionUtils.isEmpty(lockWareSkuVos)) {
                lockWareSkuVos.forEach(orderLockWareSkuVo -> {
                    //减库存
                    wareSkuMapper.minusStock(orderLockWareSkuVo.getWareSkuId(), orderLockWareSkuVo.getCount());
                });

                //删除缓存
                stringRedisTemplate.delete(WARE_ORDER_INFO_PREFIX + orderToken);
            }

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
