package cn.link.inkmall.pms.vo;

import lombok.Data;

import java.util.List;

/**
 * 商品详情页规格参数对象
 * @Author: Link
 * @Date: 2020/5/31 13:57
 * @Version 1.0
 */
@Data
public class ItemAttrGroupVo {

    private String groupName;

    private List<ItemAttrValueVo> groupAttrs;

}
