package cn.link.inkmall.ums.service.impl;

import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.link.inkmall.common.bean.PageResultVo;
import cn.link.inkmall.common.bean.PageParamVo;

import cn.link.inkmall.ums.mapper.IntegrationHistoryMapper;
import cn.link.inkmall.ums.entity.IntegrationHistoryEntity;
import cn.link.inkmall.ums.service.IntegrationHistoryService;


@Service("integrationHistoryService")
public class IntegrationHistoryServiceImpl extends ServiceImpl<IntegrationHistoryMapper, IntegrationHistoryEntity> implements IntegrationHistoryService {

    @Override
    public PageResultVo queryPage(PageParamVo paramVo) {
        IPage<IntegrationHistoryEntity> page = this.page(
                paramVo.getPage(),
                new QueryWrapper<IntegrationHistoryEntity>()
        );

        return new PageResultVo(page);
    }

}