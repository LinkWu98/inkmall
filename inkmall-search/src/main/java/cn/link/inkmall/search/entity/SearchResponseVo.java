package cn.link.inkmall.search.entity;

import lombok.Data;

import java.util.List;

/**
 * @Author: Link
 * @Date: 2020/5/21 22:34
 * @Version 1.0
 */
@Data
public class SearchResponseVo {


    /**
     * 品牌过滤 {id:null, attrName:"品牌", attrValue:[{id:"", name:"", logo:""},{id:"", name:"", logo:""}]}
     * attrValue集合中每个值转化为json字符串存入
     */
    private SearchResponseAttrVo brand;

    /**
     * 分类过滤 {id:null, attrName:"品牌", attrValue:[{id:"", name:""},{id:"", name:""}]}
     * attrValue集合中每个值转化为json字符串存入
     */
    private SearchResponseAttrVo category;

    /**
     * 嵌套过滤属性
     */
    private List<SearchResponseAttrVo> filters;

    /**
     * 分页数据
     */
    private Integer pageNum;
    private Integer pageSize;
    private Long total;

    /**
     * 页面商品列表
     */
    private List<Goods> data;

}
