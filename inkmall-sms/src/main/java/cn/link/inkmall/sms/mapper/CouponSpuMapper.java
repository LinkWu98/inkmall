package cn.link.inkmall.sms.mapper;

import cn.link.inkmall.sms.entity.CouponSpuEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 优惠券与产品关联
 * 
 * @author link
 * @email linkwu981002@gmail.com
 * @date 2020-05-11 23:08:16
 */
@Mapper
public interface CouponSpuMapper extends BaseMapper<CouponSpuEntity> {
	
}
