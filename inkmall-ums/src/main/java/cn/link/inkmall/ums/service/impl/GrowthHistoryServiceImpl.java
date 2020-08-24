package cn.link.inkmall.ums.service.impl;

import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.link.inkmall.common.bean.PageResultVo;
import cn.link.inkmall.common.bean.PageParamVo;

import cn.link.inkmall.ums.mapper.GrowthHistoryMapper;
import cn.link.inkmall.ums.entity.GrowthHistoryEntity;
import cn.link.inkmall.ums.service.GrowthHistoryService;


@Service("growthHistoryService")
public class GrowthHistoryServiceImpl extends ServiceImpl<GrowthHistoryMapper, GrowthHistoryEntity> implements GrowthHistoryService {

    @Override
    public PageResultVo queryPage(PageParamVo paramVo) {
        IPage<GrowthHistoryEntity> page = this.page(
                paramVo.getPage(),
                new QueryWrapper<GrowthHistoryEntity>()
        );

        return new PageResultVo(page);
    }

}