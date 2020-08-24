package cn.link.inkmall.pms.vo;

import cn.link.inkmall.pms.entity.AttrEntity;
import cn.link.inkmall.pms.entity.AttrGroupEntity;
import lombok.Data;
import lombok.Setter;

import java.util.List;

/**
 * 该类为添加spu时记录spu属性组及属性组下属性的对象
 * @Author: Link
 * @Date: 2020/5/13 10:48
 * @Version 1.0
 */

@Data
public class SpuAttrVo extends AttrGroupEntity {

    private List<AttrEntity> attrEntities;

}
