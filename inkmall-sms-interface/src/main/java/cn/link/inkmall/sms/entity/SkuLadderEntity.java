package cn.link.inkmall.sms.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.math.BigDecimal;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 商品阶梯价格
 * 
 * @author link
 * @email linkwu981002@gmail.com
 * @date 2020-05-11 23:08:16
 */
@Data
@TableName("sms_sku_ladder")
@NoArgsConstructor
public class SkuLadderEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	public SkuLadderEntity(Long skuId, Integer fullCount, BigDecimal discount, Integer addOther) {
		this.skuId = skuId;
		this.fullCount = fullCount;
		this.discount = discount;
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
	private Integer addOther;

}
