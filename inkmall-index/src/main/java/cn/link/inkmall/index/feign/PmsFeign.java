package cn.link.inkmall.index.feign;

import cn.link.inkmall.pms.api.PmsApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @Author: Link
 * @Date: 2020/5/26 21:03
 * @Version 1.0
 */
@FeignClient("pms-service")
public interface PmsFeign extends PmsApi {
}
