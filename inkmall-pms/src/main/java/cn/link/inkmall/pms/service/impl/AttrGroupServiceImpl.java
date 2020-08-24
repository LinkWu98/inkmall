package cn.link.inkmall.pms.service.impl;

import cn.link.inkmall.pms.entity.AttrEntity;
import cn.link.inkmall.pms.entity.SkuAttrValueEntity;
import cn.link.inkmall.pms.entity.SpuAttrValueEntity;
import cn.link.inkmall.pms.service.AttrService;
import cn.link.inkmall.pms.service.SkuAttrValueService;
import cn.link.inkmall.pms.service.SpuAttrValueService;
import cn.link.inkmall.pms.vo.ItemAttrGroupVo;
import cn.link.inkmall.pms.vo.ItemAttrValueVo;
import cn.link.inkmall.pms.vo.SpuAttrVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.link.inkmall.common.bean.PageResultVo;
import cn.link.inkmall.common.bean.PageParamVo;

import cn.link.inkmall.pms.mapper.AttrGroupMapper;
import cn.link.inkmall.pms.entity.AttrGroupEntity;
import cn.link.inkmall.pms.service.AttrGroupService;
import org.springframework.util.CollectionUtils;


@Service("attrGroupService")
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupMapper, AttrGroupEntity> implements AttrGroupService {

    @Autowired
    AttrService attrService;

    @Autowired
    SkuAttrValueService skuAttrValueService;

    @Autowired
    SpuAttrValueService spuAttrValueService;

    @Override
    public PageResultVo queryPage(PageParamVo paramVo) {
        IPage<AttrGroupEntity> page = this.page(
                paramVo.getPage(),
                new QueryWrapper<AttrGroupEntity>()
        );

        return new PageResultVo(page);
    }

    @Override
    public List<SpuAttrVo> getSpuAttrsByCid(Long cid) {

        //先获取属性组集合(只查询基本属性 - type = 1)
        List<AttrGroupEntity> groupEntities = baseMapper.selectList(new QueryWrapper<AttrGroupEntity>().eq("category_id",cid));

        //属性组非空判断
        if (CollectionUtils.isEmpty(groupEntities)) {
            return null;
        }

        //因为涉及集合中对象类型转换，因此使用Stream api的map方法
        Stream<AttrGroupEntity> stream = groupEntities.stream();

        return stream.map(attrGroupEntity -> {
            SpuAttrVo spuAttrVo = new SpuAttrVo();
            //将每个属性组的属性copy到Vo对象中
            BeanUtils.copyProperties(attrGroupEntity, spuAttrVo);
            //将AttrGroupEntity转换为SpuAttrVo，还需要查询对应属性Group下的attr
            Long groupId = attrGroupEntity.getId();
            List<AttrEntity> attrEntities = attrService.list(new QueryWrapper<AttrEntity>().eq("group_id", groupId).eq("type", 1));
            spuAttrVo.setAttrEntities(attrEntities);
            return spuAttrVo;
        }).collect(Collectors.toList());

    }

    @Override
    public List<ItemAttrGroupVo> getItemAttrGroups(Long categoryId, Long spuId, Long skuId) {

        List<AttrGroupEntity> attrGroupEntities = baseMapper.selectList(new QueryWrapper<AttrGroupEntity>().eq("category_id", categoryId));

        if (CollectionUtils.isEmpty(attrGroupEntities)) {
            return null;
        }

        //每个 AttrGroupEntity 转为 ItemAttrGroupVo
        List<ItemAttrGroupVo> itemAttrGroupVos = attrGroupEntities.stream().map(attrGroupEntity -> {

            ItemAttrGroupVo itemAttrGroupVo = new ItemAttrGroupVo();

            //获取每个属性组中的属性
            List<AttrEntity> attrEntities = attrService.list(new QueryWrapper<AttrEntity>().eq("group_id", attrGroupEntity.getId()));
            if (!CollectionUtils.isEmpty(attrEntities)) {
                //获取每个属性组中的属性id, 分别去sku，spu属性值表中去查询属性及属性值
                List<Long> attrIds = attrEntities.stream().map(AttrEntity::getId).collect(Collectors.toList());

                List<ItemAttrValueVo> itemAttrValueVos = new ArrayList<>();
                //spu_attr_value表查询
                List<SpuAttrValueEntity> spuAttrValueEntities = spuAttrValueService.list(new QueryWrapper<SpuAttrValueEntity>().eq("spu_id", spuId).in("attr_id", attrIds));
                if (!CollectionUtils.isEmpty(spuAttrValueEntities)) {

                    itemAttrValueVos.addAll(spuAttrValueEntities.stream().map(spuAttrValueEntity -> {
                        ItemAttrValueVo itemAttrValueVo = new ItemAttrValueVo();
                        BeanUtils.copyProperties(spuAttrValueEntity, itemAttrValueVo);
                        return itemAttrValueVo;
                    }).collect(Collectors.toList()));
                }

                //sku_attr_value表查询
                List<SkuAttrValueEntity> skuAttrValueEntities = skuAttrValueService.list(new QueryWrapper<SkuAttrValueEntity>().eq("sku_id", skuId).in("attr_id", attrIds));
                if (!CollectionUtils.isEmpty(skuAttrValueEntities)) {

                    itemAttrValueVos.addAll(skuAttrValueEntities.stream().map(skuAttrValueEntity -> {
                        ItemAttrValueVo itemAttrValueVo = new ItemAttrValueVo();
                        BeanUtils.copyProperties(skuAttrValueEntity, itemAttrValueVo);
                        return itemAttrValueVo;
                    }).collect(Collectors.toList()));
                }

                itemAttrGroupVo.setGroupAttrs(itemAttrValueVos);

            }

            itemAttrGroupVo.setGroupName(attrGroupEntity.getName());

            return itemAttrGroupVo;

        }).collect(Collectors.toList());

        return itemAttrGroupVos;

    }

}