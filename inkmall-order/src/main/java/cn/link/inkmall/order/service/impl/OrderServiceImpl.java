package cn.link.inkmall.order.service.impl;

import cn.link.inkmall.cart.bean.CartItem;
import cn.link.inkmall.common.exception.InkmallException;
import cn.link.inkmall.oms.entity.OrderEntity;
import cn.link.inkmall.oms.vo.OrderItemVo;
import cn.link.inkmall.oms.vo.OrderSubmitVo;
import cn.link.inkmall.order.bean.UserInfo;
import cn.link.inkmall.order.feign.*;
import cn.link.inkmall.order.interceptor.LoginInterceptor;
import cn.link.inkmall.order.service.OrderService;
import cn.link.inkmall.order.vo.OrderConfirmVo;
import cn.link.inkmall.pms.entity.SkuEntity;
import cn.link.inkmall.ums.entity.UserEntity;
import cn.link.inkmall.wms.entity.WareSkuEntity;
import cn.link.inkmall.wms.vo.OrderLockWareSkuVo;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

/**
 * @Author: Link
 * @Date: 2020/6/5 13:58
 * @Version 1.0
 */
@Service
public class OrderServiceImpl implements OrderService {

    private static final String ORDER_TOKEN_PREFIX = "order:unrepeatable:";

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Autowired
    UmsFeign umsFeign;

    @Autowired
    CartFeign cartFeign;

    @Autowired
    PmsFeign pmsFeign;

    @Autowired
    SmsFeign smsFeign;

    @Autowired
    WmsFeign wmsFeign;

    @Autowired
    OmsFeign omsFeign;

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Autowired
    ThreadPoolExecutor threadPool;


    /**
     * 获取订单确认信息 (地址、购物车商品等)
     */
    @Override
    public OrderConfirmVo confirmOrder() {

        OrderConfirmVo orderConfirmVo = new OrderConfirmVo();

        // 获取用户 token 信息中的 userId(Spring拦截器)
        UserInfo userInfo = LoginInterceptor.getUserInfo();
        Long userId = userInfo.getUserId();
        // 远程调用，获取商品信息 (异步编排)
        // 1. 商品信息（不使用购物车中信息，防止数据不是最新造成的问题）
        CompletableFuture<List<CartItem>> cartItemFuture = CompletableFuture.supplyAsync(() -> {
            // 通过 userId 查询 redis 中保存的购物车
            List<CartItem> cartItems = cartFeign.getCheckedCartItemsByUserId(userId).getData();
            if (CollectionUtils.isEmpty(cartItems)) {
                throw new InkmallException("亲，您的购物车中不存在商品哦");
            }
            return cartItems;
        }, threadPool);

        CompletableFuture<Void> orderItemsFuture = cartItemFuture.thenAcceptAsync(cartItems -> {
            // 遍历购物车商品，转为 orderItemVo对象
            List<OrderItemVo> orderItems = cartItems.stream().map(cartItem -> {
                OrderItemVo orderItemVo = new OrderItemVo();
                // 获取购物车中已选中的商品的 skuId 和 count
                orderItemVo.setSkuId(cartItem.getSkuId());
                orderItemVo.setCount(cartItem.getCount());
                // pms、sms、wms接口查询商品信息
                CompletableFuture<Void> skuInfoFuture = CompletableFuture.runAsync(() -> {
                    SkuEntity skuEntity = pmsFeign.querySkuById(cartItem.getSkuId()).getData();
                    if (skuEntity != null) {
                        BeanUtils.copyProperties(skuEntity, orderItemVo);
                        orderItemVo.setWeight(new BigDecimal(skuEntity.getWeight()));
                    }
                }, threadPool);

                CompletableFuture<Void> salesFuture = CompletableFuture.runAsync(() -> {
                    orderItemVo.setSales(smsFeign.getItemSaleVo(cartItem.getSkuId()).getData());
                }, threadPool);

                CompletableFuture<Void> storeFuture = CompletableFuture.runAsync(() -> {
                    List<WareSkuEntity> wareSkuEntities = wmsFeign.getWareSkuEntitiesBySkuId(cartItem.getSkuId()).getData();
                    if (!CollectionUtils.isEmpty(wareSkuEntities)) {
                        orderItemVo.setStore(wareSkuEntities.stream().anyMatch(wareSkuEntity -> wareSkuEntity.getStock() - wareSkuEntity.getStockLocked() > 0));
                    }
                }, threadPool);

                CompletableFuture.allOf(skuInfoFuture, salesFuture, storeFuture).join();

                return orderItemVo;

            }).collect(Collectors.toList());

            orderConfirmVo.setOrderItems(orderItems);

        }, threadPool);

        // 2. 积分信息
        CompletableFuture<Void> boundsFuture = CompletableFuture.runAsync(() -> {
            UserEntity userEntity = umsFeign.queryUserById(userId).getData();
            if (userEntity != null) {
                orderConfirmVo.setBounds(userEntity.getIntegration());
            }
        }, threadPool);


        // 3. 地址信息
        CompletableFuture<Void> addressFuture = CompletableFuture.runAsync(() -> {
            orderConfirmVo.setAddresses(umsFeign.getAddressesByUserId(userId).getData());
        }, threadPool);

        // 4. 防止订单重复提交，设置唯一标识存入redis中
        CompletableFuture<Void> orderTokenFuture = CompletableFuture.runAsync(() -> {

            String orderToken = IdWorker.getTimeId();
            stringRedisTemplate.opsForValue().set(ORDER_TOKEN_PREFIX + orderToken, orderToken);
            orderConfirmVo.setOrderToken(orderToken);

        });

        CompletableFuture.allOf(orderItemsFuture, boundsFuture, addressFuture, orderTokenFuture).join();

        return orderConfirmVo;
    }

    @Override
    public OrderEntity submitOrder(OrderSubmitVo orderSubmitVo) {

        String token = orderSubmitVo.getOrderToken();
        if (!StringUtils.isNotBlank(token)) {
            throw new InkmallException("订单token不能为空！");
        }

        List<OrderItemVo> orderItemVos = orderSubmitVo.getOrderItemVos();
        if (CollectionUtils.isEmpty(orderItemVos)) {
            throw new InkmallException("商品列表不能为空！");
        }

        //1. 防重校验（存在就删除，开始业务，不存在就提示， 使用lua保证原子性）
        String luaScript = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
        Boolean flag = stringRedisTemplate.execute(new DefaultRedisScript<>(luaScript, Boolean.class), Arrays.asList(ORDER_TOKEN_PREFIX + token), token);
        //1.1 如果脚本执行判断为true 执行后返回 true，反之为false
        if (!flag) {
            //1.2如果为false就是重复提交，提示
            throw new InkmallException("请勿重复提交订单！");
        }

        //2. 价格校验（页面传递的总价和实时总价比较）
        BigDecimal totalPrice = orderSubmitVo.getTotalPrice();
        BigDecimal currentTotalPrice = orderItemVos.stream().map(orderItemVo -> {
            //获取每个送货清单中的skuId 查询实时价格 乘以 数量 再整体相加即为实时总价
            Long skuId = orderItemVo.getSkuId();
            SkuEntity skuEntity = pmsFeign.querySkuById(skuId).getData();
            BigDecimal currentPrice = skuEntity.getPrice();
            return currentPrice.multiply(new BigDecimal(orderItemVo.getCount()));
        }).reduce(BigDecimal::add).get();

        if (totalPrice.compareTo(currentTotalPrice) != 0) {
            throw new InkmallException("商品价格有变动，请重新刷新页面重试");
        }

        //3. 锁定库存
        List<OrderLockWareSkuVo> orderLockWareSkuVos = orderItemVos.stream().map(orderItemVo -> {
            OrderLockWareSkuVo orderLockWareSkuVo = new OrderLockWareSkuVo();
            orderLockWareSkuVo.setSkuId(orderItemVo.getSkuId());
            orderLockWareSkuVo.setCount(orderItemVo.getCount());
            return orderLockWareSkuVo;
        }).collect(Collectors.toList());
        //锁定库存成功后马上设置库存回滚的延时队列
        orderLockWareSkuVos = wmsFeign.checkAndLockStock(orderLockWareSkuVos, token).getData();
        //校验是否都锁定成功，锁定失败就返回数据给页面
        boolean result = orderLockWareSkuVos.stream().anyMatch(OrderLockWareSkuVo::getLocked);
        if (!result) {
            throw new InkmallException(JSON.toJSONString(orderLockWareSkuVos));
        }

        //4. 保存订单（MQ事务，出错就回滚库存）
        orderSubmitVo.setUsername(LoginInterceptor.getUserInfo().getUsername());
        OrderEntity orderEntity = null;
        try {
            //保存订单后马上设置订单失效的延时队列
            orderEntity = omsFeign.save(orderSubmitVo).getData();
        } catch (Exception e) {
            e.printStackTrace();
            //若保存订单异常，消息队列回滚库存（可能远程调用通信会出问题，因此要 在这里处理，oms处理不到这个错误也就无法回滚异常）
            rabbitTemplate.convertAndSend("order-exchange", "ware.rollback", token);
        }

        //5. 删除购物车（消息队列异步删除）
        //5.1 删除对应用户购物车的订单中的商品
        Map<String, Object> deletCartInfoMap = new HashMap<>();
        deletCartInfoMap.put("userId", LoginInterceptor.getUserInfo().getUserId());
        deletCartInfoMap.put("skuIds", JSON.toJSONString(orderItemVos.stream().map(OrderItemVo::getSkuId).collect(Collectors.toList())));
        rabbitTemplate.convertAndSend("order-exchange", "cart.delete", deletCartInfoMap);


        return orderEntity;
    }
}
