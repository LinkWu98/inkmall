package cn.link.inkmall.sms.controller;

import java.util.List;

import cn.link.inkmall.sms.vo.ItemSaleVo;
import cn.link.inkmall.sms.vo.SaleVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.link.inkmall.sms.entity.SkuBoundsEntity;
import cn.link.inkmall.sms.service.SkuBoundsService;
import cn.link.inkmall.common.bean.PageResultVo;
import cn.link.inkmall.common.bean.ResponseVo;
import cn.link.inkmall.common.bean.PageParamVo;

/**
 * 商品spu积分设置
 *
 * @author link
 * @email linkwu981002@gmail.com
 * @date 2020-05-11 23:08:16
 */
@Api(tags = "商品spu积分设置 管理")
@RestController
@RequestMapping("sms/spubounds")
public class SkuBoundsController {

    @Autowired
    private SkuBoundsService skuBoundsService;

    /**
     * 通过 skuId 获取积分信息
     */
    @GetMapping("bounds/{skuId}")
    public ResponseVo<SkuBoundsEntity> getBoundsBySkuId(@PathVariable("skuId") Long skuId) {

        SkuBoundsEntity skuBoundsEntity = skuBoundsService.getOne(new QueryWrapper<SkuBoundsEntity>().eq("sku_id", skuId));

        return ResponseVo.ok(skuBoundsEntity);

    }

    /**
     * 通过 skuId 查询促销信息（商品详情页）
     */
    @GetMapping("item/{skuId}")
    public ResponseVo<List<ItemSaleVo>> getItemSaleVo(@PathVariable("skuId") Long skuId) {

        List<ItemSaleVo> itemSaleVos = skuBoundsService.getItemSaleVo(skuId);

        return ResponseVo.ok(itemSaleVos);

    }

    @PostMapping("sale")
    public ResponseVo<Object> saveSaleInfo(@RequestBody SaleVo saleVo) {

        skuBoundsService.saveSaleInfo(saleVo);

        return ResponseVo.ok();

    }

    /**
     * 列表
     */
    @GetMapping
    @ApiOperation("分页查询")
    public ResponseVo<PageResultVo> list(PageParamVo paramVo){
        PageResultVo pageResultVo = skuBoundsService.queryPage(paramVo);

        return ResponseVo.ok(pageResultVo);
    }


    /**
     * 信息
     */
    @GetMapping("{id}")
    @ApiOperation("详情查询")
    public ResponseVo<SkuBoundsEntity> querySpuBoundsById(@PathVariable("id") Long id){
		SkuBoundsEntity spuBounds = skuBoundsService.getById(id);

        return ResponseVo.ok(spuBounds);
    }

    /**
     * 保存
     */
    @PostMapping
    @ApiOperation("保存")
    public ResponseVo<Object> save(@RequestBody SkuBoundsEntity spuBounds){
		skuBoundsService.save(spuBounds);

        return ResponseVo.ok();
    }

    /**
     * 修改
     */
    @PostMapping("/update")
    @ApiOperation("修改")
    public ResponseVo update(@RequestBody SkuBoundsEntity spuBounds){
		skuBoundsService.updateById(spuBounds);

        return ResponseVo.ok();
    }

    /**
     * 删除
     */
    @PostMapping("/delete")
    @ApiOperation("删除")
    public ResponseVo delete(@RequestBody List<Long> ids){
		skuBoundsService.removeByIds(ids);

        return ResponseVo.ok();
    }

}
