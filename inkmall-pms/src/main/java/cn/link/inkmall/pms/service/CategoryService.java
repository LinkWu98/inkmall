package cn.link.inkmall.pms.service;

import cn.link.inkmall.pms.vo.CategoryVo;
import cn.link.inkmall.pms.vo.ItemCategoryVo;
import com.baomidou.mybatisplus.extension.service.IService;
import cn.link.inkmall.common.bean.PageResultVo;
import cn.link.inkmall.common.bean.PageParamVo;
import cn.link.inkmall.pms.entity.CategoryEntity;

import java.util.List;

/**
 * 商品三级分类
 *
 * @author link
 * @email linkwu981002@gmail.com
 * @date 2020-05-11 22:03:17
 */
public interface CategoryService extends IService<CategoryEntity> {

    PageResultVo queryPage(PageParamVo paramVo);

    List<CategoryEntity> getCategoryByparentId(Long parentId);

    List<CategoryVo> getLevelTwoAndThreeByLevelOneId(Long pid);

    List<ItemCategoryVo> getLevelOneAndTwo(Long levelThreeCid);
}

