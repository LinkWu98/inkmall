package cn.link.inkmall.wms.service.impl;

import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.link.inkmall.common.bean.PageResultVo;
import cn.link.inkmall.common.bean.PageParamVo;

import cn.link.inkmall.wms.mapper.WareOrderBillDetailMapper;
import cn.link.inkmall.wms.entity.WareOrderBillDetailEntity;
import cn.link.inkmall.wms.service.WareOrderBillDetailService;


@Service("wareOrderBillDetailService")
public class WareOrderBillDetailServiceImpl extends ServiceImpl<WareOrderBillDetailMapper, WareOrderBillDetailEntity> implements WareOrderBillDetailService {

    @Override
    public PageResultVo queryPage(PageParamVo paramVo) {
        IPage<WareOrderBillDetailEntity> page = this.page(
                paramVo.getPage(),
                new QueryWrapper<WareOrderBillDetailEntity>()
        );

        return new PageResultVo(page);
    }

}