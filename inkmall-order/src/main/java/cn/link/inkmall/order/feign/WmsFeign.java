package cn.link.inkmall.order.feign;

import cn.link.inkmall.wms.api.WmsApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @Author: Link
 * @Date: 2020/6/5 12:43
 * @Version 1.0
 */
@FeignClient("wms-service")
public interface WmsFeign extends WmsApi {
}
