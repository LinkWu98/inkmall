package cn.link.inkmall.ums.service.impl;

import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.link.inkmall.common.bean.PageResultVo;
import cn.link.inkmall.common.bean.PageParamVo;

import cn.link.inkmall.ums.mapper.UserCollectSkuMapper;
import cn.link.inkmall.ums.entity.UserCollectSkuEntity;
import cn.link.inkmall.ums.service.UserCollectSkuService;


@Service("userCollectSkuService")
public class UserCollectSkuServiceImpl extends ServiceImpl<UserCollectSkuMapper, UserCollectSkuEntity> implements UserCollectSkuService {

    @Override
    public PageResultVo queryPage(PageParamVo paramVo) {
        IPage<UserCollectSkuEntity> page = this.page(
                paramVo.getPage(),
                new QueryWrapper<UserCollectSkuEntity>()
        );

        return new PageResultVo(page);
    }

}