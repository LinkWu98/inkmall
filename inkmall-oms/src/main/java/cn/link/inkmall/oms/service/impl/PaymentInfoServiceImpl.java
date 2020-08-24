package cn.link.inkmall.oms.service.impl;

import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.link.inkmall.common.bean.PageResultVo;
import cn.link.inkmall.common.bean.PageParamVo;

import cn.link.inkmall.oms.mapper.PaymentInfoMapper;
import cn.link.inkmall.oms.entity.PaymentInfoEntity;
import cn.link.inkmall.oms.service.PaymentInfoService;


@Service("paymentInfoService")
public class PaymentInfoServiceImpl extends ServiceImpl<PaymentInfoMapper, PaymentInfoEntity> implements PaymentInfoService {

    @Override
    public PageResultVo queryPage(PageParamVo paramVo) {
        IPage<PaymentInfoEntity> page = this.page(
                paramVo.getPage(),
                new QueryWrapper<PaymentInfoEntity>()
        );

        return new PageResultVo(page);
    }

}