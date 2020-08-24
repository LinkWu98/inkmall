package cn.link.inkmall.item.vo;

import cn.link.inkmall.pms.entity.SkuImagesEntity;
import cn.link.inkmall.pms.vo.ItemAttrGroupVo;
import cn.link.inkmall.pms.vo.ItemAttrValueVo;
import cn.link.inkmall.pms.vo.ItemCategoryVo;
import cn.link.inkmall.sms.vo.ItemSaleVo;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 商品详情页 vo 对象
 * @Author: Link
 * @Date: 2020/5/31 11:15
 * @Version 1.0
 */
@Data
public class ItemVo {
    /**
     * spu信息
     */
    private String spuName;

    private List<String> descs;

    /**
     * sku信息
     */
    private Long skuId;

    private String title;

    private String subtitle;

    private BigDecimal price;

    private String defaultImage;

    private List<SkuImagesEntity> images;

    /**
     * spu下所有sku销售属性及属性值 √ getItemAttrValuesBySpuId
     */
    List<ItemAttrValueVo> skuAttrValues;

    /**
     * 品牌信息 √
     */
    private Long brandId;

    private String brandName;

    /**
     * 促销信息 sms √ getItemSaleVo
     */
    private List<ItemSaleVo> saleInfo;

    /**
     * 一级 二级 三级分类  √ getLevelOneAndTwo
     */
    private List<ItemCategoryVo> categories;

    /**
     * 库存信息 wms (库存量 - 库存锁定量)
     */
    private boolean store;

    /**
     * 规格参数属性组信息 √
     */
    private List<ItemAttrGroupVo> itemAttrGroup;





}
