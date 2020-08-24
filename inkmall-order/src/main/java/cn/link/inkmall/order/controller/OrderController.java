package cn.link.inkmall.order.controller;

import cn.link.inkmall.common.bean.ResponseVo;
import cn.link.inkmall.common.exception.InkmallException;
import cn.link.inkmall.oms.entity.OrderEntity;
import cn.link.inkmall.oms.vo.OrderSubmitVo;
import cn.link.inkmall.order.config.AlipayTemplate;
import cn.link.inkmall.order.interceptor.LoginInterceptor;
import cn.link.inkmall.order.service.OrderService;
import cn.link.inkmall.order.vo.OrderConfirmVo;
import cn.link.inkmall.order.vo.PayAsyncVo;
import cn.link.inkmall.order.vo.PayVo;
import cn.link.inkmall.wms.vo.OrderLockWareSkuVo;
import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayApiException;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RCountDownLatch;
import org.redisson.api.RSemaphore;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Author: Link
 * @Date: 2020/6/5 13:56
 * @Version 1.0
 */
@RequestMapping("order")
@RestController
public class OrderController {

    private final String SECKILL_PREFIX = "seckill:stock:";

    private final String SECKILL_LOCK_PREFIX = "seckill:lock:";

    private final String SECKILL_ORDER_LOCK_PREFIX = "seckill:countDown:";

    @Autowired
    AlipayTemplate alipayTemplate;

    @Autowired
    OrderService orderService;

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Autowired
    RedissonClient redissonClient;


    /**
     * 确认订单
     */
    @PostMapping("confirm")
    public ResponseVo<OrderConfirmVo> confirmOrder() {

        OrderConfirmVo orderConfirmVo = orderService.confirmOrder();

        return ResponseVo.ok(orderConfirmVo);

    }

    /**
     * 提交订单
     */
    @PostMapping("submit")
    public ResponseVo submitOrder(@RequestBody OrderSubmitVo orderSubmitVo) {

        OrderEntity orderEntity = orderService.submitOrder(orderSubmitVo);

        //提交订单成功后，调用支付宝接口生成表单让用户付款
        PayVo payVo = new PayVo();
        payVo.setTotal_amount(orderEntity.getPayAmount().toString());
        payVo.setOut_trade_no(orderEntity.getOrderSn());
        payVo.setSubject("迎客商城支付系统");
        payVo.setBody("body");

        try {
            String form = alipayTemplate.pay(payVo);
            //返回表单给前端显示
            return ResponseVo.ok(form);
        } catch (AlipayApiException e) {
            e.printStackTrace();
            throw new InkmallException("生成订单失败，请重新生成订单");
        }

    }

    /**
     * 支付成功后的回调(修改订单状态，修改库存)
     * payAsyncVo 封装了回调携带参数的对象
     */
    @PostMapping("pay/success")
    public ResponseVo handlePaySuccess(@RequestBody PayAsyncVo payAsyncVo) {

        String orderToken = payAsyncVo.getOut_trade_no();

        //发送消息修改订单状态
        rabbitTemplate.convertAndSend("order-exchange", "pay.success", orderToken);

        return ResponseVo.ok("支付成功！");

    }


    /**
     * 秒杀
     */
    @PostMapping("seckill/{skuId}")
    public ResponseVo seckill(@PathVariable("skuId") Long skuId) {

        //校验库存
        String stockStr = stringRedisTemplate.opsForValue().get(SECKILL_PREFIX + skuId);
        if (StringUtils.isEmpty(stockStr)) {
            throw new InkmallException("抱歉，该商品已经被抢光啦");
        }
        int stock = Integer.parseInt(stockStr);
        if (stock == 0) {
            throw new InkmallException("抱歉，该商品已经被抢光啦");
        }

        //减库存，使用信号量(锁当前skuId的商品即可)，保证原子性
        RSemaphore semaphore = redissonClient.getSemaphore(SECKILL_LOCK_PREFIX + skuId);
        semaphore.trySetPermits(stock);
        //拿到锁后再次校验库存
        stockStr = stringRedisTemplate.opsForValue().get(SECKILL_PREFIX + skuId);
        stock = Integer.parseInt(stockStr);
        if (StringUtils.isEmpty(stockStr) || stock == 0) {
            throw new InkmallException("抱歉，该商品已经被抢光啦");
        }

        //减库存
        stringRedisTemplate.opsForValue().set(SECKILL_PREFIX + skuId, String.valueOf(stock--));

        //解锁
        semaphore.release();

        //异步锁定库存，保存订单
        OrderLockWareSkuVo orderLockWareSkuVo = new OrderLockWareSkuVo();
        orderLockWareSkuVo.setSkuId(skuId);
        String orderToken = IdWorker.getTimeId();
        orderLockWareSkuVo.setOrderToken(orderToken);
        orderLockWareSkuVo.setUserId(LoginInterceptor.getUserInfo().getUserId());

        rabbitTemplate.convertAndSend("order-exchange", "seckill.success", JSON.toJSONString(orderLockWareSkuVo));

        //设置 countDown 锁，再秒杀订单生成后才能查询订单
        RCountDownLatch countDownLatch = redissonClient.getCountDownLatch(SECKILL_ORDER_LOCK_PREFIX + LoginInterceptor.getUserInfo().getUserId());
        countDownLatch.trySetCount(1);

        return ResponseVo.ok("恭喜您，秒杀成功！");

    }

    /**
     * 查看订单
     */
    @GetMapping("order")
    public ResponseVo<List<OrderEntity>> getOrder() throws InterruptedException {

        Long userId = LoginInterceptor.getUserInfo().getUserId();
        RCountDownLatch countDownLatch = redissonClient.getCountDownLatch(SECKILL_ORDER_LOCK_PREFIX + userId);
        if (countDownLatch.isExists()) {
            //阻塞待订单生成完毕后放行
            countDownLatch.await();
        }

        //查询订单并返回

        return ResponseVo.ok();

    }

}
