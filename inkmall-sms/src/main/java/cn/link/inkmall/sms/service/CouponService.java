package cn.link.inkmall.sms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import cn.link.inkmall.common.bean.PageResultVo;
import cn.link.inkmall.common.bean.PageParamVo;
import cn.link.inkmall.sms.entity.CouponEntity;

import java.util.Map;

/**
 * 优惠券信息
 *
 * @author link
 * @email linkwu981002@gmail.com
 * @date 2020-05-11 23:08:16
 */
public interface CouponService extends IService<CouponEntity> {

    PageResultVo queryPage(PageParamVo paramVo);
}

