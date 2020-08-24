package cn.link.inkmall.pms.controller;

import java.util.List;

import cn.link.inkmall.pms.vo.CategoryVo;
import cn.link.inkmall.pms.vo.ItemCategoryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cn.link.inkmall.pms.entity.CategoryEntity;
import cn.link.inkmall.pms.service.CategoryService;
import cn.link.inkmall.common.bean.PageResultVo;
import cn.link.inkmall.common.bean.ResponseVo;
import cn.link.inkmall.common.bean.PageParamVo;

/**
 * 商品三级分类
 *
 * @author link
 * @email linkwu981002@gmail.com
 * @date 2020-05-11 22:03:17
 */
@Api(tags = "商品三级分类 管理")
@RestController
@RequestMapping("pms/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    //TODO 项目回顾练手 ：添加分类时，双删缓存中的分类

    /**
     * 通过三级分类id查询二级和一级分类(商品详情页用)
     */
    @GetMapping("getLevelOneAndTwo/{levelThreeCid}")
    public ResponseVo<List<ItemCategoryVo>> getLevelOneAndTwo(@PathVariable("levelThreeCid") Long levelThreeCid) {

        List<ItemCategoryVo> itemCategoryVos = categoryService.getLevelOneAndTwo(levelThreeCid);

        return ResponseVo.ok(itemCategoryVos);

    }

   /**
    * 通过一级分类id查询二级和三级分类
    * @param pid
    * @return
    */
    @GetMapping("getLevelTwoAndThree/{pid}")
    public ResponseVo<List<CategoryVo>> getLevelTwoAndThreeByLevelOneId(@PathVariable("pid") Long pid) {

        List<CategoryVo> categoryVos = categoryService.getLevelTwoAndThreeByLevelOneId(pid);
        return ResponseVo.ok(categoryVos);
    }

    /**
     * 商品管理分类维护时查询指定parentId的分类Category
     * @param parentId
     * @return
     */
    @GetMapping("parent/{parentId}")
    public ResponseVo<List<CategoryEntity>> getCategoryByParentId(@PathVariable("parentId") Long parentId){

        List<CategoryEntity> categoryEntities = categoryService.getCategoryByparentId(parentId);

        return ResponseVo.ok(categoryEntities);

    }

    /**
     * 列表
     */
    @GetMapping
    @ApiOperation("分页查询")
    public ResponseVo<PageResultVo> list(PageParamVo paramVo){
        PageResultVo pageResultVo = categoryService.queryPage(paramVo);

        return ResponseVo.ok(pageResultVo);
    }


    /**
     * 信息
     */
    @GetMapping("{id}")
    @ApiOperation("详情查询")
    public ResponseVo<CategoryEntity> queryCategoryById(@PathVariable("id") Long id){
		CategoryEntity category = categoryService.getById(id);

        return ResponseVo.ok(category);
    }

    /**
     * 保存
     */
    @PostMapping
    @ApiOperation("保存")
    public ResponseVo<Object> save(@RequestBody CategoryEntity category){
		categoryService.save(category);

        return ResponseVo.ok();
    }

    /**
     * 修改
     */
    @PostMapping("/update")
    @ApiOperation("修改")
    public ResponseVo update(@RequestBody CategoryEntity category){
		categoryService.updateById(category);

        return ResponseVo.ok();
    }

    /**
     * 删除
     */
    @PostMapping("/delete")
    @ApiOperation("删除")
    public ResponseVo delete(@RequestBody List<Long> ids){
		categoryService.removeByIds(ids);

        return ResponseVo.ok();
    }

}
