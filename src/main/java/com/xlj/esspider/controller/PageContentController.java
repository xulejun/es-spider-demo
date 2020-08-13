package com.xlj.esspider.controller;

import com.xlj.esspider.service.PageContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @Author XLJ
 * @Date 2020/8/12
 */

@RestController
public class PageContentController {

    @Autowired
    private PageContentService pageContentService;

    // 解析页面数据
    @GetMapping("/parse/{keyword}")
    public Boolean parse(@PathVariable String keyword) throws IOException {
        return pageContentService.parseContent(keyword);
    }

    // 实现页面搜索
    @GetMapping("/search/{keyword}/{pageNo}/{pageSize}")
    public List<Map<String, Object>> search(@PathVariable String keyword, @PathVariable int PageNo, @PathVariable int pageSize) throws IOException {
        return pageContentService.searchHighLight(keyword, PageNo, pageSize);
    }


}
