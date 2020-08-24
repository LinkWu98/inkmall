package cn.link.inkmall.item.service.impl;

import cn.link.inkmall.item.feign.PmsFeign;
import cn.link.inkmall.item.feign.SmsFeign;
import cn.link.inkmall.item.feign.WmsFeign;
import cn.link.inkmall.item.service.ItemService;
import cn.link.inkmall.item.vo.ItemVo;
import cn.link.inkmall.pms.entity.*;
import cn.link.inkmall.wms.entity.WareSkuEntity;
import io.netty.util.concurrent.CompleteFuture;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @Author: Link
 * @Date: 2020/5/31 14:22
 * @Version 1.0
 */
@Service
public class ItemServiceImpl implements ItemService {

    @Autowired
    PmsFeign pmsFeign;

    @Autowired
    SmsFeign smsFeign;

    @Autowired
    WmsFeign wmsFeign;

    @Autowired
    ThreadPoolExecutor threadPool;

    @Override
    public ItemVo getItemsDetail(Long skuId) {

        if (skuId == null) {
            return null;
        }

        ItemVo itemVo = new ItemVo();

        //使用异步编排优化业务
        //1. sku对象查询
        CompletableFuture<SkuEntity> skuFuture = CompletableFuture.supplyAsync(() -> {
            SkuEntity skuEntity = pmsFeign.querySkuById(skuId).getData();
            if (skuEntity != null) {
                //sku信息
                itemVo.setSkuId(skuEntity.getId());
                itemVo.setDefaultImage(skuEntity.getDefaultImage());
                itemVo.setTitle(skuEntity.getTitle());
                itemVo.setSubtitle(skuEntity.getSubtitle());
                itemVo.setPrice(skuEntity.getPrice());
            }
            return skuEntity;
        }, threadPool);


        //不依赖于sku对象的操作，使用并行线程
        CompletableFuture<Void> skuImageFuture = CompletableFuture.runAsync(() -> {
            //sku images
            List<SkuImagesEntity> skuImagesEntities = pmsFeign.getImagesBySkuId(skuId).getData();
            itemVo.setImages(skuImagesEntities);
        }, threadPool);


        CompletableFuture<Void> salesFuture = CompletableFuture.runAsync(() -> {
            //促销信息
            itemVo.setSaleInfo(smsFeign.getItemSaleVo(skuId).getData());
        }, threadPool);

        CompletableFuture<Void> wareFuture = CompletableFuture.runAsync(() -> {
            //库存信息
            List<WareSkuEntity> wareSkuEntities = wmsFeign.getWareSkuEntitiesBySkuId(skuId).getData();
            if (!CollectionUtils.isEmpty(wareSkuEntities)) {
                itemVo.setStore(wareSkuEntities.stream().anyMatch(wareSkuEntity -> (wareSkuEntity.getStock() - wareSkuEntity.getStockLocked()) > 0));
            }
        }, threadPool);


        //依赖于sku对象的操作，使用串行线程
        CompletableFuture<Void> spuNameFuture = skuFuture.thenAcceptAsync(skuEntity -> {
            if (skuEntity != null) {
                SpuEntity spuEntity = pmsFeign.querySpuById(skuEntity.getSpuId()).getData();
                if (spuEntity != null) {
                    itemVo.setSpuName(spuEntity.getName());
                }
            }
        }, threadPool);

        CompletableFuture<Void> spuDescFuture = skuFuture.thenAcceptAsync(skuEntity -> {
            if (skuEntity != null) {
                //spu描述图片
                SpuDescEntity spuDescEntity = pmsFeign.querySpuDescById(skuEntity.getSpuId()).getData();
                if (spuDescEntity != null) {
                    String decript = spuDescEntity.getDecript();
                    if (decript != null) {
                        itemVo.setDescs(Arrays.asList(StringUtils.split(decript, ",")));
                    }
                }
            }
        }, threadPool);

        CompletableFuture<Void> skuAttrFuture = skuFuture.thenAcceptAsync(skuEntity -> {
            if (skuEntity != null) {
                //spu下所有sku销售属性及属性值
                itemVo.setSkuAttrValues(pmsFeign.getItemAttrValuesBySpuId(skuEntity.getSpuId()).getData());
            }
        }, threadPool);


        CompletableFuture<Void> brandFuture = skuFuture.thenAcceptAsync(skuEntity -> {
            if (skuEntity != null) {
                //brand
                Long brandId = skuEntity.getBrandId();
                if (brandId != null) {
                    itemVo.setBrandId(brandId);
                    BrandEntity brandEntity = pmsFeign.queryBrandById(brandId).getData();
                    if (brandEntity != null) {
                        itemVo.setBrandName(brandEntity.getName());
                    }
                }
            }
        }, threadPool);


        CompletableFuture<Void> categoryFuture = skuFuture.thenAcceptAsync(skuEntity -> {
            if (skuEntity != null) {
                //一级 二级 三级分类
                Long categoryId = skuEntity.getCategoryId();
                if (categoryId != null) {
                    itemVo.setCategories(pmsFeign.getLevelOneAndTwo(categoryId).getData());
                }
            }
        }, threadPool);


        CompletableFuture<Void> attrGroupFuture = skuFuture.thenAcceptAsync(skuEntity -> {
            if (skuEntity != null) {
                Long categoryId = skuEntity.getCategoryId();
                Long spuId = skuEntity.getSpuId();
                if (categoryId != null && spuId != null) {
                    //规格参数属性组信息
                    itemVo.setItemAttrGroup(pmsFeign.getItemAttrGroups(categoryId, spuId, skuId).getData());
                }
            }
        }, threadPool);

        //阻塞主线程等待其他线程执行完毕
        CompletableFuture.allOf
                (skuImageFuture, salesFuture, wareFuture, spuNameFuture, spuDescFuture,
                        skuAttrFuture, brandFuture, categoryFuture, attrGroupFuture).join();

        return itemVo;

    }
}
