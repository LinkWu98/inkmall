package cn.link.inkmall.oms.feign;

import cn.link.inkmall.pms.api.PmsApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @Author: Link
 * @Date: 2020/6/6 23:27
 * @Version 1.0
 */
@FeignClient("pms-service")
public interface PmsFeign extends PmsApi {
}
