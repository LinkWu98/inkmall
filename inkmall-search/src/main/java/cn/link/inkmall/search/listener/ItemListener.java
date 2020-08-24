package cn.link.inkmall.search.listener;

import cn.link.inkmall.pms.entity.*;
import cn.link.inkmall.search.entity.Goods;
import cn.link.inkmall.search.entity.SearchAttrValue;
import cn.link.inkmall.search.feign.PmsFeign;
import cn.link.inkmall.search.feign.WmsFeign;
import cn.link.inkmall.search.repository.GoodsRepository;
import cn.link.inkmall.wms.entity.WareSkuEntity;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author: Link
 * @Date: 2020/5/26 20:13
 * @Version 1.0
 */
@Component
public class ItemListener {

    @Autowired
    GoodsRepository goodsRepository;

    @Autowired
    PmsFeign pmsFeign;

    @Autowired
    WmsFeign wmsFeign;

    @RabbitListener(bindings =
        @QueueBinding(
            value = @Queue(value = "search_item_queue", durable = "true"),
            exchange = @Exchange(value = "pms.item.exchange", ignoreDeclarationExceptions = "true", type = ExchangeTypes.TOPIC),
            key = {"item.insert"}
        )
    )
    public void listenPmsBigSave(Long spuId, Channel channel, Message message) throws IOException {
        try {
            //保存数据到 es 索引库
            insertItems(spuId);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            //业务出现异常
            if (message.getMessageProperties().getRedelivered()) {
                //若重新入过队就拒绝消息，消息会被废弃或进入该业务队列绑定的死信队列
                channel.basicReject(message.getMessageProperties().getDeliveryTag(), false);
            } else {
                //若未重新入过队就重试
                channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
            }

        }
    }

    private void insertItems(Long spuId) {

        SpuEntity spuEntity = pmsFeign.querySpuById(spuId).getData();

        if (spuEntity == null) {
            return;
        }

        // 由 spu 查询对应的 sku
        List<SkuEntity> skuEntities = pmsFeign.getSkuBySpuId(spuEntity.getId()).getData();
        if (CollectionUtils.isEmpty(skuEntities)) {
            return;
        }

        //sku 转为 goods 批量存入 es 索引库中
        List<Goods> goodsList = skuEntities.stream().map(skuEntity -> {
            Goods goods = new Goods();
            BeanUtils.copyProperties(skuEntity, goods);
            goods.setSkuId(skuEntity.getId());
            goods.setCreateTime(spuEntity.getCreateTime());
            goods.setPrice(skuEntity.getPrice().doubleValue());

            //pmsFeign
            CategoryEntity categoryEntity = pmsFeign.queryCategoryById(skuEntity.getCategoryId()).getData();
            if (categoryEntity != null) {
                //categoryName
                goods.setCategoryName(categoryEntity.getName());
            }

            BrandEntity brandEntity = pmsFeign.queryBrandById(skuEntity.getBrandId()).getData();
            if (brandEntity != null) {
                //brandName logo
                goods.setBrandName(brandEntity.getName());
                goods.setLogo(brandEntity.getLogo());
            }
                //searchAttr
            List<AttrEntity> attrEntities = pmsFeign.getAttrByCidTypeAndSearchType(spuEntity.getCategoryId(), null, 1).getData();
            if (!CollectionUtils.isEmpty(attrEntities)) {
                    //初始化一个空 SearchAttrValue 集合
                List<SearchAttrValue> searchAttrs = new ArrayList<>();

                    //先获取searchType的attrId集合
                List<Long> attrIds = attrEntities.stream().map(AttrEntity::getId).collect(Collectors.toList());
                    //通过attrId集合和 spuId 去 spu_attr_value 查询对应的搜索属性，转为SearchAttrValue
                List<SpuAttrValueEntity> spuSearchAttrs = pmsFeign.getSpuSearchAttrValueBySpuIdAndAttrIds(spuEntity.getId(), attrIds).getData();
                if (!CollectionUtils.isEmpty(spuSearchAttrs)) {
                    searchAttrs.addAll(spuSearchAttrs.stream().map(spuSearchAttr -> {
                        SearchAttrValue searchAttrValue = new SearchAttrValue();
                        searchAttrValue.setAttrId(spuSearchAttr.getAttrId());
                        searchAttrValue.setAttrName(spuSearchAttr.getAttrName());
                        searchAttrValue.setAttrValue(spuSearchAttr.getAttrValue());
                        return searchAttrValue;
                    }).collect(Collectors.toList()));
                }
                    //通过attrId集合和 spuId 去 sku_attr_value 查询对应的搜索属性，转为SearchAttrValue
                List<SkuAttrValueEntity> skuSearchAttrs = pmsFeign.getSkuSearchAttrValueBySpuIdAndAttrIds(skuEntity.getId(), attrIds).getData();
                 if (!CollectionUtils.isEmpty(skuSearchAttrs)) {
                    searchAttrs.addAll(skuSearchAttrs.stream().map(skuSearchAttr -> {
                        SearchAttrValue searchAttrValue = new SearchAttrValue();
                        searchAttrValue.setAttrId(skuSearchAttr.getAttrId());
                        searchAttrValue.setAttrName(skuSearchAttr.getAttrName());
                        searchAttrValue.setAttrValue(skuSearchAttr.getAttrValue());
                        return searchAttrValue;
                    }).collect(Collectors.toList()));
                }
            }

            //wmsFeign
            List<WareSkuEntity> wareSkuEntities = wmsFeign.getWareSkuEntitiesBySkuId(skuEntity.getId()).getData();
            if (!CollectionUtils.isEmpty(wareSkuEntities)) {
                //sales
                Long sales = wareSkuEntities.stream().map(WareSkuEntity::getSales).reduce((sales1, sales2) -> sales1 + sales2).get();
                goods.setSales(sales);
                //store
                boolean store = wareSkuEntities.stream().anyMatch(wareSkuEntity -> wareSkuEntity.getStock() > 0);
                goods.setStore(store);

            }

            return goods;
        }).collect(Collectors.toList());

        goodsRepository.saveAll(goodsList);

    }

}
