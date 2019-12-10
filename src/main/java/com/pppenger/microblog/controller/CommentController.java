package com.pppenger.microblog.controller;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.ConstraintViolationException;

import com.pppenger.microblog.domin.Blog;
import com.pppenger.microblog.domin.Comment;
import com.pppenger.microblog.domin.User;
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
        Blog blog = blogService.getBlogById(blogId);
        List<Comment> comments = blog.getComments();
        List<Comment> hotList = comments.stream().sorted(Comparator.comparing(Comment::getVoteSize).reversed()).collect(Collectors.toList());
        List<Comment> timeList = comments.stream().sorted(Comparator.comparing(Comment::getCreateTime)).collect(Collectors.toList());
        // 判断操作用户是否是评论的所有者
        String commentOwner = "";
        if (SecurityContextHolder.getContext().getAuthentication() !=null && SecurityContextHolder.getContext().getAuthentication().isAuthenticated()
                &&  !SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString().equals("anonymousUser")) {
            User principal = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (principal !=null) {
                commentOwner = principal.getUsername();
            }
        }

        model.addAttribute("commentOwner", commentOwner);
        model.addAttribute("comments", comments);
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
    public Result createComment(Long blogId, String commentContent,String toUser) {
        try {
            blogService.createComment(blogId, commentContent,toUser);
        } catch (ConstraintViolationException e)  {
            throw e;
        } catch (Exception e) {
            throw e;
        }
        return Result.success("创建评论成功");
    }

    /**
     * 删除评论
     * @return
     */
    @DeleteMapping("/{id}")
    public Result deleteBlog(@PathVariable("id") Long id, Long blogId) {

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
            blogService.removeComment(blogId, id);
            commentService.removeComment(id);
        } catch (ConstraintViolationException e)  {
            throw e;
        } catch (Exception e) {
            throw e;
        }

        return Result.success("删除评论成功");
    }
}
