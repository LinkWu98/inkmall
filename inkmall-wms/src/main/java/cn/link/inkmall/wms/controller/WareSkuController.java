package cn.link.inkmall.wms.controller;

import cn.link.inkmall.common.bean.PageParamVo;
import cn.link.inkmall.common.bean.PageResultVo;
import cn.link.inkmall.common.bean.ResponseVo;
import cn.link.inkmall.wms.entity.WareSkuEntity;
import cn.link.inkmall.wms.service.WareSkuService;
import cn.link.inkmall.wms.vo.OrderLockWareSkuVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 商品库存
 *
 * @author link
 * @email linkwu981002@gmail.com
 * @date 2020-05-11 22:52:38
 */
@Api(tags = "商品库存 管理")
@RestController
@RequestMapping("wms/waresku")
public class WareSkuController {

    @Autowired
    private WareSkuService wareSkuService;

    /**
     * 根据指定 skuId 和 商品数量 查询库存，锁定库存
     */
    @PostMapping("check/lock")
    public ResponseVo<List<OrderLockWareSkuVo>> checkAndLockStock(@RequestBody List<OrderLockWareSkuVo> orderLockWareSkuVos,
                                                                  @RequestParam("orderToken") String orderToken) {

        wareSkuService.checkAndLockStock(orderLockWareSkuVos, orderToken);

        return ResponseVo.ok(orderLockWareSkuVos);
    }


    /**
     * 通过指定的skuId获取sku的库存信息集WareSkuEntities
     *
     * @param skuId
     * @return
     */
    @GetMapping("sku/{skuId}")
    public ResponseVo<List<WareSkuEntity>> getWareSkuEntitiesBySkuId(@PathVariable("skuId") Long skuId) {

        List<WareSkuEntity> wareSkuEntities = wareSkuService.list(new QueryWrapper<WareSkuEntity>().eq("sku_id", skuId));

        return ResponseVo.ok(wareSkuEntities);

    }

    /**
     * 列表
     */
    @GetMapping
    @ApiOperation("分页查询")
    public ResponseVo<PageResultVo> list(PageParamVo paramVo) {
        PageResultVo pageResultVo = wareSkuService.queryPage(paramVo);

        return ResponseVo.ok(pageResultVo);
    }


    /**
     * 信息
     */
    @GetMapping("{id}")
    @ApiOperation("详情查询")
    public ResponseVo<WareSkuEntity> queryWareSkuById(@PathVariable("id") Long id) {
        WareSkuEntity wareSku = wareSkuService.getById(id);

        return ResponseVo.ok(wareSku);
    }

    /**
     * 保存
     */
    @PostMapping
    @ApiOperation("保存")
    public ResponseVo<Object> save(@RequestBody WareSkuEntity wareSku) {
        wareSkuService.save(wareSku);

        return ResponseVo.ok();
    }

    /**
     * 修改
     */
    @PostMapping("/update")
    @ApiOperation("修改")
    public ResponseVo update(@RequestBody WareSkuEntity wareSku) {
        wareSkuService.updateById(wareSku);

        return ResponseVo.ok();
    }

    /**
     * 删除
     */
    @PostMapping("/delete")
    @ApiOperation("删除")
    public ResponseVo delete(@RequestBody List<Long> ids) {
        wareSkuService.removeByIds(ids);

        return ResponseVo.ok();
    }

}
