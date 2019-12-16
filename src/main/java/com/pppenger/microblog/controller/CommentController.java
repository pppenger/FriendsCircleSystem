package com.pppenger.microblog.controller;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.ConstraintViolationException;

import com.pppenger.microblog.domin.Blog;
import com.pppenger.microblog.domin.Comment;
import com.pppenger.microblog.domin.User;
import com.pppenger.microblog.domin.Vote;
import com.pppenger.microblog.result.CodeMsg;
import com.pppenger.microblog.result.Result;
import com.pppenger.microblog.service.BlogService;
import com.pppenger.microblog.service.CommentService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;


/**
 * 主页控制器.
 *
 * @since 1.0.0 2017年3月8日
 * @author <a href="https://waylau.com">Way Lau</a>
 */
@Controller
@RequestMapping("/comments")
public class CommentController {

    @Autowired
    private BlogService blogService;

    @Autowired
    private CommentService commentService;

    /**
     * 获取评论列表
     * @param blogId
     * @param model
     * @return
     */
    @GetMapping
    public String listComments(@RequestParam(value="blogId",required=true) Long blogId, Model model) {
        // 判断操作用户是否是评论的所有者
        String commentOwner = "";
        User principal = null;
        if (SecurityContextHolder.getContext().getAuthentication() !=null && SecurityContextHolder.getContext().getAuthentication().isAuthenticated()
                &&  !SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString().equals("anonymousUser")) {
            principal = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (principal !=null) {
                commentOwner = principal.getUsername();
            }
        }
        Blog blog = blogService.getBlogById(blogId);
        List<Comment> comments = blog.getComments();

        if (principal !=null) {
            for (Comment comment : comments){
                List list1 = new ArrayList();
                for (Vote vote : comment.getVotes()){
                    vote.getUser().getUsername().equals(principal.getUsername());
                    list1.add(vote);
                    break;
                }
                comment.setVotes(list1);
            }
        }

        List<Comment> hotList = comments.stream().sorted(Comparator.comparing(Comment::getVoteSize).reversed()).limit(5).collect(Collectors.toList());
        List<Comment> timeList = comments.stream().sorted(Comparator.comparing(Comment::getCreateTime).reversed()).collect(Collectors.toList());


        model.addAttribute("commentOwner", commentOwner);
//        model.addAttribute("comments", comments);
        model.addAttribute("hotcomments", hotList);
        model.addAttribute("timecomments", timeList);
        return "/userspace/blog :: #mainContainerRepleace";
    }
    /**
     * 发表评论
     * @param blogId
     * @param commentContent
     * @return
     */
    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_USER')")  // 指定角色权限才能操作方法
    @ResponseBody
    public Result createComment(Long blogId, String commentContent,String toUserName) {
        try {
            blogService.createComment(blogId, commentContent,toUserName);
        } catch (ConstraintViolationException e)  {
            throw e;
        } catch (Exception e) {
            throw e;
        }
        return Result.success("创建评论成功");
    }

//    /**
//     * 删除评论
//     * @return
//     */
//    @DeleteMapping("/{id}")
//    public Result deleteBlog(@PathVariable("id") Long id, Long blogId) {
//        boolean isOwner = false;
//        User user = commentService.getCommentById(id).getFormUser();
//        // 判断操作用户是否是博客的所有者
//        if (SecurityContextHolder.getContext().getAuthentication() !=null && SecurityContextHolder.getContext().getAuthentication().isAuthenticated()
//                &&  !SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString().equals("anonymousUser")) {
//            User principal = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//            if (principal !=null && user.getUsername().equals(principal.getUsername())) {
//                isOwner = true;
//            }
//        }
//        if (!isOwner) {
//            return Result.error(CodeMsg.HAVE_NOT_AUTHORITY);
//        }
//        try {
//            blogService.removeComment(blogId, id);
//            commentService.removeComment(id);
//        } catch (ConstraintViolationException e)  {
//            throw e;
//        } catch (Exception e) {
//            throw e;
//        }
//        return Result.success("删除评论成功");
//    }
    /**
     * 更新评论(设为默认值)
     * @return
     */
    @DeleteMapping("/{id}")
    @ResponseBody
    public Result deleteBlog(@PathVariable("id") Long id) {
        boolean isOwner = false;
        User user = commentService.getCommentById(id).getFormUser();
        // 判断操作用户是否是博客的所有者
        if (SecurityContextHolder.getContext().getAuthentication() !=null && SecurityContextHolder.getContext().getAuthentication().isAuthenticated()
                &&  !SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString().equals("anonymousUser")) {
            User principal = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (principal !=null && user.getUsername().equals(principal.getUsername())) {
                isOwner = true;
            }
        }
        if (!isOwner) {
            return Result.error(CodeMsg.HAVE_NOT_AUTHORITY);
        }
        try {
            commentService.updateComment(id);
        } catch (ConstraintViolationException e)  {
            throw e;
        } catch (Exception e) {
            throw e;
        }
        return Result.success("评论内容已被系统删除");
    }

}
