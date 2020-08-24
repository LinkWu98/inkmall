package cn.link.inkmall.pms.vo;

import cn.link.inkmall.pms.entity.CategoryEntity;
import lombok.Data;

import java.util.List;

/**
 * @Author: Link
 * @Date: 2020/5/26 21:06
 * @Version 1.0
 */
@Data
public class CategoryVo extends CategoryEntity {

    List<CategoryEntity> subs;

}
