package cn.link.inkmall.item.feign;

import cn.link.inkmall.sms.api.SmsApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @Author: Link
 * @Date: 2020/5/31 14:25
 * @Version 1.0
 */
@FeignClient("sms-service")
public interface SmsFeign extends SmsApi {
}
