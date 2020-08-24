package cn.link.inkmall.order.feign;

import cn.link.inkmall.pms.api.PmsApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @Author: Link
 * @Date: 2020/6/5 12:42
 * @Version 1.0
 */
@FeignClient("pms-service")
public interface PmsFeign extends PmsApi {
}
