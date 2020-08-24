package cn.link.inkmall.oms.service.impl;

import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.link.inkmall.common.bean.PageResultVo;
import cn.link.inkmall.common.bean.PageParamVo;

import cn.link.inkmall.oms.mapper.OrderOperateHistoryMapper;
import cn.link.inkmall.oms.entity.OrderOperateHistoryEntity;
import cn.link.inkmall.oms.service.OrderOperateHistoryService;


@Service("orderOperateHistoryService")
public class OrderOperateHistoryServiceImpl extends ServiceImpl<OrderOperateHistoryMapper, OrderOperateHistoryEntity> implements OrderOperateHistoryService {

    @Override
    public PageResultVo queryPage(PageParamVo paramVo) {
        IPage<OrderOperateHistoryEntity> page = this.page(
                paramVo.getPage(),
                new QueryWrapper<OrderOperateHistoryEntity>()
        );

        return new PageResultVo(page);
    }

}