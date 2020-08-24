package cn.link.inkmall.pms.service.impl;

import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.link.inkmall.common.bean.PageResultVo;
import cn.link.inkmall.common.bean.PageParamVo;

import cn.link.inkmall.pms.mapper.CommentReplayMapper;
import cn.link.inkmall.pms.entity.CommentReplayEntity;
import cn.link.inkmall.pms.service.CommentReplayService;


@Service("commentReplayService")
public class CommentReplayServiceImpl extends ServiceImpl<CommentReplayMapper, CommentReplayEntity> implements CommentReplayService {

    @Override
    public PageResultVo queryPage(PageParamVo paramVo) {
        IPage<CommentReplayEntity> page = this.page(
                paramVo.getPage(),
                new QueryWrapper<CommentReplayEntity>()
        );

        return new PageResultVo(page);
    }

}