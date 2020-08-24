package cn.link.inkmall.sms.service.impl;

import cn.link.inkmall.sms.entity.SkuFullReductionEntity;
import cn.link.inkmall.sms.entity.SkuLadderEntity;
import cn.link.inkmall.sms.service.SkuFullReductionService;
import cn.link.inkmall.sms.service.SkuLadderService;
import cn.link.inkmall.sms.vo.ItemSaleVo;
import cn.link.inkmall.sms.vo.SaleVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.link.inkmall.common.bean.PageResultVo;
import cn.link.inkmall.common.bean.PageParamVo;

import cn.link.inkmall.sms.mapper.SkuBoundsMapper;
import cn.link.inkmall.sms.entity.SkuBoundsEntity;
import cn.link.inkmall.sms.service.SkuBoundsService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


@Service("spuBoundsService")
public class SkuBoundsServiceImpl extends ServiceImpl<SkuBoundsMapper, SkuBoundsEntity> implements SkuBoundsService {

    @Autowired
    SkuLadderService skuLadderService;

    @Autowired
    SkuFullReductionService skuFullReductionService;

    @Override
    public PageResultVo queryPage(PageParamVo paramVo) {
        IPage<SkuBoundsEntity> page = this.page(
                paramVo.getPage(),
                new QueryWrapper<SkuBoundsEntity>()
        );

        return new PageResultVo(page);
    }

    @Override
    public void saveSaleInfo(SaleVo saleVo) {

        //work 0 0 1 1 -> 1 1 0 0(数据库反着存的) -> 12
        List<Integer> work = saleVo.getWork();
        Integer workInt = work.get(3)*8 + work.get(2)*4 + work.get(1);
        //保存积分
        this.save(new SkuBoundsEntity(saleVo.getSpuId(), saleVo.getGrowBounds(), saleVo.getBuyBounds(), workInt));
        //保存折扣
        skuLadderService.save(new SkuLadderEntity(saleVo.getSpuId(), saleVo.getFullCount(), saleVo.getDiscount(), saleVo.getLadderAddOther()));
        //保存满减
        skuFullReductionService.save(new SkuFullReductionEntity(saleVo.getSpuId(), saleVo.getFullPrice(), saleVo.getReducePrice(), saleVo.getFullAddOther()));

    }

    @Override
    public List<ItemSaleVo> getItemSaleVo(Long skuId) {

        List<ItemSaleVo> itemSaleVos = new ArrayList<>();

        //打折
        SkuLadderEntity skuLadderEntity = skuLadderService.getOne(new QueryWrapper<SkuLadderEntity>().eq("sku_id", skuId));
        if (skuLadderEntity != null) {
            ItemSaleVo itemSaleVo = new ItemSaleVo();
            itemSaleVo.setType("打折");
            itemSaleVo.setIntroduction("满" + skuLadderEntity.getFullCount() + "件，打" + skuLadderEntity.getDiscount().divide(new BigDecimal(10)) + "折");
            itemSaleVos.add(itemSaleVo);
        }

        //满减
        SkuFullReductionEntity skuFullReductionEntity = skuFullReductionService.getOne(new QueryWrapper<SkuFullReductionEntity>().eq("sku_id", skuId));
        if (skuFullReductionEntity != null) {
            ItemSaleVo itemSaleVo = new ItemSaleVo();
            itemSaleVo.setType("满减");
            itemSaleVo.setIntroduction("满" + skuFullReductionEntity.getFullPrice() + "元，减" + skuFullReductionEntity.getReducePrice() +"元");
            itemSaleVos.add(itemSaleVo);
        }

        //积分
        SkuBoundsEntity skuBoundsEntity = baseMapper.selectOne(new QueryWrapper<SkuBoundsEntity>().eq("sku_id", skuId));
        if (skuBoundsEntity != null) {
            ItemSaleVo itemSaleVo = new ItemSaleVo();
            itemSaleVo.setType("积分");
            itemSaleVo.setIntroduction("购买该商品赠送购物积分 ：" + skuBoundsEntity.getBuyBounds() + "点, 成长积分：" + skuBoundsEntity.getGrowBounds() + "点");
            itemSaleVos.add(itemSaleVo);
        }

        return itemSaleVos;

    }

}