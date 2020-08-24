package cn.link.inkmall.wms.service.impl;

import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.link.inkmall.common.bean.PageResultVo;
import cn.link.inkmall.common.bean.PageParamVo;

import cn.link.inkmall.wms.mapper.PurchaseDetailMapper;
import cn.link.inkmall.wms.entity.PurchaseDetailEntity;
import cn.link.inkmall.wms.service.PurchaseDetailService;


@Service("purchaseDetailService")
public class PurchaseDetailServiceImpl extends ServiceImpl<PurchaseDetailMapper, PurchaseDetailEntity> implements PurchaseDetailService {

    @Override
    public PageResultVo queryPage(PageParamVo paramVo) {
        IPage<PurchaseDetailEntity> page = this.page(
                paramVo.getPage(),
                new QueryWrapper<PurchaseDetailEntity>()
        );

        return new PageResultVo(page);
    }

}