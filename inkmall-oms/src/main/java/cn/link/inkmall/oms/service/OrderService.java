package cn.link.inkmall.oms.service;

import cn.link.inkmall.oms.vo.OrderSubmitVo;
import com.baomidou.mybatisplus.extension.service.IService;
import cn.link.inkmall.common.bean.PageResultVo;
import cn.link.inkmall.common.bean.PageParamVo;
import cn.link.inkmall.oms.entity.OrderEntity;

import java.util.Map;

/**
 * 订单
 *
 * @author link
 * @email linkwu981002@gmail.com
 * @date 2020-05-11 23:01:21
 */
public interface OrderService extends IService<OrderEntity> {

    PageResultVo queryPage(PageParamVo paramVo);

    OrderEntity saveOrder(OrderSubmitVo orderSubmitVo);
}

