package cn.link.inkmall.order.service;

import cn.link.inkmall.oms.entity.OrderEntity;
import cn.link.inkmall.oms.vo.OrderSubmitVo;
import cn.link.inkmall.order.vo.OrderConfirmVo;

/**
 * @Author: Link
 * @Date: 2020/6/5 13:58
 * @Version 1.0
 */
public interface OrderService {
    /**
     * 获取订单确认信息 (地址、购物车商品等)
     */
    OrderConfirmVo confirmOrder();

    /**
     * 提交订单 (防重校验，价格校验，库存锁定，删除购物车商品，订单倒计时等)
     * @param orderSubmitVo
     */
    OrderEntity submitOrder(OrderSubmitVo orderSubmitVo);
}
