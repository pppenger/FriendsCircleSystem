package com.pppenger.microblog.controller;


import com.pppenger.microblog.result.Result;
import com.pppenger.microblog.service.BlogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 主页控制器.
 *
 * @since 1.0.0 2017年3月8日
 * @author <a href="https://waylau.com">Way Lau</a>
 */
@Controller
@RequestMapping("/blogs")
public class BlogController {

    @Autowired
    private BlogService blogService;

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


    /**
     * 上传微博图片接口
     * @param order 排序方式——默认按时间排序
     * @param partition 分区
     * @return
     */
    @PostMapping(value = "/uploadPictures")
    @ResponseBody
    public Result<List> uploadPictures(@RequestParam("files") MultipartFile[] multipartFiles) {

        List list = blogService.uploadPictures(multipartFiles);
        return Result.success(list);
    }

}