package cn.link.inkmall.order.feign;

import cn.link.inkmall.cart.api.CartApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @Author: Link
 * @Date: 2020/6/5 12:44
 * @Version 1.0
 */
@FeignClient("cart-service")
public interface CartFeign extends CartApi {
}
