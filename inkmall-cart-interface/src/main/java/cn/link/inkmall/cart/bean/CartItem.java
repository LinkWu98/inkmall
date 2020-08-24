package cn.link.inkmall.cart.bean;

import cn.link.inkmall.pms.entity.SkuAttrValueEntity;
import cn.link.inkmall.sms.vo.ItemSaleVo;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 购物车商品模型
 * @Author: Link
 * @Date: 2020/6/1 23:44
 * @Version 1.0
 */
@Data
public class CartItem {

    private Long skuId;

    private String defaultImage;

    private String title;

    private BigDecimal price;

    private Integer count;

    private List<SkuAttrValueEntity> saleAttrs;

    private List<ItemSaleVo> sales;

    private Boolean store = false;

    /**
     * 商品的实时价格
     */
    private BigDecimal currentPrice;

    /**
     * 是否选中
     */
    private Boolean check = true;

}
