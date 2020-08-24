package cn.link.inkmall.item.controller;

import cn.link.inkmall.common.bean.ResponseVo;
import cn.link.inkmall.item.service.ItemService;
import cn.link.inkmall.item.vo.ItemVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: Link
 * @Date: 2020/5/31 11:13
 * @Version 1.0
 */
@RestController
@RequestMapping("item")
public class ItemController {

    @Autowired
    ItemService itemService;

    /**
     * 获取商品详情对象
     */
    @GetMapping("{skuId}")
    public ResponseVo<ItemVo> getItemsDetail(@PathVariable("skuId") Long skuId) {

        ItemVo itemVo = itemService.getItemsDetail(skuId);

        return ResponseVo.ok(itemVo);

    }

}
