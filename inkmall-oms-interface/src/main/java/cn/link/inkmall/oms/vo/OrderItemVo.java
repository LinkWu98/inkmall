package cn.link.inkmall.oms.vo;

import cn.link.inkmall.pms.entity.SkuAttrValueEntity;
import cn.link.inkmall.sms.vo.ItemSaleVo;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 订单确认页面商品模型
 * @Author: Link
 * @Date: 2020/6/5 13:49
 * @Version 1.0
 */
@Data
public class OrderItemVo {

    private Long skuId;

    private Integer count;

    private String title;

    private String defaultImage;

    private BigDecimal price;

    private BigDecimal weight;
    /**
     * 销售属性
     */
    private List<SkuAttrValueEntity> saleAttrs;

    /**
     * 营销信息
     */
    private List<ItemSaleVo> sales;

    /**
     * 库存信息
     */
    private Boolean store;

}
