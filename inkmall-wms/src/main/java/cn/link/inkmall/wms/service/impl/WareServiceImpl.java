package cn.link.inkmall.wms.service.impl;

import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.link.inkmall.common.bean.PageResultVo;
import cn.link.inkmall.common.bean.PageParamVo;

import cn.link.inkmall.wms.mapper.WareMapper;
import cn.link.inkmall.wms.entity.WareEntity;
import cn.link.inkmall.wms.service.WareService;


@Service("wareService")
public class WareServiceImpl extends ServiceImpl<WareMapper, WareEntity> implements WareService {

    @Override
    public PageResultVo queryPage(PageParamVo paramVo) {
        IPage<WareEntity> page = this.page(
                paramVo.getPage(),
                new QueryWrapper<WareEntity>()
        );

        return new PageResultVo(page);
    }

}