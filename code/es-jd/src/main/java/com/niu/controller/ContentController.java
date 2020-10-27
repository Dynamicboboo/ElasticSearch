package com.niu.controller;

import com.niu.service.ContentService;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @Description ：
 * @Author tj
 * @Date 2020/10/26
 */
@Controller
public class ContentController {
    @Autowired
    private ContentService contentService;

    @GetMapping("/parse/{keyword}")
    public Boolean parse(@PathVariable("keyword") String keywords) throws Exception {
        return contentService.parseContent(keywords);

    }
}
