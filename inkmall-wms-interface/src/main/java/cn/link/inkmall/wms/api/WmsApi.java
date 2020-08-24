package cn.link.inkmall.wms.api;

import cn.link.inkmall.common.bean.ResponseVo;
import cn.link.inkmall.wms.entity.WareSkuEntity;
import cn.link.inkmall.wms.vo.OrderLockWareSkuVo;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Author: Link
 * @Date: 2020/5/18 2:21
 * @Version 1.0
 */
public interface WmsApi {
    @GetMapping("wms/waresku/sku/{skuId}")
    ResponseVo<List<WareSkuEntity>> getWareSkuEntitiesBySkuId(@PathVariable("skuId") Long skuId);


    /**
     * 根据指定 skuId 和 商品数量 查询库存，锁定库存
     */
    @PostMapping("wms/waresku/check/lock")
    ResponseVo<List<OrderLockWareSkuVo>> checkAndLockStock(@RequestBody List<OrderLockWareSkuVo> orderLockWareSkuVos,
                                                           @RequestParam("orderToken") String orderToken);
}
