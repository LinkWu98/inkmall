package cn.link.inkmall.ums.service.impl;

import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.link.inkmall.common.bean.PageResultVo;
import cn.link.inkmall.common.bean.PageParamVo;

import cn.link.inkmall.ums.mapper.UserLevelMapper;
import cn.link.inkmall.ums.entity.UserLevelEntity;
import cn.link.inkmall.ums.service.UserLevelService;


@Service("userLevelService")
public class UserLevelServiceImpl extends ServiceImpl<UserLevelMapper, UserLevelEntity> implements UserLevelService {

    @Override
    public PageResultVo queryPage(PageParamVo paramVo) {
        IPage<UserLevelEntity> page = this.page(
                paramVo.getPage(),
                new QueryWrapper<UserLevelEntity>()
        );

        return new PageResultVo(page);
    }

}