package cn.link.inkmall.pms.api;

import cn.link.inkmall.common.bean.PageParamVo;
import cn.link.inkmall.common.bean.ResponseVo;
import cn.link.inkmall.pms.entity.*;
import cn.link.inkmall.pms.vo.CategoryVo;
import cn.link.inkmall.pms.vo.ItemAttrGroupVo;
import cn.link.inkmall.pms.vo.ItemAttrValueVo;
import cn.link.inkmall.pms.vo.ItemCategoryVo;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * pms服务远程调用接口
 *
 * @Author: Link
 * @Date: 2020/5/17 20:43
 * @Version 1.0
 */
public interface PmsApi {

    /**
     * 分页查询spu
     *
     * @param pageParamVo
     * @return
     */
    @PostMapping("pms/spu/page")
    ResponseVo<List<SpuEntity>> getSpusByPage(@RequestBody PageParamVo pageParamVo);

    /**
     * 通过指定spuId(spu，基本商品模型)查询sku(sku, 具体销售商品)
     *
     * @param spuId
     * @return
     */
    @GetMapping("pms/sku/spu/{spuId}")
    ResponseVo<List<SkuEntity>> getSkuBySpuId(@PathVariable("spuId") Long spuId);

    /**
     * 查询品牌信息
     *
     * @param id
     * @return
     */
    @GetMapping("pms/brand/{id}")
    ResponseVo<BrandEntity> queryBrandById(@PathVariable("id") Long id);

    /**
     * 查询分类信息
     *
     * @param id
     * @return
     */
    @GetMapping("pms/category/{id}")
    ResponseVo<CategoryEntity> queryCategoryById(@PathVariable("id") Long id);

    /**
     * 通过categoryId, type(区分销售和基本属性), searchType(是否检索属性)获取对应属性
     *
     * @param categoryId
     * @param type
     * @param searchType
     * @return
     */
    @GetMapping("pms/attr/category/{categoryId}")
    ResponseVo<List<AttrEntity>> getAttrByCidTypeAndSearchType(@PathVariable("categoryId") Long categoryId,
                                                               @RequestParam(value = "type", required = false) Integer type,
                                                               @RequestParam(value = "searchType", required = false) Integer searchType);

    /**
     * 查询spu的搜索属性值
     *
     * @param spuId
     * @param attrIds
     * @return
     */
    @GetMapping("pms/spuattrvalue/search/attr/value")
    ResponseVo<List<SpuAttrValueEntity>> getSpuSearchAttrValueBySpuIdAndAttrIds(@RequestParam Long spuId,
                                                                                @RequestParam List<Long> attrIds);


    /**
     * 查询sku的搜索属性值
     *
     * @param skuId
     * @param attrIds
     * @return
     */
    @GetMapping("pms/skuattrvalue/search/attr/value")
    ResponseVo<List<SkuAttrValueEntity>> getSkuSearchAttrValueBySpuIdAndAttrIds(@RequestParam Long skuId,
                                                                                @RequestParam List<Long> attrIds);

    /**
     * 通过 spuId 查询 spu
     *
     * @param id
     * @return
     */
    @GetMapping("pms/spu/{id}")
    ResponseVo<SpuEntity> querySpuById(@PathVariable("id") Long id);

    /**
     * 商品管理分类维护时查询指定parentId的分类Category
     *
     * @param parentId
     * @return
     */
    @GetMapping("pms/category/parent/{parentId}")
    ResponseVo<List<CategoryEntity>> getCategoryByParentId(@PathVariable("parentId") Long parentId);

    /**
     * 通过一级分类id查询二级和三级分类
     *
     * @param pid
     * @return
     */
    @GetMapping("pms/category/getLevelTwoAndThree/{pid}")
    ResponseVo<List<CategoryVo>> getLevelTwoAndThreeByLevelOneId(@PathVariable("pid") Long pid);


    /**
     * 通过三级分类id查询二级和一级分类(商品详情页用)
     */
    @GetMapping("pms/category/getLevelOneAndTwo/{levelThreeCid}")
    ResponseVo<List<ItemCategoryVo>> getLevelOneAndTwo(@PathVariable("levelThreeCid") Long levelThreeCid);


    /**
     * 通过 spuId 获取 spu 下 所有sku的销售属性及属性值
     */
    @GetMapping("item/attrs/{spuId}")
    ResponseVo<List<ItemAttrValueVo>> getItemAttrValuesBySpuId(@PathVariable("spuId") Long spuId);


    /**
     * 通过 categoryId 、spuId 、skuId 获取属性组下所有属性及属性值
     */
    @GetMapping("pms/attrgroup/item/attrgroup")
    ResponseVo<List<ItemAttrGroupVo>> getItemAttrGroups(@RequestParam("categoryId") Long categoryId,
                                                        @RequestParam("spuId") Long spuId,
                                                        @RequestParam("skuId") Long skuId);

    /**
     * 通过 skuId 获取 sku
     */
    @GetMapping("pms/sku/{id}")
    ResponseVo<SkuEntity> querySkuById(@PathVariable("id") Long id);


    /**
     * 通过 skuId 查询sku的图片
     */
    @GetMapping("pms/skuimages/images/{skuId}")
    ResponseVo<List<SkuImagesEntity>> getImagesBySkuId(@PathVariable("skuId") Long skuId);


    /**
     * 通过 spuId 查询 描述信息
     */
    @GetMapping("pms/skuimages/{spuId}")
    @ApiOperation("详情查询")
    ResponseVo<SpuDescEntity> querySpuDescById(@PathVariable("spuId") Long spuId);

    /**
     * 通过 skuId 获取 sku 属性值
     */
    @GetMapping("pms/skuattrvalue/attr/value/{skuId}")
    ResponseVo<List<SkuAttrValueEntity>> getSkuAttrValuesBySkuId(@PathVariable("skuId") Long skuId);

    /**
     * 通过 skuId 查询 sku 的默认图片
     */
    @GetMapping("pms/skuimages/default/{skuId}")
    ResponseVo<SkuImagesEntity> getDefaultImageBySkuId(@PathVariable("skuId") Long skuId);

}
