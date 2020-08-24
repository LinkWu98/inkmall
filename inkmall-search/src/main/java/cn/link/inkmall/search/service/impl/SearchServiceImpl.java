package cn.link.inkmall.search.service.impl;

import cn.link.inkmall.search.entity.Goods;
import cn.link.inkmall.search.entity.SearchParamVo;
import cn.link.inkmall.search.entity.SearchResponseAttrVo;
import cn.link.inkmall.search.entity.SearchResponseVo;
import cn.link.inkmall.search.service.SearchService;
import com.alibaba.fastjson.JSON;
import org.apache.commons.lang.StringUtils;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.NestedQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.nested.NestedAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.nested.ParsedNested;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedLongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @Author: Link
 * @Date: 2020/5/21 21:16
 * @Version 1.0
 */
@Service
public class SearchServiceImpl implements SearchService {

    @Autowired
    RestHighLevelClient restHighLevelClient;

    @Override
    public SearchResponseVo search(SearchParamVo searchParamVo) {

        try {
            //1. 构建dsl语句
            SearchSourceBuilder searchSourceBuilder = this.createDsl(searchParamVo);
            System.out.println(searchSourceBuilder);
            SearchRequest searchRequest = new SearchRequest(new String[]{"goods"}, searchSourceBuilder);
            //2. 执行搜索
            SearchResponse response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            //3. 解析结果集并返回
            SearchResponseVo searchResponseVo = this.praseSearchResponse(response);
                //页码
            searchResponseVo.setPageNum(searchParamVo.getPageNum().intValue());
                //每页的记录数
            searchResponseVo.setPageSize(searchParamVo.getPageSize().intValue());
            return searchResponseVo;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;

    }

    /**
     * 解析查询后的响应，封装为对象
     * @param response
     * @return
     */
    private SearchResponseVo praseSearchResponse(SearchResponse response) {
        //初始化响应对象
        SearchResponseVo searchResponseVo = new SearchResponseVo();
        SearchHits hits = response.getHits();

        //总记录数
        long total = hits.getTotalHits();
        searchResponseVo.setTotal(total);


        SearchHit[] hitsHits = hits.getHits();
        //商品列表 + 高亮title
        if (hitsHits != null && hitsHits.length > 0) {
            //将每一条记录类型转换为 Goods , 最后返回一个商品列表
            List<Goods> data = Stream.of(hitsHits).map(hitsHit -> {
                //将_source中的json字符串反序列化为goods对象
                Goods goods = JSON.parseObject(hitsHit.getSourceAsString(), Goods.class);
                //高亮
                Map<String, HighlightField> highlightFieldMap = hitsHit.getHighlightFields();
                if (!CollectionUtils.isEmpty(highlightFieldMap)) {
                    goods.setTitle(highlightFieldMap.get("title").fragments()[0].toString());
                }
                return goods;
            }).collect(Collectors.toList());
            searchResponseVo.setData(data);
        }

        //聚合结果集(转为map)
        Map<String, Aggregation> aggregationMap = response.getAggregations().asMap();
            //若聚合结果集为空，直接返回
        if (CollectionUtils.isEmpty(aggregationMap)) {
            return searchResponseVo;
        }

        //品牌聚合解析
        ParsedLongTerms brandIdAgg = (ParsedLongTerms)aggregationMap.get("brandIdAgg");
        List<? extends Terms.Bucket> brandIdAggBuckets = brandIdAgg.getBuckets();
        if (!CollectionUtils.isEmpty(brandIdAggBuckets)){
            SearchResponseAttrVo brand = new SearchResponseAttrVo();
            List<String> brandAttrValues = brandIdAggBuckets.stream().map(brandIdAggBucket -> {
                Map<String, Aggregation> brandAggMap = brandIdAggBucket.getAggregations().asMap();
                List<? extends Terms.Bucket> brandNameAggBuckets = ((ParsedStringTerms) brandAggMap.get("brandNameAgg")).getBuckets();
                List<? extends Terms.Bucket> logoAggBuckets = ((ParsedStringTerms) brandAggMap.get("logoAgg")).getBuckets();
                //将id name logo注入到map中
                Map<String, Object> brandMap = new HashMap<>();
                long brandId = brandIdAggBucket.getKeyAsNumber().longValue();
                brandMap.put("id", brandId);
                if (!CollectionUtils.isEmpty(brandNameAggBuckets)) {
                    brandMap.put("name", brandNameAggBuckets.get(0).getKeyAsString());
                }

                if (!CollectionUtils.isEmpty(logoAggBuckets)) {
                    brandMap.put("logo", logoAggBuckets.get(0).getKeyAsString());
                }

                //map解析为json字符串
                return JSON.toJSONString(brandMap);

            }).collect(Collectors.toList());
            brand.setAttrId(null);
            brand.setAttrName("品牌");
            brand.setAttrValues(brandAttrValues);
            searchResponseVo.setBrand(brand);
        }

        //分类聚合解析
        ParsedLongTerms categoryIdAgg = (ParsedLongTerms)aggregationMap.get("categoryIdAgg");
        List<? extends Terms.Bucket> categoryIdAggBuckets = categoryIdAgg.getBuckets();
        if (!CollectionUtils.isEmpty(categoryIdAggBuckets)) {
            SearchResponseAttrVo category = new SearchResponseAttrVo();
            List<String> categoryAttrValues = categoryIdAggBuckets.stream().map(categoryIdAggBucket -> {
                //将id name注入到map中
                Map<String, Object> categoryMap = new HashMap<>();
                categoryMap.put("id", ((Terms.Bucket) categoryIdAggBucket).getKeyAsString());
                ParsedStringTerms categoryNameAgg = (ParsedStringTerms) ((Terms.Bucket) categoryIdAggBucket).getAggregations().asMap().get("categoryNameAgg");
                List<? extends Terms.Bucket> categoryNameAggBuckets = categoryNameAgg.getBuckets();
                if (!CollectionUtils.isEmpty(categoryNameAggBuckets)) {
                    categoryMap.put("name", categoryIdAggBuckets.get(0).getKeyAsString());
                }
                return JSON.toJSONString(categoryMap);
            }).collect(Collectors.toList());

            category.setAttrId(null);
            category.setAttrName("分类");
            category.setAttrValues(categoryAttrValues);
            searchResponseVo.setCategory(category);
        }

        //嵌套聚合解析
        ParsedNested attrAgg = (ParsedNested)aggregationMap.get("attrAgg");
        Map<String, Aggregation> attrAggMap = attrAgg.getAggregations().asMap();
        if (CollectionUtils.isEmpty(attrAggMap)) {
            return searchResponseVo;
        }
        ParsedLongTerms attrIdAgg = (ParsedLongTerms)attrAggMap.get("attrIdAgg");
        List<? extends Terms.Bucket> attrIdAggBuckets = attrIdAgg.getBuckets();
        if (!CollectionUtils.isEmpty(attrIdAggBuckets)) {

            //AttrIdAggs中每一个bucket就是一个过滤规格参数
            List<SearchResponseAttrVo> filters = attrIdAggBuckets.stream().map(attrIdAggBucket -> {
                SearchResponseAttrVo filter = new SearchResponseAttrVo();
                Map<String, Aggregation> attrIdAggMap = ((Terms.Bucket) attrIdAggBucket).getAggregations().asMap();
                ParsedStringTerms attrNameAgg = (ParsedStringTerms) attrIdAggMap.get("attrNameAgg");
                ParsedStringTerms attrValueAgg = (ParsedStringTerms) attrIdAggMap.get("attrValueAgg");

                List<? extends Terms.Bucket> attrValueAggBuckets = attrValueAgg.getBuckets();
                if (!CollectionUtils.isEmpty(attrValueAggBuckets)) {
                    //过滤属性的属性值（集合）
                    List<String> attrValues = attrValueAggBuckets.stream().map(Terms.Bucket::getKeyAsString).collect(Collectors.toList());
                    filter.setAttrValues(attrValues);
                }

                //过滤属性的id
                filter.setAttrId(((Terms.Bucket) attrIdAggBucket).getKeyAsNumber().longValue());
                //过滤属性的name
                filter.setAttrName(attrNameAgg.getBuckets().get(0).getKeyAsString());
                return filter;
            }).collect(Collectors.toList());

            searchResponseVo.setFilters(filters);

        }


        return searchResponseVo;
    }


    /**
     * 构建 dsl 语句
     * @param searchParamVo
     * @return
     */
    private SearchSourceBuilder createDsl(SearchParamVo searchParamVo) {

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        String keyword = searchParamVo.getKeyword();
        //非空校验，若关键字为空，直接返回
        if (!StringUtils.isNotBlank(keyword)){
            //可以打广告哦
            return null;
        }

        //分页
        int pageNum = searchParamVo.getPageNum().intValue();
        int pageSize = searchParamVo.getPageSize().intValue();
        searchSourceBuilder.from((pageNum - 1) * pageSize);
        searchSourceBuilder.size(pageSize);

        //排序 sort=1:desc (1-price，2-createTime, 3-sales, 默认得分)
        String sort = searchParamVo.getSort();
        if (StringUtils.isNotBlank(sort)){
            String[] sortSplit = StringUtils.split(sort, ":");
            //防止恶意填写数据访问，校验长度
            if(sortSplit.length == 2){
                String order = sortSplit[0];
                switch (order) {
                    case "1" : order = "price"; break;
                    case "2" : order = "createTime"; break;
                    case "3" : order = "sales"; break;
                    default: order = "_score"; break;
                }
                searchSourceBuilder.sort(order, StringUtils.equals("desc", sortSplit[1]) ? SortOrder.DESC : SortOrder.ASC);
            }
        }

        //高亮
        searchSourceBuilder.highlighter(new HighlightBuilder().field("title").preTags("<font style='color:red'>").postTags("</font>"));

        //_source
        searchSourceBuilder.fetchSource(new String[]{"price", "skuId", "defaultImage", "title"}, null);



        //查询 - bool查询
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
            //1. 匹配查询
        boolQuery.must(QueryBuilders.matchQuery("title", keyword));
            //2. 过滤查询
                //2.1 品牌过滤
        List<Long> brandId = searchParamVo.getBrandId();
        if (!CollectionUtils.isEmpty(brandId)) {
            boolQuery.filter(QueryBuilders.termsQuery("brandId", brandId));
        }
                //2.2 分类过滤
        Long categoryId = searchParamVo.getCategoryId();
        if (categoryId != null) {
            boolQuery.filter(QueryBuilders.termsQuery("categoryId", categoryId.toString()));
        }
                //2.3 嵌套属性过滤 格式:props=5:麒麟-高通, 6:12G-8G
        List<String> props = searchParamVo.getProps();
        if (!CollectionUtils.isEmpty(props)) {
            props.forEach(prop -> {
                String[] splitProp = StringUtils.split(prop, ":");
                //防止恶意填写数据访问，校验长度
                if (splitProp.length == 2){
                    String attrId = splitProp[0];
                    String[] attrValues = StringUtils.split(splitProp[1], "-");
                    //构建每个nested的bool查询对象
                    BoolQueryBuilder nestedBoolQuery = QueryBuilders.boolQuery();
                    nestedBoolQuery.must(QueryBuilders.termQuery("searchAttrs.attrId", attrId));
                    nestedBoolQuery.must(QueryBuilders.termsQuery("searchAttrs.attrValue.keyword", attrValues));
                    NestedQueryBuilder nestedQuery = QueryBuilders.nestedQuery("searchAttrs", nestedBoolQuery, ScoreMode.None);
                    //将嵌套查询注入bool查询的filter查询中
                    boolQuery.filter(nestedQuery);
                }
            });
        }

                //2.4 范围过滤（价格）
        Double priceForm = searchParamVo.getPriceForm();
        Double priceTo = searchParamVo.getPriceTo();
                //其中一个不为空就加入过滤
        if (priceForm != null || priceTo != null) {
            RangeQueryBuilder rangeQuery = QueryBuilders.rangeQuery("price");
            if (priceForm != null) {
                rangeQuery.gte(priceForm);
            }
            if (priceTo != null) {
                rangeQuery.lte(priceTo);
            }
            boolQuery.filter(rangeQuery);
        }
                //2.5 库存过滤
        Boolean store = searchParamVo.getStore();
        if (store != null) {
            boolQuery.filter(QueryBuilders.termQuery("store", store));
        }

        searchSourceBuilder.query(boolQuery);

        //聚合
            //1. 品牌聚合
        TermsAggregationBuilder brandIdAgg = AggregationBuilders.terms("brandIdAgg").field("brandId")
                .subAggregation(AggregationBuilders.terms("brandNameAgg").field("brandName"))
                .subAggregation(AggregationBuilders.terms("logoAgg").field("logo"));
            //2. 分类聚合
        TermsAggregationBuilder categoryAgg = AggregationBuilders.terms("categoryIdAgg").field("categoryId")
                .subAggregation(AggregationBuilders.terms("categoryNameAgg").field("categoryName"));
            //3. 嵌套聚合
        NestedAggregationBuilder attrAgg = AggregationBuilders.nested("attrAgg", "searchAttrs")
                .subAggregation(AggregationBuilders.terms("attrIdAgg").field("searchAttrs.attrId")
                .subAggregation(AggregationBuilders.terms("attrNameAgg").field("searchAttrs.attrName.keyword"))
                .subAggregation(AggregationBuilders.terms("attrValueAgg").field("searchAttrs.attrValue.keyword")));

        searchSourceBuilder.aggregation(brandIdAgg);
        searchSourceBuilder.aggregation(categoryAgg);
        searchSourceBuilder.aggregation(attrAgg);

        return searchSourceBuilder;


    }

}
