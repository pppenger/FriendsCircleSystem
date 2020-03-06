package com.pppenger.microblog.controller;

import com.pppenger.microblog.domin.*;
import com.pppenger.microblog.domin.Collection;
import com.pppenger.microblog.result.CodeMsg;
import com.pppenger.microblog.result.Result;
import com.pppenger.microblog.service.*;
import com.pppenger.microblog.vo.BlogVO;
import com.pppenger.microblog.vo.CollectionBlogVO;
import com.pppenger.microblog.vo.CollectionVO;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;


@Controller
@RequestMapping("/u")
public class UserspaceController {
    @Value("${blogScore}")
    private Integer blogScore;

        @Autowired
        private UserDetailsService userDetailsService;

        @Autowired
        private UserService userService;

        @Autowired
        private UserspaceService userspaceService;

        @Autowired
        private BlogService blogService;

        @Autowired
        private CatalogService catalogService;

        @Autowired
        private CollectionService collectionService;
    @Autowired
    private TopService topService;

        @GetMapping("/{username}")
        public String userSpace(@PathVariable("username") String username, Model model) {
            User user = (User)userDetailsService.loadUserByUsername(username);
            model.addAttribute("user", user);
            return "redirect:/u/" + username + "/blogs";
        }

        @GetMapping("/{username}/profile")
        @PreAuthorize("authentication.name.equals(#username)")
        public ModelAndView profile(@PathVariable("username") String username, Model model) {
            User  user = (User)userDetailsService.loadUserByUsername(username);
            model.addAttribute("user", user);
            return new ModelAndView("/userspace/profile", "userModel", model);
        }

//    @GetMapping
//    public String  test(){
//        return "/userspace/profile";
//    }
    /**
     * 保存个人设置
     * @return
     */
    @PostMapping("/{username}/profile")
    @PreAuthorize("authentication.name.equals(#username)")
    public String saveProfile(@PathVariable("username") String username,User user) {
        //不需要修改头像，查出来的时候就已经带头像了
        User originalUser = userService.getUserById(user.getId());
        originalUser.setEmail(user.getEmail());
        originalUser.setUsername(user.getUsername());
//        originalUser.setName(user.getName());

        // 判断密码是否做了变更
        String rawPassword = originalUser.getPassword();
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        String encodePasswd = encoder.encode(user.getPassword());
        //比较传过来的密码加密后和数据库的密码有没有区别
        boolean isMatch = encoder.matches(rawPassword, encodePasswd);
        if (!isMatch) {
            originalUser.setEncodePassword(user.getPassword());
        }

        userService.saveUser(originalUser);
        return "redirect:/u/" + username + "/profile";
    }

    @PostMapping(value = "/{username}/avatar")
    @PreAuthorize("authentication.name.equals(#username)")
    @ResponseBody
    public Result<String> saveAvatar(@PathVariable("username") String username,
                                     @RequestParam("imagefile") MultipartFile imagefile) {

        String msg = userspaceService.saveAvatar(username,imagefile);
        return Result.success(msg);
        //return "redirect:/u/" + username + "/profile";
    }
//
//    @PostMapping("/iitoyo/avatarr")
//    @PreAuthorize("authentication.name.equals(#username)")
//    public String saveAvatar(@RequestParam("username") String username) {
//        StringBuilder sb=new StringBuilder();
//
//        String msg=sb.toString();
//        System.out.println(msg);
//        return msg;
//    }


    @GetMapping("/{username}/blogs")
    public String listBlogsByOrder(@PathVariable("username") String username,
                                   @RequestParam(value="order",required=false,defaultValue="new") String order, //排序，默认按新排序
                                   @RequestParam(value="catalogId",required=false ) Long catalogId,       //类别
                                   @RequestParam(value="keyword",required=false,defaultValue="" ) String keyword,   //关键字
                                   @RequestParam(value="async",required=false) boolean async,
                                   @RequestParam(value="pageIndex",required=false,defaultValue="0") int pageIndex,
                                   @RequestParam(value="pageSize",required=false,defaultValue="10") int pageSize,
                                   Model model) {
        User  user = (User)userDetailsService.loadUserByUsername(username);
        model.addAttribute("user", user);

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
            page = blogService.listBlogsByUserAndCatalogAndTitleLikeAndSort(user,catalogId, keyword, pageable);
        }
        if (order.equals("new")) { // 最新查询【直接按插入时间查询】
            Pageable pageable = new PageRequest(pageIndex, pageSize);
            page = blogService.listBlogsByUserAndCatalogAndTitleLike(user,catalogId, keyword, pageable);
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
        //return "/userspace/blog";

        return (async==true?"/userspace/u :: #mainContainerRepleace":"/userspace/u");
//        return (async==true?new ModelAndView("/userspace/u :: #mainContainerRepleace", "model", model):
//                new ModelAndView("/userspace/u", "model", model));
    }

    /**
     * 获取博客展示界面
     * @param id
     * @param model
     * @return
     */
    @GetMapping("/{username}/blogs/{id}")
    public String getBlogById(@PathVariable("username") String username,@PathVariable("id") Long id, Model model) {
        User principal = null;
        Blog blog = blogService.getBlogById(id);
//        // 每次读取，简单的可以认为阅读量增加1次
//        blogService.readingIncrease(id);

        // 判断操作用户是否是博客的所有者
        boolean isBlogOwner = false;
        User loginUser = null;
        if (SecurityContextHolder.getContext().getAuthentication() !=null && SecurityContextHolder.getContext().getAuthentication().isAuthenticated()
                &&  !SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString().equals("anonymousUser")) {
            principal = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (principal !=null){
                loginUser = userService.getUserById(principal.getId());
            }
            if (principal !=null && username.equals(principal.getUsername())) {
                isBlogOwner = true;
            }
        }
        model.addAttribute("loginUser", loginUser);

        // 判断操作用户的点赞情况
        List<Vote> votes = blog.getVotes();
        Vote currentVote = null; // 当前用户的点赞情况

        if (principal !=null) {
            for (Vote vote : votes) {
                if (vote.getUser().getUsername().equals(principal.getUsername())){
                    currentVote = vote;
                    break;
                }
            }

            for (Comment comment : blog.getComments()){
                List list2 = new ArrayList();
                for (Vote vote : comment.getVotes()){
                    if (vote.getUser().getUsername().equals(principal.getUsername())){
                        list2.add(vote);
                        break;
                    }
                }
                comment.setVotes(list2);
            }
        }

        BlogVO blogvo =new BlogVO(blog.getId(), blog.getTitle(), blog.getSummary(), blog.getUser(), blog.getCreateTime(), blog.getReadSize(), blog.getCommentSize(), blog.getVoteSize(), blog.getReportSize(), blog.getComments(), blog.getVotes(), blog.getPictures(), blog.getCatalog(), null);
        if (principal !=null){
            List<Collection> collectionList = collectionService.findByUser(principal);
            if (collectionList.size()>0) {
                List<CollectionBlogVO> collectionBlogVOS = collectionService.selectCBbyCollIds(collectionList.stream().map(Collection::getId).collect(Collectors.toList()));
                Map<Long, Long> BlogIdCollIdMap = collectionBlogVOS.stream().collect(Collectors.toMap(CollectionBlogVO::getBlogId, CollectionBlogVO::getCollectionId, (o, n) -> o));
                if (!StringUtils.isEmpty(BlogIdCollIdMap)) {
                    blogvo.setCollectionId(BlogIdCollIdMap.get(blogvo.getId()));
                }
            }
        }


        model.addAttribute("isBlogOwner", isBlogOwner);
        model.addAttribute("blog",blogvo);
        model.addAttribute("currentVote",currentVote);
        return "/userspace/blog";
    }


    /**
     * 删除博客
     * @param id
     * @param model
     * @return
     */
    @DeleteMapping("/{username}/blogs/{id}")
    @PreAuthorize("authentication.name.equals(#username)")
    @ResponseBody
    public Result deleteBlog(@PathVariable("username") String username,@PathVariable("id") Long id) {

        try {
            collectionService.delByBlogId(id);
            blogService.removeBlog(id);
        } catch (Exception e) {
            return Result.error(CodeMsg.SERVER_ERROR);
        }

        String redirectUrl = "/u/" + username + "/blogs";
        return Result.success(redirectUrl);
    }


    /**
     * 获取编辑博客的界面
     * @param model
     * @return
     */
    @GetMapping("/{username}/blogs/edit/{id}")
    public ModelAndView editBlog(@PathVariable("username") String username,@PathVariable("id") Long id, Model model) {
        model.addAttribute("blog", blogService.getBlogById(id));
        return new ModelAndView("/userspace/blogedit", "blogModel", model);
    }

    /**
     * 保存博客
     * @param username
     * @param blog
     * @return
     */
    @PostMapping("/{username}/blogs/edit")
    @PreAuthorize("authentication.name.equals(#username)")
    @ResponseBody
    public Result saveBlog(@PathVariable("username") String username,
                           @RequestParam(value = "id",required=false)  String id,
                           @RequestParam("title") String title,
                           @RequestParam("summary") String summary,
                           @RequestParam("miaosha") String miaosha,
                           @RequestParam(value = "catalogId",required=false) Long catalogId,
                           @RequestParam(value = "fileURL",required = false) String[] fileURL) {
        User loaduser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (userService.getUserById(loaduser.getId()).getScore()<blogScore){
            return Result.error(CodeMsg.HAVE_NOT_SCORE);
        }
        User user = (User)userDetailsService.loadUserByUsername(username);
        Blog blog=new Blog();
        List<Picture> pictureList = new ArrayList();
        if (fileURL!=null){
            for (String url:fileURL){
                Picture picture = new Picture(user,url);
                pictureList.add(picture);
            }
            blog.setPictures(pictureList);
        }

        if (id!=null) {
            blog.setId(Long.parseLong(id));
        }
        blog.setUser(user);
        blog.setTitle(title);
        blog.setSummary(summary);
        if (catalogId!=null){
            blog.setCatalog(catalogService.getCatalogById(catalogId));
        }
        Blog saveblog = blogService.saveBlog(blog);

        if ("TOP1".equals(miaosha)){
            Top top1=topService.findOne((long) 1);
            if (user.getUsername().equals(top1.getTopUsername())){
                user.setVoteSize(user.getVoteSize()/2);
                userService.saveUser(user);

                top1.setBlogOwnerUsername(user.getUsername());
                top1.setTitle(title);
                top1.setBlogId(saveblog.getId());
                top1.setHaveSend(1);
                topService.save(top1);
            }
        }


        //String redirectUrl = "/u/" + username + "/blogs/" + blog.getId();
        return Result.success("处理成功");
    }


}
