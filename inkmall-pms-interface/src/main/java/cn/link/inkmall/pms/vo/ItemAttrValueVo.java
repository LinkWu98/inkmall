package cn.link.inkmall.pms.vo;

import lombok.Data;

/**
 * spu下的sku的销售属性及属性值
 * @Author: Link
 * @Date: 2020/5/31 13:39
 * @Version 1.0
 */
@Data
public class ItemAttrValueVo {

    private Long attrId;

    private String attrName;

    private String attrValue;

}
