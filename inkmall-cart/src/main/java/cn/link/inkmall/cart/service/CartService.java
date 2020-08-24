package cn.link.inkmall.cart.service;

import cn.link.inkmall.cart.bean.CartItem;

import java.util.List;

/**
 * @Author: Link
 * @Date: 2020/6/2 19:34
 * @Version 1.0
 */
public interface CartService {
    void addCartItem(CartItem cartItem);

    List<CartItem> queryCartItems();

    void updateCount(CartItem cartItem);

    void deleteCartItem(Long skuId);

    void updateCheck(CartItem cartItem);

    List<CartItem> getCheckedCartItemsByUserId(Long userId);
}
