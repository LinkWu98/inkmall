package cn.link.inkmall.order.feign;

import cn.link.inkmall.oms.api.OmsApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @Author: Link
 * @Date: 2020/6/5 12:46
 * @Version 1.0
 */
@FeignClient("oms-service")
public interface OmsFeign extends OmsApi {
}
