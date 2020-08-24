package cn.link.inkmall.oms.service.impl;

import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.link.inkmall.common.bean.PageResultVo;
import cn.link.inkmall.common.bean.PageParamVo;

import cn.link.inkmall.oms.mapper.OrderReturnApplyMapper;
import cn.link.inkmall.oms.entity.OrderReturnApplyEntity;
import cn.link.inkmall.oms.service.OrderReturnApplyService;


@Service("orderReturnApplyService")
public class OrderReturnApplyServiceImpl extends ServiceImpl<OrderReturnApplyMapper, OrderReturnApplyEntity> implements OrderReturnApplyService {

    @Override
    public PageResultVo queryPage(PageParamVo paramVo) {
        IPage<OrderReturnApplyEntity> page = this.page(
                paramVo.getPage(),
                new QueryWrapper<OrderReturnApplyEntity>()
        );

        return new PageResultVo(page);
    }

}