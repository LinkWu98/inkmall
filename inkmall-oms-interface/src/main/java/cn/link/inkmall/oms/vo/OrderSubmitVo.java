package cn.link.inkmall.oms.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 订单提交的数据模型
 *
 * @Author: Link
 * @Date: 2020/6/6 22:35
 * @Version 1.0
 */
@Data
public class OrderSubmitVo {

    private String orderToken;

    private Long userId;

    private String username;

    private List<OrderItemVo> orderItemVos;

    /**
     * 收货人姓名
     */
    private String receiverName;

    /**
     * 收货人电话
     */
    private String receiverPhone;

    /**
     * 邮编
     */
    private String postCode;

    private String province;

    private String city;

    private String region;

    private String address;

    /**
     * 订单总额
     */
    private BigDecimal totalPrice;

    /**
     * 应付总额
     */
    private BigDecimal payAmount;

    /**
     * 可获得的成长值
     */
    private Integer growth;

    /**
     * 可获得的积分
     */
    private Integer integration;

    /**
     * 下单时使用的积分
     */
    private BigDecimal useIntegration;

    /**
     * 积分抵扣金额 100 积分抵 1 元
     */
    private BigDecimal integrationAmount;

    /**
     * 支付方式【1->支付宝；2->微信；3->银联； 4->货到付款；】
     */
    private Integer payType;

    /**
     * 订单来源[0->PC订单；1->app订单]
     */
    private Integer sourceType;

    /**
     * 物流公司(配送方式)
     */
    private String deliveryCompany;

}
