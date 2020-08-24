package cn.link.inkmall.oms.api;

import cn.link.inkmall.common.bean.ResponseVo;
import cn.link.inkmall.oms.entity.OrderEntity;
import cn.link.inkmall.oms.vo.OrderSubmitVo;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @Author: Link
 * @Date: 2020/6/5 12:39
 * @Version 1.0
 */
public interface OmsApi {

    /**
     * 保存订单和订单详情
     */
    @PostMapping("oms/order")
    ResponseVo<OrderEntity> save(@RequestBody OrderSubmitVo orderSubmitVo);

}
