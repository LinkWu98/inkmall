package cn.link.inkmall.sms.service.impl;

import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.link.inkmall.common.bean.PageResultVo;
import cn.link.inkmall.common.bean.PageParamVo;

import cn.link.inkmall.sms.mapper.SkuFullReductionMapper;
import cn.link.inkmall.sms.entity.SkuFullReductionEntity;
import cn.link.inkmall.sms.service.SkuFullReductionService;


@Service("skuFullReductionService")
public class SkuFullReductionServiceImpl extends ServiceImpl<SkuFullReductionMapper, SkuFullReductionEntity> implements SkuFullReductionService {

    @Override
    public PageResultVo queryPage(PageParamVo paramVo) {
        IPage<SkuFullReductionEntity> page = this.page(
                paramVo.getPage(),
                new QueryWrapper<SkuFullReductionEntity>()
        );

        return new PageResultVo(page);
    }

}