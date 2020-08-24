package cn.link.inkmall.index.controller;

import cn.link.inkmall.common.bean.ResponseVo;
import cn.link.inkmall.index.service.IndexService;
import cn.link.inkmall.pms.vo.CategoryVo;
import cn.link.inkmall.pms.entity.CategoryEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 首页数据控制器
 * @Author: Link
 * @Date: 2020/5/26 20:58
 * @Version 1.0
 */
@RequestMapping("index")
@RestController
public class IndexController {

    @Autowired
    IndexService indexService;

    /**
     * 加载首页时，查询所有一级分类
     * @return
     */
    @GetMapping("cates")
    public ResponseVo<List<CategoryEntity>> getLevelOneCategories() {

        List<CategoryEntity> categoryEntities = indexService.getLevelOneCategories();

        return ResponseVo.ok(categoryEntities);
    }

    @GetMapping("cates/{pid}")
    public ResponseVo<List<CategoryVo>> getLevelTwoAndLevelThreeCategories(@PathVariable("pid") Long pid) {

        List<CategoryVo> categoryVos = indexService.getLevelTwoAndLevelThreeCategories(pid);

        return ResponseVo.ok(categoryVos);

    }


}
