package cn.link.inkmall.search.entity;

import lombok.Data;

import java.util.List;

/**
 * 搜索对象
 * @Author: Link
 * @Date: 2020/5/21 21:05
 * @Version 1.0
 */
@Data
public class SearchParamVo {

    //search?keyword=高端&brandId=4,5&categoryId=225&props=5:麒麟-高通,6:12G-8G&sort=1:desc&store=true

    //匹配查询
    /**
     * 关键字 keyword=高端
     */
    private String keyword;


    //过滤查询
    /**
     * 商品id，可多选 brandId=4,5
     */
    private List<Long> brandId;

    /**
     * 种类id categoryId=225
     */
    private Long categoryId;

    /**
     * 规格参数属性 props=5:麒麟-高通,6:12G-8G
     */
    private List<String> props;

    /**
     * 分页数据
     */
    private Long pageNum = 1L;

    private Long pageSize = 20L;

    /**
     * 排序 sort=1:desc (1-price，2-createTime, 3-sales, 默认得分)
     */
    private String sort;

    /**
     * 价格区间
     */
    private Double priceForm;
    private Double priceTo;

    /**
     * 是否有货
     */
    private Boolean store;


}
