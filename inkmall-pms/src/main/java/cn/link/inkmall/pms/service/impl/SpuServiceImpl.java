package cn.link.inkmall.pms.service.impl;

import cn.link.inkmall.pms.entity.*;
import cn.link.inkmall.pms.feign.SmsFeign;
import cn.link.inkmall.pms.service.*;
import cn.link.inkmall.pms.vo.ItemAttrValueVo;
import cn.link.inkmall.pms.vo.SkuVo;
import cn.link.inkmall.pms.vo.SpuAttrValueVo;
import cn.link.inkmall.pms.vo.SpuVo;
import io.seata.spring.annotation.GlobalTransactional;
import org.apache.commons.lang.StringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.link.inkmall.common.bean.PageResultVo;
import cn.link.inkmall.common.bean.PageParamVo;

import cn.link.inkmall.pms.mapper.SpuMapper;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


@Service("spuService")
public class SpuServiceImpl extends ServiceImpl<SpuMapper, SpuEntity> implements SpuService {

    @Autowired
    SpuAttrValueService spuAttrValueService;

    @Autowired
    SkuService skuService;

    @Autowired
    SpuDescService spuDescService;

    @Autowired
    SkuImagesService skuImagesService;

    @Autowired
    SkuAttrValueService skuAttrValueService;

    @Autowired
    SmsFeign smsFeign;

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Override
    public PageResultVo queryPage(PageParamVo paramVo) {
        IPage<SpuEntity> page = this.page(
                paramVo.getPage(),
                new QueryWrapper<SpuEntity>()
        );

        return new PageResultVo(page);
    }

    @Override
    public PageResultVo getSpuListByCategoryId(Long categoryId, PageParamVo paramVo) {

        QueryWrapper<SpuEntity> wrapper = new QueryWrapper<>();

        //若categoryId == 0则查询所有spu
        if (categoryId != 0) {
            wrapper.eq("category_id", categoryId);
        }

        String key = paramVo.getKey();

        //若查询条件不为空，就要按条件查询
        if (StringUtils.isNotBlank(key)) {
            //此方法等于 where category_id = x and (id = xx or name like %xx%) 先查并集，再查交集
            wrapper.and(spuEntityQueryWrapper -> spuEntityQueryWrapper.eq("id", key).or().like("name", key));
        }

        //分页查询spu
        IPage<SpuEntity> page = page(paramVo.getPage(), wrapper);
        return new PageResultVo(page);
    }

    @Override
    @GlobalTransactional(rollbackFor = Exception.class)
    public void bigSave(SpuVo spuVo) {
        //1. 保存spu
        saveSpu(spuVo);

        //1.2保存spu的描述信息
        spuDescService.saveSpuDesc(spuVo);

        //1.3保存spu的基本信息
        spuAttrValueService.saveSpuAttrs(spuVo);

        //2. 保存sku
        skuService.saveSkuAndSaleInfo(spuVo);

        //3. 发送消息至队列，通知 es 更新索引库(发送spuId, 让es的工程远程调用更新即可，否则信息量太大)
        rabbitTemplate.convertAndSend("pms.item.exchange", "item.insert", spuVo.getId());

    }

    @Override
    public List<ItemAttrValueVo> getItemAttrValuesBySpuId(Long spuId) {

        //通过 spuId 获取 spu下的所有skuId
        List<SkuEntity> skuEntities = skuService.list(new QueryWrapper<SkuEntity>().eq("spu_id", spuId));
        List<Long> skuIds = skuEntities.stream().map(SkuEntity::getId).collect(Collectors.toList());


        //通过 skuId集合去sku_attr_value查询属性及属性值
        List<SkuAttrValueEntity> skuAttrValueEntities = skuAttrValueService.list(new QueryWrapper<SkuAttrValueEntity>().in("sku_id", skuIds));

        //转为 ItemAttrValueVo 返回
        return skuAttrValueEntities.stream().map(skuAttrValueEntity -> {
            ItemAttrValueVo itemAttrValueVo = new ItemAttrValueVo();
            BeanUtils.copyProperties(skuAttrValueEntity, itemAttrValueVo);
            return itemAttrValueVo;
        }).collect(Collectors.toList());
    }


    private void saveSpu(SpuVo spuVo) {
        //1.1写入spu 回显id
        spuVo.setCreateTime(new Date());
        spuVo.setUpdateTime(spuVo.getCreateTime());
        save(spuVo);
    }

}