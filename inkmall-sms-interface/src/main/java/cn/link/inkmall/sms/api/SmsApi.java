package cn.link.inkmall.sms.api;

import cn.link.inkmall.common.bean.ResponseVo;
import cn.link.inkmall.sms.entity.SkuBoundsEntity;
import cn.link.inkmall.sms.vo.ItemSaleVo;
import cn.link.inkmall.sms.vo.SaleVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * sms远程调用接口
 * @Author: Link
 * @Date: 2020/5/14 17:02
 * @Version 1.0
 */
@FeignClient("sms-service")
public interface SmsApi {

    /**
     * 保存对应sku的销售信息
     * @param saleVo
     * @return
     */
    @PostMapping("sms/spubounds/sale")
    ResponseVo<Object> saveSaleInfo(@RequestBody SaleVo saleVo);

    /**
     * 通过 skuId 查询促销信息（商品详情页）
     */
    @GetMapping("sms/spubounds/item/{skuId}")
    ResponseVo<List<ItemSaleVo>> getItemSaleVo(@PathVariable("skuId") Long skuId);

    /**
     * 通过 skuId 获取积分信息
     */
    @GetMapping("sms/spubounds/bounds/{skuId}")
    ResponseVo<SkuBoundsEntity> getBoundsBySkuId(@PathVariable("skuId") Long skuId);

}
