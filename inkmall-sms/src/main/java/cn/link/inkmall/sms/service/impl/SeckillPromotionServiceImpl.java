package cn.link.inkmall.sms.service.impl;

import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.link.inkmall.common.bean.PageResultVo;
import cn.link.inkmall.common.bean.PageParamVo;

import cn.link.inkmall.sms.mapper.SeckillPromotionMapper;
import cn.link.inkmall.sms.entity.SeckillPromotionEntity;
import cn.link.inkmall.sms.service.SeckillPromotionService;


@Service("seckillPromotionService")
public class SeckillPromotionServiceImpl extends ServiceImpl<SeckillPromotionMapper, SeckillPromotionEntity> implements SeckillPromotionService {

    @Override
    public PageResultVo queryPage(PageParamVo paramVo) {
        IPage<SeckillPromotionEntity> page = this.page(
                paramVo.getPage(),
                new QueryWrapper<SeckillPromotionEntity>()
        );

        return new PageResultVo(page);
    }

}