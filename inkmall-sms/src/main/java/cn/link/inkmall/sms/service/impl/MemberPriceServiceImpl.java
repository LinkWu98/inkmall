package cn.link.inkmall.sms.service.impl;

import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.link.inkmall.common.bean.PageResultVo;
import cn.link.inkmall.common.bean.PageParamVo;

import cn.link.inkmall.sms.mapper.MemberPriceMapper;
import cn.link.inkmall.sms.entity.MemberPriceEntity;
import cn.link.inkmall.sms.service.MemberPriceService;


@Service("memberPriceService")
public class MemberPriceServiceImpl extends ServiceImpl<MemberPriceMapper, MemberPriceEntity> implements MemberPriceService {

    @Override
    public PageResultVo queryPage(PageParamVo paramVo) {
        IPage<MemberPriceEntity> page = this.page(
                paramVo.getPage(),
                new QueryWrapper<MemberPriceEntity>()
        );

        return new PageResultVo(page);
    }

}