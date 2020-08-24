package cn.link.inkmall.cart.controller;

import cn.link.inkmall.cart.bean.CartItem;
import cn.link.inkmall.cart.service.CartService;
import cn.link.inkmall.common.bean.ResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Author: Link
 * @Date: 2020/6/2 19:33
 * @Version 1.0
 */
@RestController
@RequestMapping("cart")
public class CartController {

    @Autowired
    CartService cartService;

    /**
     * 通过 userId 获取缓存中的购物车选中的商品
     */
    @GetMapping("cart/{userId}")
    public ResponseVo<List<CartItem>> getCheckedCartItemsByUserId(@PathVariable("userId") Long userId) {

        List<CartItem> cartItems = cartService.getCheckedCartItemsByUserId(userId);

        return ResponseVo.ok(cartItems);

    }

    /**
     * 添加商品到购物车
     */
    @PostMapping
    public ResponseVo addCartItem(@RequestBody CartItem cartItem) {

        cartService.addCartItem(cartItem);

        return ResponseVo.ok();

    }

    /**
     * 查询购物车商品
     */
    @GetMapping
    public ResponseVo<List<CartItem>> queryCartItems() {

        List<CartItem> cartItems = cartService.queryCartItems();

        return ResponseVo.ok(cartItems);

    }

    /**
     * 修改商品数量
     */
    @PostMapping("update")
    public ResponseVo updateCount(@RequestBody CartItem cartItem) {

        cartService.updateCount(cartItem);

        return ResponseVo.ok();

    }

    /**
     * 删除购物车商品
     */
    @PostMapping("{skuId}")
    public ResponseVo deleteCartItem(@PathVariable("skuId") Long skuId) {

        cartService.deleteCartItem(skuId);

        return ResponseVo.ok();

    }

    /**
     * 更新商品选中状态
     */
    @PostMapping("update/check")
    public ResponseVo updateCheck(@RequestBody CartItem cartItem) {

        cartService.updateCheck(cartItem);

        return ResponseVo.ok();

    }


}
