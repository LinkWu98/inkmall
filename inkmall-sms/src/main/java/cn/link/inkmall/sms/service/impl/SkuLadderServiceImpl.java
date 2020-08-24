package cn.link.inkmall.sms.service.impl;

import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.link.inkmall.common.bean.PageResultVo;
import cn.link.inkmall.common.bean.PageParamVo;

import cn.link.inkmall.sms.mapper.SkuLadderMapper;
import cn.link.inkmall.sms.entity.SkuLadderEntity;
import cn.link.inkmall.sms.service.SkuLadderService;


@Service("skuLadderService")
public class SkuLadderServiceImpl extends ServiceImpl<SkuLadderMapper, SkuLadderEntity> implements SkuLadderService {

    @Override
    public PageResultVo queryPage(PageParamVo paramVo) {
        IPage<SkuLadderEntity> page = this.page(
                paramVo.getPage(),
                new QueryWrapper<SkuLadderEntity>()
        );

        return new PageResultVo(page);
    }

}