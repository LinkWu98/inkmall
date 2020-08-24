package cn.link.inkmall.pms.vo;

import cn.link.inkmall.pms.entity.SpuAttrValueEntity;
import lombok.Data;
import org.apache.commons.lang.StringUtils;

import java.util.List;

/**
 * 封装了页面新增spu时传来的Spu基本属性的对象
 * @Author: Link
 * @Date: 2020/5/14 15:56
 * @Version 1.0
 */
@Data
public class SpuAttrValueVo extends SpuAttrValueEntity {

    public void setValueSelected(List<String> valueSelected) {
        this.setAttrValue(StringUtils.join(valueSelected, ","));
    }

}
