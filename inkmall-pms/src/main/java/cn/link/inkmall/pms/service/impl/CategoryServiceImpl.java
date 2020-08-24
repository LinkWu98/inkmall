package cn.link.inkmall.pms.service.impl;

import cn.link.inkmall.pms.vo.CategoryVo;
import cn.link.inkmall.pms.vo.ItemCategoryVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.link.inkmall.common.bean.PageResultVo;
import cn.link.inkmall.common.bean.PageParamVo;

import cn.link.inkmall.pms.mapper.CategoryMapper;
import cn.link.inkmall.pms.entity.CategoryEntity;
import cn.link.inkmall.pms.service.CategoryService;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, CategoryEntity> implements CategoryService {

    @Autowired
    CategoryMapper categoryMapper;

    @Override
    public PageResultVo queryPage(PageParamVo paramVo) {
        IPage<CategoryEntity> page = this.page(
                paramVo.getPage(),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageResultVo(page);
    }

    @Override
    public List<CategoryEntity> getCategoryByparentId(Long parentId) {

        //如果parentId为-1则查询所有
        if (parentId == -1){

            return baseMapper.selectList(null);

        }

        //不为-1则查询指定分类
        return baseMapper.selectList(new QueryWrapper<CategoryEntity>().eq("parent_id", parentId));

    }

    @Override
    public List<CategoryVo> getLevelTwoAndThreeByLevelOneId(Long pid) {

        return categoryMapper.getLevelTwoAndThreeByLevelOneId(pid);

    }

    @Override
    public List<ItemCategoryVo> getLevelOneAndTwo(Long levelThreeCid) {

        //先查询 三级分类
        CategoryEntity categoryEntity3 = baseMapper.selectById(levelThreeCid);
        if (categoryEntity3 == null) {
            return null;
        }
        List<CategoryEntity> list = new ArrayList<>();
        list.add(categoryEntity3);

        //通过三级分类查询二级分类
        CategoryEntity categoryEntity2 = baseMapper.selectById(categoryEntity3.getParentId());
        if (categoryEntity2 != null) {
            list.add(categoryEntity2);
        }

        //通过三级分类查询一级分类
        CategoryEntity categoryEntity1 = baseMapper.selectById(categoryEntity2.getParentId());
        if (categoryEntity1 != null) {
            list.add(categoryEntity1);
        }

        //转为 ItemCategoryVo 对象返回
        List<ItemCategoryVo> itemCategoryVos = list.stream().map(categoryEntity -> {
            ItemCategoryVo itemCategoryVo = new ItemCategoryVo();
            itemCategoryVo.setCategoryId(categoryEntity.getId());
            itemCategoryVo.setCategoryName(categoryEntity.getName());
            return itemCategoryVo;
        }).collect(Collectors.toList());

        return itemCategoryVos;
    }

}