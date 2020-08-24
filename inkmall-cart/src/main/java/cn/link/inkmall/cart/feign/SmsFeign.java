package cn.link.inkmall.cart.feign;

import cn.link.inkmall.sms.api.SmsApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @Author: Link
 * @Date: 2020/6/2 20:38
 * @Version 1.0
 */
@FeignClient("wms-service")
public interface SmsFeign extends SmsApi {
}
