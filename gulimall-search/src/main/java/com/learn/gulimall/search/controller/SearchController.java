package com.learn.gulimall.search.controller;

import com.learn.gulimall.search.service.MallSearchService;
import com.learn.gulimall.search.vo.SearchParam;
import com.learn.gulimall.search.vo.SearchResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * packageName = com.learn.gulimall.search.controller
 * author = Casey
 * Data = 2020/4/23 10:03 下午
 **/

@Controller
public class SearchController {
    @Autowired
    MallSearchService mallSearchService;

    /**
     * 将页面提交过来的请求查询参数封装成一个对象
     * @param searchParam
     * @return
     */
    @GetMapping("/list.html")
    public String listPage(SearchParam searchParam, Model model, HttpServletRequest request){
        searchParam.setQueryString(request.getQueryString());
        SearchResult result = mallSearchService.search(searchParam);
        return "list";
    }
}
