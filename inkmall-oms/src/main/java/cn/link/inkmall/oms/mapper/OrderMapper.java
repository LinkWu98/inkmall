package cn.link.inkmall.oms.mapper;

import cn.link.inkmall.oms.entity.OrderEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单
 * 
 * @author link
 * @email linkwu981002@gmail.com
 * @date 2020-05-11 23:01:21
 */
@Mapper
public interface OrderMapper extends BaseMapper<OrderEntity> {

    int closeOrder(String orderToken);

    int paySuccess(String orderToken);
}
