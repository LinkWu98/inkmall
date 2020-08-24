package cn.link.inkmall.pms.vo;

import cn.link.inkmall.pms.entity.SkuAttrValueEntity;
import cn.link.inkmall.pms.entity.SkuEntity;
import cn.link.inkmall.sms.vo.SaleVo;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 封装了页面sku对象的数据
 *
 * @Author: Link
 * @Date: 2020/5/14 16:05
 * @Version 1.0
 */
@Data
public class SkuVo extends SkuEntity {

    /**
     * 图片信息
     */
    private List<String> images;

    /**
     * 销售属性
     */
    private List<SkuAttrValueEntity> saleAttrs;

    //以下为营销信息

     /**
     * 成长积分
     */
    private BigDecimal growBounds;

    /**
     * 购物积分
     */
    private BigDecimal buyBounds;

    /**
     * 优惠生效情况[1111（四个状态位，从右到左）;0 - 无优惠，成长积分是否赠送;1 - 无优惠，购物积分是否赠送;2 - 有优惠，成长积分是否赠送;3 - 有优惠，购物积分是否赠送【状态位0：不赠送，1：赠送】]
     */
    private List<Integer> work;

    /**
     * 满几件
     */
    private Integer fullCount;

    /**
     * 打几折
     */
    private BigDecimal discount;

    /**
     * 是否叠加其他优惠[0-不可叠加，1-可叠加]
     */
    private Integer ladderAddOther;

    /**
	 * 满多少
	 */
	private BigDecimal fullPrice;

	/**
	 * 减多少
	 */
	private BigDecimal reducePrice;

	/**
	 * 是否参与其他优惠
	 */
    private Integer fullAddOther;
}
