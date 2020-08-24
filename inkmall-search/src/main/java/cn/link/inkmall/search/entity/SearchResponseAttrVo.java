package cn.link.inkmall.search.entity;

import lombok.Data;

import java.util.List;

/**
 * 封装响应数据的聚合参数对象
 * @Author: Link
 * @Date: 2020/5/21 22:35
 * @Version 1.0
 */
@Data
public class SearchResponseAttrVo {

    private Long attrId;

    private String attrName;

    private List<String> attrValues;

}
