package cn.link.inkmall.pms.service.impl;

import cn.link.inkmall.pms.entity.SkuAttrValueEntity;
import cn.link.inkmall.pms.entity.SkuImagesEntity;
import cn.link.inkmall.pms.feign.SmsFeign;
import cn.link.inkmall.pms.service.SkuAttrValueService;
import cn.link.inkmall.pms.service.SkuImagesService;
import cn.link.inkmall.pms.vo.SkuVo;
import cn.link.inkmall.pms.vo.SpuVo;
import cn.link.inkmall.sms.vo.SaleVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.link.inkmall.common.bean.PageResultVo;
import cn.link.inkmall.common.bean.PageParamVo;

import cn.link.inkmall.pms.mapper.SkuMapper;
import cn.link.inkmall.pms.entity.SkuEntity;
import cn.link.inkmall.pms.service.SkuService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;


@Service("skuService")
public class SkuServiceImpl extends ServiceImpl<SkuMapper, SkuEntity> implements SkuService {

    @Autowired
    SmsFeign smsFeign;

    @Autowired
    SkuImagesService skuImagesService;

    @Autowired
    SkuAttrValueService skuAttrValueService;

    @Override
    public PageResultVo queryPage(PageParamVo paramVo) {
        IPage<SkuEntity> page = this.page(
                paramVo.getPage(),
                new QueryWrapper<SkuEntity>()
        );

        return new PageResultVo(page);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveSkuAndSaleInfo(SpuVo spuVo) {
        //非空判断
        List<SkuVo> skuVos = spuVo.getSkus();
        if(CollectionUtils.isEmpty(skuVos)){
            return;
        }
        skuVos.forEach(skuVo -> {
            //2.1写入sku 回显id
            skuVo.setSpuId(spuVo.getId());
            this.save(skuVo);
            //2.2存储图片信息
            skuImagesService.saveSkuImages(skuVo);
            //2.3存储销售信息
            skuAttrValueService.saveSkuAttrs(skuVo);
            //3 存储营销信息(远程调用)
            SaleVo saleVo = new SaleVo();
            saleVo.setSpuId(spuVo.getId());
            BeanUtils.copyProperties(skuVo, saleVo);
            smsFeign.saveSaleInfo(saleVo);

        });
    }




}