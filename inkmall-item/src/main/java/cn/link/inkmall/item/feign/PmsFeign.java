package cn.link.inkmall.item.feign;

import cn.link.inkmall.pms.api.PmsApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @Author: Link
 * @Date: 2020/5/31 14:24
 * @Version 1.0
 */
@FeignClient("pms-service")
public interface PmsFeign extends PmsApi {
}
