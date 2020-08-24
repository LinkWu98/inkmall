package cn.link.inkmall.search.feign;

import cn.link.inkmall.pms.api.PmsApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @Author: Link
 * @Date: 2020/5/17 20:47
 * @Version 1.0
 */
@FeignClient("pms-service")
public interface PmsFeign extends PmsApi {
}
