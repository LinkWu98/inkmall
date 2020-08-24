package cn.link.inkmall.search.controller;

import cn.link.inkmall.common.bean.ResponseVo;
import cn.link.inkmall.search.entity.SearchParamVo;
import cn.link.inkmall.search.entity.SearchResponseVo;
import cn.link.inkmall.search.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: Link
 * @Date: 2020/5/21 21:03
 * @Version 1.0
 */
@RestController
@RequestMapping("search")
public class SearchController {

    @Autowired
    SearchService searchService;

    /**
     * es查询
     * @param searchParamVo
     * @return
     */
    @GetMapping
    public ResponseVo<SearchResponseVo> doSearch(SearchParamVo searchParamVo){
        SearchResponseVo searchResponseVo = searchService.search(searchParamVo);
        return ResponseVo.ok(searchResponseVo);
    }

}
