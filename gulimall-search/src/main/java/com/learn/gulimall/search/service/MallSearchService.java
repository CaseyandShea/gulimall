package com.learn.gulimall.search.service;

import com.learn.gulimall.search.vo.SearchParam;
import com.learn.gulimall.search.vo.SearchResult;

/**
 * packageName = com.learn.gulimall.search.service
 * author = Casey
 * Data = 2020/4/24 12:02 上午
 **/
public interface MallSearchService {
    SearchResult search(SearchParam searchParam );
}
