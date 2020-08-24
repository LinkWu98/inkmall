//package cn.link.inkmall.search;
//
//import cn.link.inkmall.common.bean.PageParamVo;
//import cn.link.inkmall.pms.entity.*;
//import cn.link.inkmall.search.entity.Goods;
//import cn.link.inkmall.search.entity.SearchAttrValue;
//import cn.link.inkmall.search.feign.PmsFeign;
//import cn.link.inkmall.search.feign.WmsFeign;
//import cn.link.inkmall.search.repository.GoodsRepository;
//import cn.link.inkmall.wms.entity.WareSkuEntity;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.BeanUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
//import org.springframework.util.CollectionUtils;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.stream.Collectors;
//
//@SpringBootTest
//class InkmallSearchApplicationTests {
//
//    @Autowired
//    ElasticsearchRestTemplate restTemplate;
//
//    @Autowired
//    PmsFeign pmsFeign;
//
//    @Autowired
//    WmsFeign wmsFeign;
//
//    @Autowired
//    GoodsRepository goodsRepository;
//
//    @Test
//    void importDataToEs() {
//
//        //创建索引库、表和映射
//        restTemplate.createIndex(Goods.class);
//        restTemplate.putMapping(Goods.class);
//
//        //从spu入手，给商品对象赋值  (spu -> sku -> goods)
//        //使用分页查询spu，以防数据量太大，造成OOM
//        int pageNum = 0;
//        int pageSize = 100;
//
//        do {
//            //1.远程调用 pms 查询 spu
//            List<SpuEntity> spuEntities = pmsFeign.getSpusByPage(new PageParamVo(pageNum, pageSize, null)).getData();
//            if (CollectionUtils.isEmpty(spuEntities)) {
//                //记录为整数的情况下，进入此次循环查不到数据了，直接退出
//                break;
//            }
//            //遍历spu通过spuId查询sku
//            spuEntities.forEach(spuEntity -> {
//
//                //2.远程调用 pms 查询 spuId对应的sku
//                List<SkuEntity> skuEntities = pmsFeign.getSkuBySpuId(spuEntity.getId()).getData();
//                if (!CollectionUtils.isEmpty(skuEntities)) {
//                    List<Goods> goodsList = skuEntities.stream().map(skuEntity -> {
//                        Goods goods = new Goods();
//                        BeanUtils.copyProperties(skuEntity, goods);
//                        goods.setPrice(skuEntity.getPrice().doubleValue());
//                        goods.setSkuId(skuEntity.getId());
//                        goods.setCreateTime(spuEntity.getCreateTime());
//                        //3.远程调用 pms 查询 skuId对应的brand、category信息
//                        BrandEntity brandEntity = pmsFeign.queryBrandById(spuEntity.getBrandId()).getData();
//                        goods.setBrandName(brandEntity.getName());
//                        goods.setLogo(brandEntity.getLogo());
//                        CategoryEntity categoryEntity = pmsFeign.queryCategoryById(spuEntity.getCategoryId()).getData();
//                        goods.setCategoryName(categoryEntity.getName());
//                        //4.远程调用 pms 查询 categoryId对应的搜索属性, 并获取对应的属性值
//                        List<AttrEntity> attrEntities = pmsFeign.getAttrByCidTypeAndSearchType(spuEntity.getCategoryId(), null, 1).getData();
//                        if (!CollectionUtils.isEmpty(attrEntities)) {
//                            ArrayList<SearchAttrValue> searchAttrValues = new ArrayList<>();
//                            //获取搜索属性的id集合
//                            List<Long> searchAttrIds = attrEntities.stream().map(AttrEntity::getId).collect(Collectors.toList());
//                            //去pms_spu/sku_attr_value中查询对应spuId/skuId和属性id的属性值
//                            List<SpuAttrValueEntity> spuAttrValueEntities = pmsFeign.getSpuSearchAttrValueBySpuIdAndAttrIds(spuEntity.getId(), searchAttrIds).getData();
//                            if (!CollectionUtils.isEmpty(spuAttrValueEntities)) {
//                                searchAttrValues.addAll(spuAttrValueEntities.stream().map(spuAttrValueEntity -> {
//                                    SearchAttrValue searchAttrValue = new SearchAttrValue();
//                                    BeanUtils.copyProperties(spuAttrValueEntity, searchAttrValue);
//                                    return searchAttrValue;
//                                }).collect(Collectors.toList()));
//                            }
//
//                            List<SkuAttrValueEntity> skuAttrValueEntities = pmsFeign.getSkuSearchAttrValueBySpuIdAndAttrIds(skuEntity.getId(), searchAttrIds).getData();
//                            if (!CollectionUtils.isEmpty(skuAttrValueEntities)) {
//                                searchAttrValues.addAll(skuAttrValueEntities.stream().map(skuAttrValueEntity -> {
//                                    SearchAttrValue searchAttrValue = new SearchAttrValue();
//                                    BeanUtils.copyProperties(skuAttrValueEntity, searchAttrValue);
//                                    return searchAttrValue;
//                                }).collect(Collectors.toList()));
//                            }
//                            //将搜索属性值存入goods中
//                            goods.setSearchAttrs(searchAttrValues);
//                        }
//                        //5.远程调用 wms 查询 skuId对应的store、sales信息
//                        List<WareSkuEntity> wareSkuEntities = wmsFeign.getWareSkuEntitiesBySkuId(skuEntity.getId()).getData();
//                        if (!CollectionUtils.isEmpty(wareSkuEntities)) {
//                            //设置sales
//                            goods.setSales(wareSkuEntities.stream().map(WareSkuEntity::getSales).reduce((a, b) -> a + b).get());
//                            //设置store( >0 有库存 )
//                            goods.setStore(wareSkuEntities.stream().map(WareSkuEntity::getStock).anyMatch(stock -> stock > 0));
//                        }
//
//                        return goods;
//                    }).collect(Collectors.toList());
//
//                     //5.将goods集合存入es中
//                    goodsRepository.saveAll(goodsList);
//                }
//            });
//
//
//            //一次查100条，若记录数还是100，就再执行一次查询，不足100说明后面没记录了，退出循环
//            pageSize = spuEntities.size();
//            pageNum++;
//
//        } while (pageSize == 100);
//
//
//    }
//
//}
