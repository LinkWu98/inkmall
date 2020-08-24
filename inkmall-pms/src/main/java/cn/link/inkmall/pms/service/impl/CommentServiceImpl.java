package cn.link.inkmall.pms.service.impl;

import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.link.inkmall.common.bean.PageResultVo;
import cn.link.inkmall.common.bean.PageParamVo;

import cn.link.inkmall.pms.mapper.CommentMapper;
import cn.link.inkmall.pms.entity.CommentEntity;
import cn.link.inkmall.pms.service.CommentService;


@Service("commentService")
public class CommentServiceImpl extends ServiceImpl<CommentMapper, CommentEntity> implements CommentService {

    @Override
    public PageResultVo queryPage(PageParamVo paramVo) {
        IPage<CommentEntity> page = this.page(
                paramVo.getPage(),
                new QueryWrapper<CommentEntity>()
        );

        return new PageResultVo(page);
    }

}