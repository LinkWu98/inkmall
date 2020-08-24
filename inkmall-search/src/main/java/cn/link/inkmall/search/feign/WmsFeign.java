package cn.link.inkmall.search.feign;

import cn.link.inkmall.wms.api.WmsApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @Author: Link
 * @Date: 2020/5/18 2:23
 * @Version 1.0
 */
@FeignClient("wms-service")
public interface WmsFeign extends WmsApi {
}
