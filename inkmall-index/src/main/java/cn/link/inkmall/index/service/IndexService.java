package cn.link.inkmall.index.service;

import cn.link.inkmall.pms.vo.CategoryVo;
import cn.link.inkmall.pms.entity.CategoryEntity;

import java.util.List;

/**
 * @Author: Link
 * @Date: 2020/5/26 21:00
 * @Version 1.0
 */
public interface IndexService {
    List<CategoryEntity> getLevelOneCategories();

    List<CategoryVo> getLevelTwoAndLevelThreeCategories(Long pid);
}
