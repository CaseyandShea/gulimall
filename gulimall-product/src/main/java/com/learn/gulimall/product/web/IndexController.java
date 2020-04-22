package com.learn.gulimall.product.web;

import com.learn.gulimall.product.entity.CategoryEntity;
import com.learn.gulimall.product.service.CategoryService;
import com.learn.gulimall.product.vo.Catelog2Vo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

/**
 * packageName = com.learn.gulimall.product.web
 * author = Casey
 * Data = 2020/4/21 1:14 下午
 **/

@Controller
public class IndexController {
    @Autowired
    CategoryService categoryService;

    @GetMapping({"/","/index.html"})
    public String indexPage(Model model){
        //todo 查出所有的一级分类
       List<CategoryEntity> categoryEntities =  categoryService.getLevel1Categorys();

        //试图解析器进行拼串
        //classpath：/templates
        model.addAttribute("categorys", categoryEntities);

        return "index";
    }

    @ResponseBody
    @GetMapping("/index/catelog.json")
    public  Map<String, List<Catelog2Vo>> getCatelogJson(){
        Map<String, List<Catelog2Vo>> map =   categoryService.getCatelogJson();

        return map;
    }
}
