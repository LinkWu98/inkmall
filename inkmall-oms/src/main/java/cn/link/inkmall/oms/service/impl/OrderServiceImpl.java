package cn.link.inkmall.oms.service.impl;

import cn.link.inkmall.common.bean.PageParamVo;
import cn.link.inkmall.common.bean.PageResultVo;
import cn.link.inkmall.common.exception.InkmallException;
import cn.link.inkmall.oms.entity.OrderEntity;
import cn.link.inkmall.oms.entity.OrderItemEntity;
import cn.link.inkmall.oms.feign.PmsFeign;
import cn.link.inkmall.oms.feign.SmsFeign;
import cn.link.inkmall.oms.mapper.OrderMapper;
import cn.link.inkmall.oms.service.OrderItemService;
import cn.link.inkmall.oms.service.OrderService;
import cn.link.inkmall.oms.vo.OrderItemVo;
import cn.link.inkmall.oms.vo.OrderSubmitVo;
import cn.link.inkmall.pms.entity.*;
import cn.link.inkmall.sms.entity.SkuBoundsEntity;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;


@Service("orderService")
public class OrderServiceImpl extends ServiceImpl<OrderMapper, OrderEntity> implements OrderService {

    @Autowired
    private OrderItemService orderItemService;

    @Autowired
    private PmsFeign pmsFeign;

    @Autowired
    private SmsFeign smsFeign;

    @Autowired
    private ThreadPoolExecutor threadPool;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Override

    public PageResultVo queryPage(PageParamVo paramVo) {
        IPage<OrderEntity> page = this.page(
                paramVo.getPage(),
                new QueryWrapper<OrderEntity>()
        );

        return new PageResultVo(page);
    }

    /**
     * 订单保存（出现异常就回滚）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderEntity saveOrder(OrderSubmitVo orderSubmitVo) {

        /**
         * 一系列非空校验
         */
        if (orderSubmitVo == null) {
            throw new InkmallException("数据不能为空！");
        }

        String token = orderSubmitVo.getOrderToken();
        if (!StringUtils.isNotBlank(token)) {
            throw new InkmallException("订单编号token不能为空！");
        }

        List<OrderItemVo> orderItemVos = orderSubmitVo.getOrderItemVos();
        if (CollectionUtils.isEmpty(orderItemVos)) {
            throw new InkmallException("商品数据不能为空！");
        }

        OrderEntity orderEntity = new OrderEntity();
        //设置订单信息，保存
        this.setOrderEntity(orderSubmitVo, orderEntity);
        this.save(orderEntity);

        //设置订单详情（orderItemVo -> orderItem存入数据库）
        List<OrderItemEntity> orderItemEntities = orderItemVos.stream().map(orderItemVo -> {
            //只需获取skuId 、 商品数量，其他都查询获取最新数据存入
            Long skuId = orderItemVo.getSkuId();
            OrderItemEntity orderItemEntity = new OrderItemEntity();
            orderItemEntity.setOrderId(orderEntity.getId());
            orderItemEntity.setSkuId(skuId);
            orderItemEntity.setSkuQuantity(orderItemVo.getCount());

            CompletableFuture<SkuEntity> skuEntityFuture = CompletableFuture.supplyAsync(() -> {
                /**
                 * skuEntity信息
                 */

                SkuEntity skuEntity = pmsFeign.querySkuById(skuId).getData();
                if (skuEntity != null) {
                    orderItemEntity.setSkuName(skuEntity.getName());
                    orderItemEntity.setSkuPrice(skuEntity.getPrice());
                    orderItemEntity.setCategoryId(skuEntity.getCategoryId());
                }

                return skuEntity;

            }, threadPool);

            CompletableFuture<Void> skuPicFuture = CompletableFuture.runAsync(() -> {
                /**
                 * sku默认图片
                 */
                SkuImagesEntity skuImagesEntity = pmsFeign.getDefaultImageBySkuId(skuId).getData();
                if (skuImagesEntity != null) {
                    orderItemEntity.setSkuPic(skuImagesEntity.getUrl());
                }
            }, threadPool);

            CompletableFuture<Void> saleAttrFuture = CompletableFuture.runAsync(() -> {
                /**
                 * 销售属性组合（JSON）
                 */
                List<SkuAttrValueEntity> skuAttrValueEntities = pmsFeign.getSkuAttrValuesBySkuId(skuId).getData();
                if (!CollectionUtils.isEmpty(skuAttrValueEntities)) {
                    String skuAttrsVals = JSON.toJSONString(skuAttrValueEntities);
                    orderItemEntity.setSkuAttrsVals(skuAttrsVals);
                }
            }, threadPool);

            CompletableFuture<Void> boundsFuture = CompletableFuture.runAsync(() -> {
                /**
                 * 积分和成长值信息
                 */
                SkuBoundsEntity skuBoundsEntity = smsFeign.getBoundsBySkuId(skuId).getData();
                if (skuBoundsEntity != null) {
                    orderItemEntity.setGiftGrowth(skuBoundsEntity.getGrowBounds().intValue());
                    orderItemEntity.setGiftIntegration(skuBoundsEntity.getBuyBounds().intValue());
                }
            }, threadPool);

            CompletableFuture<Void> skuInfoFuture = skuEntityFuture.thenAcceptAsync(skuEntity -> {
                /**
                 * spu信息
                 */
                if (skuEntity != null) {
                    Long spuId = skuEntity.getSpuId();
                    SpuEntity spuEntity = pmsFeign.querySpuById(spuId).getData();
                    orderItemEntity.setSpuId(spuId);
                    orderItemEntity.setSpuName(spuEntity.getName());
                }

            }, threadPool);

            CompletableFuture<Void> spuDescFuture = skuEntityFuture.thenAcceptAsync(skuEntity -> {
                /**
                 * spu desc信息
                 */
                if (skuEntity != null) {
                    Long spuId = skuEntity.getSpuId();
                    SpuDescEntity spuDescEntity = pmsFeign.querySpuDescById(spuId).getData();
                    if (spuDescEntity != null) {
                        orderItemEntity.setSpuPic(spuDescEntity.getDecript());
                    }
                }

            }, threadPool);

            CompletableFuture<Void> brandFuture = skuEntityFuture.thenAcceptAsync(skuEntity -> {
                /**
                 * brand信息
                 */
                if (skuEntity != null) {
                    Long brandId = skuEntity.getBrandId();
                    BrandEntity brandEntity = pmsFeign.queryBrandById(brandId).getData();
                    if (brandEntity != null) {
                        orderItemEntity.setSpuBrand(brandEntity.getName());
                    }
                }

            }, threadPool);

            CompletableFuture.allOf(skuPicFuture, saleAttrFuture, boundsFuture, skuInfoFuture, spuDescFuture, brandFuture).join();

            return orderItemEntity;

        }).collect(Collectors.toList());

        //批量保存订单详情
        orderItemService.saveBatch(orderItemEntities);


        //获得的总积分和总成长值 并存入数据库订单信息中（每个商品的积分和成长值叠加）
        Integer totalGrowth = orderItemEntities.stream().map(OrderItemEntity::getGiftGrowth).reduce((a, b) -> a + b).get();
        Integer totalIntegration = orderItemEntities.stream().map(OrderItemEntity::getGiftIntegration).reduce((a, b) -> a + b).get();
        orderEntity.setGrowth(totalGrowth);
        orderEntity.setIntegration(totalIntegration);
        this.update(orderEntity, new QueryWrapper<OrderEntity>().eq("id", orderEntity.getId()));

        //生成订单后发送消息到延时队列，超时就关闭订单, 回滚库存
        rabbitTemplate.convertAndSend("order-exchange", "order.timeout", token);


        return orderEntity;
    }

    private void setOrderEntity(OrderSubmitVo orderSubmitVo, OrderEntity orderEntity) {

        //保存订单
        orderEntity.setUserId(orderSubmitVo.getUserId());
        orderEntity.setUsername(orderSubmitVo.getUsername());
        orderEntity.setOrderSn(orderSubmitVo.getOrderToken());
        orderEntity.setCreateTime(new Date());
        orderEntity.setTotalAmount(orderSubmitVo.getTotalPrice());
        //应付金额 = 总价 - 积分抵扣 + 运费
        orderEntity.setPayAmount(orderSubmitVo.getTotalPrice().subtract(orderSubmitVo.getUseIntegration().divide(new BigDecimal(100))).add(new BigDecimal(10)));
        orderEntity.setFreightAmount(new BigDecimal(10));
        orderEntity.setIntegrationAmount(orderSubmitVo.getUseIntegration().divide(new BigDecimal(100)));
        orderEntity.setPayType(orderSubmitVo.getPayType());
        orderEntity.setSourceType(orderSubmitVo.getSourceType());
        orderEntity.setDeliveryCompany(orderSubmitVo.getDeliveryCompany());
        orderEntity.setAutoConfirmDay(14);
        orderEntity.setReceiverName(orderSubmitVo.getReceiverName());
        orderEntity.setReceiverPhone(orderSubmitVo.getReceiverPhone());
        orderEntity.setReceiverPostCode(orderSubmitVo.getPostCode());
        orderEntity.setReceiverProvince(orderSubmitVo.getProvince());
        orderEntity.setReceiverCity(orderSubmitVo.getCity());
        orderEntity.setReceiverRegion(orderSubmitVo.getRegion());
        orderEntity.setReceiverAddress(orderSubmitVo.getAddress());
        orderEntity.setConfirmStatus(0);
        orderEntity.setUseIntegration(orderSubmitVo.getUseIntegration().intValue());
        orderEntity.setStatus(0);
        //TODO 发票信息

    }

}