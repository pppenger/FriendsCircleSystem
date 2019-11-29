package com.pppenger.microblog.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 主页控制器.
 *
 * @since 1.0.0 2017年3月8日
 * @author <a href="https://waylau.com">Way Lau</a>
 */
@Controller
@RequestMapping("/blogs")
public class BlogController {

    /**
     * 获取微博列表
     * @param order 排序方式——默认按时间排序
     * @param partition 分区
     * @return
     */
    @GetMapping
    public String listBlogs(@RequestParam(value="order",required=false,defaultValue="new") String order,
                            @RequestParam(value="tag",required=false) Long tag) {
        System.out.print("order:" +order + ";tag:" +tag );
        return "redirect:/index?order="+order+"&tag="+tag;
    }

}