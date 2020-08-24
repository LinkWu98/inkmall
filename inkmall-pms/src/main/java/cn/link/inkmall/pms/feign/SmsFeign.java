package cn.link.inkmall.pms.feign;

import cn.link.inkmall.sms.api.SmsApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @Author: Link
 * @Date: 2020/5/14 17:12
 * @Version 1.0
 */
@FeignClient("sms-service")
public interface SmsFeign extends SmsApi {
}
