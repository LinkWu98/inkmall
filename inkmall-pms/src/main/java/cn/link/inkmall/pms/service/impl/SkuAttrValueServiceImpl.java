package cn.link.inkmall.pms.service.impl;

import cn.link.inkmall.pms.entity.SpuAttrValueEntity;
import cn.link.inkmall.pms.vo.SkuVo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.link.inkmall.common.bean.PageResultVo;
import cn.link.inkmall.common.bean.PageParamVo;

import cn.link.inkmall.pms.mapper.SkuAttrValueMapper;
import cn.link.inkmall.pms.entity.SkuAttrValueEntity;
import cn.link.inkmall.pms.service.SkuAttrValueService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;


@Service("skuAttrValueService")
public class SkuAttrValueServiceImpl extends ServiceImpl<SkuAttrValueMapper, SkuAttrValueEntity> implements SkuAttrValueService {

    @Override
    public PageResultVo queryPage(PageParamVo paramVo) {
        IPage<SkuAttrValueEntity> page = this.page(
                paramVo.getPage(),
                new QueryWrapper<SkuAttrValueEntity>()
        );

        return new PageResultVo(page);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveSkuAttrs(SkuVo skuVo) {
        List<SkuAttrValueEntity> saleAttrs = skuVo.getSaleAttrs();
        if(!CollectionUtils.isEmpty(saleAttrs)){
            saleAttrs.forEach(saleAttr -> {
                saleAttr.setSkuId(skuVo.getId());
                saleAttr.setSort(0);
            });

            this.saveBatch(saleAttrs);
        }
    }

    @Override
    public List<SkuAttrValueEntity> getSkuSearchAttrValueBySkuIdAndAttrIds(Long skuId, List<Long> attrIds) {
        return baseMapper.selectList(new QueryWrapper<SkuAttrValueEntity>().eq("sku_id", skuId).in("attr_id", attrIds));
    }


}