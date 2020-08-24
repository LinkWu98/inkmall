package cn.link.inkmall.pms.service.impl;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.link.inkmall.common.bean.PageResultVo;
import cn.link.inkmall.common.bean.PageParamVo;

import cn.link.inkmall.pms.mapper.AttrMapper;
import cn.link.inkmall.pms.entity.AttrEntity;
import cn.link.inkmall.pms.service.AttrService;


@Service("attrService")
public class AttrServiceImpl extends ServiceImpl<AttrMapper, AttrEntity> implements AttrService {

    @Override
    public PageResultVo queryPage(PageParamVo paramVo) {
        IPage<AttrEntity> page = this.page(
                paramVo.getPage(),
                new QueryWrapper<AttrEntity>()
        );

        return new PageResultVo(page);
    }

    @Override
    public List<AttrEntity> getAttrByCidTypeAndSearchType(Long categoryId, Integer type, Integer searchType) {

        QueryWrapper<AttrEntity> wrapper = new QueryWrapper<>();

        wrapper.eq("category_id", categoryId);

        if (type != null) {
            wrapper.eq("type", type);
        }

        if (searchType != null) {
            wrapper.eq("search_type", searchType);
        }

        return this.list(wrapper);

    }

}