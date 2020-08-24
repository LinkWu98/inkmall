package cn.link.inkmall.sms.service.impl;

import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.link.inkmall.common.bean.PageResultVo;
import cn.link.inkmall.common.bean.PageParamVo;

import cn.link.inkmall.sms.mapper.CouponSpuCategoryMapper;
import cn.link.inkmall.sms.entity.CouponSpuCategoryEntity;
import cn.link.inkmall.sms.service.CouponSpuCategoryService;


@Service("couponSpuCategoryService")
public class CouponSpuCategoryServiceImpl extends ServiceImpl<CouponSpuCategoryMapper, CouponSpuCategoryEntity> implements CouponSpuCategoryService {

    @Override
    public PageResultVo queryPage(PageParamVo paramVo) {
        IPage<CouponSpuCategoryEntity> page = this.page(
                paramVo.getPage(),
                new QueryWrapper<CouponSpuCategoryEntity>()
        );

        return new PageResultVo(page);
    }

}