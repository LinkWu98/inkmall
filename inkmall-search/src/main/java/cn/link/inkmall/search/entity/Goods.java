package cn.link.inkmall.search.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Date;
import java.util.List;

/**
 *
 * 数据库中的商品数据 -> es的商品对象（sku为基准）
 *
 * @Author: Link
 * @Date: 2020/5/17 20:13
 * @Version 1.0
 */
@Data
@Document(indexName = "goods", type = "info", shards = 3, replicas = 2)
public class Goods {

    @Id
    private Long skuId;

    @Field(type = FieldType.Keyword, index = false)
    private String defaultImage;

    /**
     * 匹配分词查询的属性 match
     */
    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String title;

    /**
     * 聚合属性 agg
     */
    @Field(type = FieldType.Long)
    private Long brandId;

    @Field(type = FieldType.Keyword)
    private String brandName;

    @Field(type = FieldType.Keyword, index = false)
    private String logo;

    @Field(type = FieldType.Long)
    private Long categoryId;

    @Field(type = FieldType.Keyword)
    private String categoryName;

    /**
     * 过滤属性 filter
     */
    @Field(type = FieldType.Integer)
    private Long sales = 0L;

    @Field(type = FieldType.Date)
    private Date createTime;

    @Field(type = FieldType.Double)
    private Double price;

    @Field(type = FieldType.Boolean)
    private boolean store = false;

    /**
     * 搜索属性 nested
     */
    @Field(type = FieldType.Nested)
    private List<SearchAttrValue> searchAttrs;
}
