package cn.link.inkmall.cart.api;

import cn.link.inkmall.cart.bean.CartItem;
import cn.link.inkmall.common.bean.ResponseVo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * @Author: Link
 * @Date: 2020/6/5 12:44
 * @Version 1.0
 */
public interface CartApi {

    /**
     * 通过 userId 获取缓存中的购物车
     */
    @GetMapping("cart/cart/{userId}")
    ResponseVo<List<CartItem>> getCheckedCartItemsByUserId(@PathVariable("userId") Long userId);

}
