package com.pppenger.microblog.controller;


import com.pppenger.microblog.domin.Blog;
import com.pppenger.microblog.domin.Top;
import com.pppenger.microblog.domin.User;
import com.pppenger.microblog.domin.es.EsBlog;
import com.pppenger.microblog.result.Result;
import com.pppenger.microblog.service.BlogService;
import com.pppenger.microblog.service.EsBlogService;
import com.pppenger.microblog.service.TopService;
import com.pppenger.microblog.service.UserService;
import com.pppenger.microblog.vo.BlogVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

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
    private UserDetailsService userDetailsService;
    @Autowired
    private UserService userService;
    @Autowired
    private BlogService blogService;
    @Autowired
    private EsBlogService esBlogService;
    @Autowired
    private TopService topService;


    @GetMapping
    public String listBlogsByOrder(
                                   @RequestParam(value="order",required=false,defaultValue="new") String order, //排序，默认按新排序
                                   @RequestParam(value="catalogId",required=false ) Long catalogId,       //类别
                                   @RequestParam(value="keyword",required=false,defaultValue="" ) String keyword,   //关键字
                                   @RequestParam(value="async",required=false) boolean async,
                                   @RequestParam(value="pageIndex",required=false,defaultValue="0") int pageIndex,
                                   @RequestParam(value="pageSize",required=false,defaultValue="10") int pageSize,
                                   Model model) {
        //获取当前登录用户
        User principal = null;
        User loginUser = null;
        if (SecurityContextHolder.getContext().getAuthentication() !=null && SecurityContextHolder.getContext().getAuthentication().isAuthenticated()
                &&  !SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString().equals("anonymousUser")) {
            principal = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (principal !=null) {
                loginUser = userService.getUserById(principal.getId());
            }
        }
        model.addAttribute("loginUser", loginUser);


        Page<Blog> page = null;
        if (order.equals("hot")) { // 最热查询【先按阅读量评论量等排序一个Sort，然后再去查询】
            Sort sort = new Sort(Sort.Direction.DESC,"reading","comments","likes");
            Pageable pageable = new PageRequest(pageIndex, pageSize, sort);
            page = blogService.listBlogsByCatalogLikeAndSort(catalogId, pageable);
        }
        if (order.equals("new")) { // 最新查询【直接按插入时间查询】

            Sort sort = new Sort(Sort.Direction.DESC,"createTime");
            Pageable pageable = new PageRequest(pageIndex, pageSize, sort);
            //Pageable pageable = new PageRequest(pageIndex, pageSize);
            page = blogService.listBlogsByCatalogLike(catalogId, pageable);
        }
        List<Blog> list = page.getContent();	// 当前所在页面数据列表

        //对评论进行筛选
        list = blogService.getBlogTop2HotComment(list);
        //设置博客的评论的赞列表为用户点赞情况
        list = blogService.setBlogVoteAndCommentVoteListToUser(principal,list);

        //设置收藏夹信息
        List<BlogVO> blogVOS = list.stream().map(B -> new BlogVO(B.getId(), B.getTitle(), B.getSummary(), B.getUser(), B.getCreateTime(), B.getReadSize(), B.getCommentSize(), B.getVoteSize(), B.getReportSize(), B.getComments(), B.getVotes(), B.getPictures(), B.getCatalog(), null)).collect(Collectors.toList());
        blogVOS = blogService.setBlogCollectionIdByUser(principal,blogVOS);
        model.addAttribute("order", order);
        model.addAttribute("page", page);
        model.addAttribute("blogList", blogVOS);

        return (async==true?"/index :: #mainContainerRepleace":"/index");
    }


    @GetMapping("/searchtest")
    public String search(){
        return "/searchBlog";
    }


    @GetMapping("/search")
    public String searchEsBlogs(
            @RequestParam(value="order",required=false,defaultValue="new") String order,
            @RequestParam(value="keyword",required=false,defaultValue="" ) String keyword,
            @RequestParam(value="async",required=false) boolean async,
            @RequestParam(value="pageIndex",required=false,defaultValue="0") int pageIndex,
            @RequestParam(value="pageSize",required=false,defaultValue="30") int pageSize,
            @RequestParam(value="toURL",required=false) String toURL,
            Model model) {
        Page<EsBlog> page = null;
        List<EsBlog> list = null;
        boolean isEmpty = true; // 系统初始化时，没有博客数据
        try {
            if (order.equals("hot")) { // 最热查询
                Sort sort = new Sort(Sort.Direction.DESC,"voteSize","commentSize","createTime");
                Pageable pageable = new PageRequest(pageIndex, pageSize, sort);
                page = esBlogService.listHotestEsBlogs(keyword, pageable);
            } else if (order.equals("new")) { // 最新查询
                Sort sort = new Sort(Sort.Direction.DESC,"createTime");
                Pageable pageable = new PageRequest(pageIndex, pageSize, sort);
                page = esBlogService.listNewestEsBlogs(keyword, pageable);
            }

            isEmpty = false;
        } catch (Exception e) {
            Pageable pageable = new PageRequest(pageIndex, pageSize);
            page = esBlogService.listEsBlogs(pageable);
        }

        list = page.getContent();	// 当前所在页面数据列表


        model.addAttribute("order", order);
        model.addAttribute("keyword", keyword);
        model.addAttribute("page", page);
        //总页数
//        System.out.println(page.getTotalPages());
        //当前页数-1
//        System.out.println(page.getNumber());
        model.addAttribute("blogList", list);

        if ("/top10".equals(toURL)){
            model.addAttribute("top10List", list);
            return "/index :: #top10";
        }

        return (async==true?"/searchBlog :: #mainContainerRepleace":"/searchBlog");
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


    @GetMapping("/getTOP1")
    @ResponseBody
    public Result getTop1Blog() {
        Top top=topService.findOne((long) 1);
        return Result.success(top);
    }

    @PostMapping(value = "/pickTop1")
    @ResponseBody
    public Result pickTop1(@RequestParam("username") String username) {

        int pick= topService.pickTop(username);
        if (pick>0){

            return Result.success("秒杀成功");
        }else {

            return Result.success("sorry,秒杀失败");
        }
    }


}