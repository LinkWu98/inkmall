package cn.link.inkmall.sms.service.impl;

import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.link.inkmall.common.bean.PageResultVo;
import cn.link.inkmall.common.bean.PageParamVo;

import cn.link.inkmall.sms.mapper.CouponMapper;
import cn.link.inkmall.sms.entity.CouponEntity;
import cn.link.inkmall.sms.service.CouponService;


@Service("couponService")
public class CouponServiceImpl extends ServiceImpl<CouponMapper, CouponEntity> implements CouponService {

    @Override
    public PageResultVo queryPage(PageParamVo paramVo) {
        IPage<CouponEntity> page = this.page(
                paramVo.getPage(),
                new QueryWrapper<CouponEntity>()
        );

        return new PageResultVo(page);
    }

}