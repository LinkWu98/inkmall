package cn.link.inkmall.order.feign;

import cn.link.inkmall.sms.api.SmsApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @Author: Link
 * @Date: 2020/6/5 12:43
 * @Version 1.0
 */
@FeignClient("sms-service")
public interface SmsFeign extends SmsApi{
}
