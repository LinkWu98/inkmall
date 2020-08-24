package cn.link.inkmall.wms.service.impl;

import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.link.inkmall.common.bean.PageResultVo;
import cn.link.inkmall.common.bean.PageParamVo;

import cn.link.inkmall.wms.mapper.WareOrderBillMapper;
import cn.link.inkmall.wms.entity.WareOrderBillEntity;
import cn.link.inkmall.wms.service.WareOrderBillService;


@Service("wareOrderBillService")
public class WareOrderBillServiceImpl extends ServiceImpl<WareOrderBillMapper, WareOrderBillEntity> implements WareOrderBillService {

    @Override
    public PageResultVo queryPage(PageParamVo paramVo) {
        IPage<WareOrderBillEntity> page = this.page(
                paramVo.getPage(),
                new QueryWrapper<WareOrderBillEntity>()
        );

        return new PageResultVo(page);
    }

}