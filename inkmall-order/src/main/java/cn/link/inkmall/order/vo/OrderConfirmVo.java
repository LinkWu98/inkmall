package cn.link.inkmall.order.vo;

import cn.link.inkmall.oms.vo.OrderItemVo;
import cn.link.inkmall.ums.entity.UserAddressEntity;
import lombok.Data;

import java.util.List;

/**
 * 订单确认页面数据模型
 * @Author: Link
 * @Date: 2020/6/5 12:36
 * @Version 1.0
 */
@Data
public class OrderConfirmVo {

    /**
     * 用户地址信息集合
     */
    private List<UserAddressEntity> addresses;

    /**
     * 用户购物积分
     */
    private Integer bounds;

    /**
     * 订单商品集合
     */
    private List<OrderItemVo> orderItems;

    /**
     * 防止表单重复提交字段
     */
    private String orderToken;


}
