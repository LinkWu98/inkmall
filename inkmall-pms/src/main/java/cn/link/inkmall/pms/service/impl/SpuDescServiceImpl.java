package cn.link.inkmall.pms.service.impl;

import cn.link.inkmall.pms.vo.SpuVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.link.inkmall.common.bean.PageResultVo;
import cn.link.inkmall.common.bean.PageParamVo;

import cn.link.inkmall.pms.mapper.SpuDescMapper;
import cn.link.inkmall.pms.entity.SpuDescEntity;
import cn.link.inkmall.pms.service.SpuDescService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;


@Service("spuDescService")
public class SpuDescServiceImpl extends ServiceImpl<SpuDescMapper, SpuDescEntity> implements SpuDescService {

    @Override
    public PageResultVo queryPage(PageParamVo paramVo) {
        IPage<SpuDescEntity> page = this.page(
                paramVo.getPage(),
                new QueryWrapper<SpuDescEntity>()
        );

        return new PageResultVo(page);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveSpuDesc(SpuVo spuVo) {
        SpuDescEntity spuDescEntity = new SpuDescEntity();
        List<String> spuDescs = spuVo.getSpuImages();
        if(!CollectionUtils.isEmpty(spuDescs)){
            spuDescEntity.setDecript(StringUtils.join(spuDescs, ","));
        }
        spuDescEntity.setSpuId(spuVo.getId());
        this.save(spuDescEntity);
    }


}