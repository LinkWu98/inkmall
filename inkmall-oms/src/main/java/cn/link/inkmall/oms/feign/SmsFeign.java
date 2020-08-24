package cn.link.inkmall.oms.feign;

import cn.link.inkmall.pms.api.PmsApi;
import cn.link.inkmall.sms.api.SmsApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @Author: Link
 * @Date: 2020/6/6 23:27
 * @Version 1.0
 */
@FeignClient("sms-service")
public interface SmsFeign extends SmsApi {
}
