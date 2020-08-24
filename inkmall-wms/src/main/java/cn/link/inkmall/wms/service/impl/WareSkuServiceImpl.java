package cn.link.inkmall.wms.service.impl;

import cn.link.inkmall.common.bean.PageParamVo;
import cn.link.inkmall.common.bean.PageResultVo;
import cn.link.inkmall.wms.entity.WareSkuEntity;
import cn.link.inkmall.wms.mapper.WareSkuMapper;
import cn.link.inkmall.wms.service.WareSkuService;
import cn.link.inkmall.wms.vo.OrderLockWareSkuVo;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;


@Service("wareSkuService")
public class WareSkuServiceImpl extends ServiceImpl<WareSkuMapper, WareSkuEntity> implements WareSkuService {

    private static final String WARE_STOCK_LOCK_PREFIX = "wms:stock:";

    private static final String WARE_ORDER_INFO_PREFIX = "wms:orderInfo:";

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private WareSkuMapper wareSkuMapper;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Override
    public PageResultVo queryPage(PageParamVo paramVo) {
        IPage<WareSkuEntity> page = this.page(
                paramVo.getPage(),
                new QueryWrapper<WareSkuEntity>()
        );

        return new PageResultVo(page);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void checkAndLockStock(List<OrderLockWareSkuVo> orderLockWareSkuVos, String orderToken) {

        if (CollectionUtils.isEmpty(orderLockWareSkuVos)) {
            return;
        }

        if (StringUtils.isEmpty(orderToken)) {
            return;
        }

        //验库存 + 锁库存
        orderLockWareSkuVos.forEach(orderLockWareSkuVo -> {
            //验库存和锁库存之间要加锁（当前 sku 的锁即可），保证线程安全（否则并发请求处理同一资源的情况下会出现错误）
            Long skuId = orderLockWareSkuVo.getSkuId();
            RLock fairLock = redissonClient.getFairLock(WARE_STOCK_LOCK_PREFIX + orderLockWareSkuVo.getSkuId());

            fairLock.lock();
            // 验库存（查询） ： 遍历集合，通过 skuId 和 count 查询表中是否有库存
            Integer count = orderLockWareSkuVo.getCount();
            // select * from wms_ware_sku where sku_id = x and stock - stock_locked - count > 0
            List<WareSkuEntity> wareSkuEntities = wareSkuMapper.getWareSkuBySkuIdAndCount(skuId, count);
            if (CollectionUtils.isEmpty(wareSkuEntities)) {
                //如果查询不到就没有库存
                orderLockWareSkuVo.setLocked(false);
            } else {
                // 查询到，锁库存（更新） ： 指定仓库设置库存锁定
                // 一般电商能指定距离收货地址最近的仓库，这里就指定第一个仓库吧
                Long wareSkuId = wareSkuEntities.get(0).getId();
                // update wms_ware_sku set stock_locked = stock_locked + count where id = x
                Integer result = wareSkuMapper.lockStockByWareSkuIdAndCount(wareSkuId, count);
                if (result == 1) {
                    orderLockWareSkuVo.setLocked(true);
                    orderLockWareSkuVo.setWareSkuId(wareSkuId);
                } else {
                    orderLockWareSkuVo.setLocked(false);
                }

            }
            fairLock.unlock();

        });

        //要整体校验库存锁定，有一个没有锁定就全部回滚
        boolean result = orderLockWareSkuVos.stream().anyMatch(OrderLockWareSkuVo::getLocked);
        if (!result) {
            orderLockWareSkuVos
                    .stream()
                    //回滚锁定的商品
                    .filter(OrderLockWareSkuVo::getLocked)
                    .forEach(orderLockWareSkuVo -> {
                        wareSkuMapper.rollbackStockLocked(orderLockWareSkuVo.getWareSkuId(), orderLockWareSkuVo.getCount());
                        orderLockWareSkuVo.setLocked(false);
                    });

            //直接返回
            return;
        }

        //将库存信息存于缓存中，用于订单超时回滚锁定的库存，订单支付完毕也需要更改库存信息
        stringRedisTemplate.opsForValue().set(WARE_ORDER_INFO_PREFIX + orderToken, JSON.toJSONString(orderLockWareSkuVos));

        //发送延时消息解锁库存，解决库存锁定完毕但服务器宕机导致订单生成失败，库存无法解锁的问题
        rabbitTemplate.convertAndSend("order-exchange", "ware.timeout", orderToken);

    }

}