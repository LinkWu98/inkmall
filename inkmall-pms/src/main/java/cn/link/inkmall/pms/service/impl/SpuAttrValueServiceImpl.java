package cn.link.inkmall.pms.service.impl;

import cn.link.inkmall.pms.vo.SpuAttrValueVo;
import cn.link.inkmall.pms.vo.SpuVo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.link.inkmall.common.bean.PageResultVo;
import cn.link.inkmall.common.bean.PageParamVo;

import cn.link.inkmall.pms.mapper.SpuAttrValueMapper;
import cn.link.inkmall.pms.entity.SpuAttrValueEntity;
import cn.link.inkmall.pms.service.SpuAttrValueService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;


@Service("spuAttrValueService")
public class SpuAttrValueServiceImpl extends ServiceImpl<SpuAttrValueMapper, SpuAttrValueEntity> implements SpuAttrValueService {

    @Override
    public PageResultVo queryPage(PageParamVo paramVo) {
        IPage<SpuAttrValueEntity> page = this.page(
                paramVo.getPage(),
                new QueryWrapper<SpuAttrValueEntity>()
        );

        return new PageResultVo(page);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveSpuAttrs(SpuVo spuVo) {
        List<SpuAttrValueVo> baseAttrs = spuVo.getBaseAttrs();
        if(!CollectionUtils.isEmpty(baseAttrs)){
            List<SpuAttrValueEntity> spuAttrValueEntities = baseAttrs.stream().map(baseAttr -> {
                baseAttr.setSpuId(spuVo.getId());
                baseAttr.setSort(0);
                return (SpuAttrValueEntity) baseAttr;
            }).collect(Collectors.toList());
            this.saveBatch(spuAttrValueEntities);
        }
    }

    @Override
    public List<SpuAttrValueEntity> getSpuSearchAttrValueBySpuIdAndAttrIds(Long spuId, List<Long> attrIds) {

        return baseMapper.selectList(new QueryWrapper<SpuAttrValueEntity>().eq("spu_id", spuId).in("attr_id", attrIds));

    }


}