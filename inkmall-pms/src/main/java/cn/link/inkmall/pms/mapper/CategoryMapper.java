package cn.link.inkmall.pms.mapper;

import cn.link.inkmall.pms.entity.CategoryEntity;
import cn.link.inkmall.pms.vo.CategoryVo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 商品三级分类
 * 
 * @author link
 * @email linkwu981002@gmail.com
 * @date 2020-05-11 22:03:17
 */
@Mapper
public interface CategoryMapper extends BaseMapper<CategoryEntity> {

    List<CategoryVo> getLevelTwoAndThreeByLevelOneId(Long pid);
}
