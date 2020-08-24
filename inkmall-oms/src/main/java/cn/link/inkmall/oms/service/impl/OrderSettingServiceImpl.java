package cn.link.inkmall.oms.service.impl;

import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.link.inkmall.common.bean.PageResultVo;
import cn.link.inkmall.common.bean.PageParamVo;

import cn.link.inkmall.oms.mapper.OrderSettingMapper;
import cn.link.inkmall.oms.entity.OrderSettingEntity;
import cn.link.inkmall.oms.service.OrderSettingService;


@Service("orderSettingService")
public class OrderSettingServiceImpl extends ServiceImpl<OrderSettingMapper, OrderSettingEntity> implements OrderSettingService {

    @Override
    public PageResultVo queryPage(PageParamVo paramVo) {
        IPage<OrderSettingEntity> page = this.page(
                paramVo.getPage(),
                new QueryWrapper<OrderSettingEntity>()
        );

        return new PageResultVo(page);
    }

}