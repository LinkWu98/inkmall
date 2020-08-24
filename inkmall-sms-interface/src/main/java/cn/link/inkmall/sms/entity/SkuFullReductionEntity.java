package cn.link.inkmall.sms.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.math.BigDecimal;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 商品满减信息
 * 
 * @author link
 * @email linkwu981002@gmail.com
 * @date 2020-05-11 23:08:16
 */
@Data
@TableName("sms_sku_full_reduction")
@NoArgsConstructor
public class SkuFullReductionEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	public SkuFullReductionEntity(Long skuId, BigDecimal fullPrice, BigDecimal reducePrice, Integer addOther) {
		this.skuId = skuId;
		this.fullPrice = fullPrice;
		this.reducePrice = reducePrice;
		this.addOther = addOther;
	}

	/**
	 * id
	 */
	@TableId
	private Long id;
	/**
	 * spu_id
	 */
	private Long skuId;
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
	private Integer addOther;

}
