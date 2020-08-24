package cn.link.inkmall.search.entity;

import lombok.Data;

/**
 * es中商品对象的搜索属性
 * @Author: Link
 * @Date: 2020/5/17 20:24
 * @Version 1.0
 */
@Data
public class SearchAttrValue {

    private Long attrId;

    private String attrName;

    private String attrValue;


}
