package cn.link.inkmall.ums.service.impl;

import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.link.inkmall.common.bean.PageResultVo;
import cn.link.inkmall.common.bean.PageParamVo;

import cn.link.inkmall.ums.mapper.UserStatisticsMapper;
import cn.link.inkmall.ums.entity.UserStatisticsEntity;
import cn.link.inkmall.ums.service.UserStatisticsService;


@Service("userStatisticsService")
public class UserStatisticsServiceImpl extends ServiceImpl<UserStatisticsMapper, UserStatisticsEntity> implements UserStatisticsService {

    @Override
    public PageResultVo queryPage(PageParamVo paramVo) {
        IPage<UserStatisticsEntity> page = this.page(
                paramVo.getPage(),
                new QueryWrapper<UserStatisticsEntity>()
        );

        return new PageResultVo(page);
    }

}