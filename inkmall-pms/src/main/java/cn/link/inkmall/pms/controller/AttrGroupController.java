package cn.link.inkmall.pms.controller;

import java.util.List;

import cn.link.inkmall.pms.vo.ItemAttrGroupVo;
import cn.link.inkmall.pms.vo.SpuAttrVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
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

import cn.link.inkmall.pms.entity.AttrGroupEntity;
import cn.link.inkmall.pms.service.AttrGroupService;
import cn.link.inkmall.common.bean.PageResultVo;
import cn.link.inkmall.common.bean.ResponseVo;
import cn.link.inkmall.common.bean.PageParamVo;

/**
 * 属性分组
 *
 * @author link
 * @email linkwu981002@gmail.com
 * @date 2020-05-11 22:03:17
 */
@Api(tags = "属性分组 管理")
@RestController
@RequestMapping("pms/attrgroup")
public class AttrGroupController {

    @Autowired
    private AttrGroupService attrGroupService;

    /**
     * 通过 categoryId 、spuId 、skuId 获取属性组下所有属性及属性值
     */
    @GetMapping("item/attrgroup")
    public ResponseVo<List<ItemAttrGroupVo>> getItemAttrGroups(@RequestParam("categoryId") Long categoryId,
                                                               @RequestParam("spuId") Long spuId,
                                                               @RequestParam("skuId") Long skuId) {

        List<ItemAttrGroupVo> itemAttrGroupVos = attrGroupService.getItemAttrGroups(categoryId, spuId, skuId);

        return ResponseVo.ok(itemAttrGroupVos);

    }

    /**
     * 通过三级categoryId获取SpuAttrVo对象(封装了属性组及对应的spu属性(基本属性))
     * @param cid
     * @return
     */
    @GetMapping("withattrs/{cid}")
    public ResponseVo<List<SpuAttrVo>> getSpuAttrsByCid(@PathVariable("cid") Long cid){

        List<SpuAttrVo> spuAttrVos = attrGroupService.getSpuAttrsByCid(cid);

        return ResponseVo.ok(spuAttrVos);

    }

    /**
     * 通过三级分类id获取该分类的属性组集
     * @param cid
     * @return
     */
    @GetMapping("category/{cid}")
    public ResponseVo<List<AttrGroupEntity>> getAttrGroupByCategoryId(@PathVariable("cid") Long cid){

        List<AttrGroupEntity> attrGroupEntities = attrGroupService.list(new QueryWrapper<AttrGroupEntity>().eq("category_id", cid));

        return ResponseVo.ok(attrGroupEntities);

    }

    /**
     * 列表
     */
    @GetMapping
    @ApiOperation("分页查询")
    public ResponseVo<PageResultVo> list(PageParamVo paramVo){
        PageResultVo pageResultVo = attrGroupService.queryPage(paramVo);

        return ResponseVo.ok(pageResultVo);
    }


    /**
     * 信息
     */
    @GetMapping("{id}")
    @ApiOperation("详情查询")
    public ResponseVo<AttrGroupEntity> queryAttrGroupById(@PathVariable("id") Long id){
		AttrGroupEntity attrGroup = attrGroupService.getById(id);

        return ResponseVo.ok(attrGroup);
    }

    /**
     * 保存
     */
    @PostMapping
    @ApiOperation("保存")
    public ResponseVo<Object> save(@RequestBody AttrGroupEntity attrGroup){
		attrGroupService.save(attrGroup);

        return ResponseVo.ok();
    }

    /**
     * 修改
     */
    @PostMapping("/update")
    @ApiOperation("修改")
    public ResponseVo update(@RequestBody AttrGroupEntity attrGroup){
		attrGroupService.updateById(attrGroup);

        return ResponseVo.ok();
    }

    /**
     * 删除
     */
    @PostMapping("/delete")
    @ApiOperation("删除")
    public ResponseVo delete(@RequestBody List<Long> ids){
		attrGroupService.removeByIds(ids);

        return ResponseVo.ok();
    }

}
