package cn.link.inkmall.auth.feign;

import cn.link.inkmall.ums.api.UmsApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @Author: Link
 * @Date: 2020/6/1 22:18
 * @Version 1.0
 */
@FeignClient("ums-service")
public interface UmsFeign extends UmsApi {
}
