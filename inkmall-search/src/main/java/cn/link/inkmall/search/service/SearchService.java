package cn.link.inkmall.search.service;

import cn.link.inkmall.search.entity.SearchParamVo;
import cn.link.inkmall.search.entity.SearchResponseVo;

/**
 * @Author: Link
 * @Date: 2020/5/21 21:16
 * @Version 1.0
 */
public interface SearchService {


    SearchResponseVo search(SearchParamVo searchParamVo);
}
