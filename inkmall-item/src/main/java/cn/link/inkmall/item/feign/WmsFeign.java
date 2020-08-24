package cn.link.inkmall.item.feign;

import cn.link.inkmall.wms.api.WmsApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @Author: Link
 * @Date: 2020/5/31 14:25
 * @Version 1.0
 */
@FeignClient("wms-feign")
public interface WmsFeign extends WmsApi {
}
