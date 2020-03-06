package com.pppenger.microblog.controller;

import javax.validation.ConstraintViolationException;

import com.pppenger.microblog.domin.Top;
import com.pppenger.microblog.domin.User;
import com.pppenger.microblog.repository.UserRepository;
import com.pppenger.microblog.result.Result;
import com.pppenger.microblog.service.*;
import com.pppenger.microblog.vo.UserVoteSizeRankVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;


/**
 * 点赞控制器.
 *
 * @author <a href="https://waylau.com">Way Lau</a>
 * @since 1.0.0 2017年3月8日
 */
@Controller
@RequestMapping("/votes")
public class VoteController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;
    @Autowired
    UserDetailsService userDetailsService;
    @Autowired
    private BlogService blogService;

    @Autowired
    private VoteService voteService;

    @Autowired
    private CommentService commentService;
    @Autowired
    private TopService topService;

    /**
     * 发表点赞
     *
     * @param blogId
     * @param VoteContent
     * @return
     */
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    @ResponseBody
    public Result createVote(Long blogId, String thisBlogUsername) {
        Long voteId;
        try {
            voteId = blogService.createVote(blogId);

            User blogUser = (User) userDetailsService.loadUserByUsername(thisBlogUsername);
            blogUser.setVoteSize(blogUser.getVoteSize() + 1);
            userService.saveUser(blogUser);
        } catch (ConstraintViolationException e) {
            throw e;
        } catch (Exception e) {
            throw e;
        }

        return Result.success(voteId);
    }

    /**
     * 删除点赞
     *
     * @return
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @ResponseBody
    public Result delete(@PathVariable("id") Long id, Long blogId, String thisBlogUsername) {

        boolean isOwner = false;
        User user = voteService.getVoteById(id).getUser();

        // 判断操作用户是否是点赞的所有者
        if (SecurityContextHolder.getContext().getAuthentication() != null && SecurityContextHolder.getContext().getAuthentication().isAuthenticated()
                && !SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString().equals("anonymousUser")) {
            User principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (principal != null && user.getUsername().equals(principal.getUsername())) {
                isOwner = true;
            }
        }

        if (!isOwner) {
            return Result.success("没有操作权限");
        }

        try {
            blogService.removeVote(blogId, id);
            voteService.removeVote(id);

            User blogUser = (User) userDetailsService.loadUserByUsername(thisBlogUsername);
            if (blogUser.getVoteSize() > 0) {
                blogUser.setVoteSize(blogUser.getVoteSize() - 1);
                userService.saveUser(blogUser);
            }
        } catch (ConstraintViolationException e) {
            throw e;
        } catch (Exception e) {
            throw e;
        }

        return Result.success("取消点赞成功");
    }


    /**
     * 发表评论点赞
     *
     * @param commentId
     * @return
     */
    @PostMapping("/comment")
    @PreAuthorize("isAuthenticated()")
    @ResponseBody
    public Result createCommentVote(Long commentId, String commentFormUserName) {
        Long voteId;
        try {
            voteId = commentService.createVote(commentId);

            User blogUser = (User) userDetailsService.loadUserByUsername(commentFormUserName);
            blogUser.setVoteSize(blogUser.getVoteSize() + 1);
            userService.saveUser(blogUser);
        } catch (ConstraintViolationException e) {
            throw e;
        } catch (Exception e) {
            throw e;
        }

        return Result.success(voteId);
    }

    /**
     * 删除评论点赞
     *
     * @return
     */
    @DeleteMapping("/comment/{id}")
    @PreAuthorize("isAuthenticated()")
    @ResponseBody
    public Result commentDelete(@PathVariable("id") Long id, Long commentId, String commentFormUserName) {

        boolean isOwner = false;
        User user = voteService.getVoteById(id).getUser();

        // 判断操作用户是否是点赞的所有者
        if (SecurityContextHolder.getContext().getAuthentication() != null && SecurityContextHolder.getContext().getAuthentication().isAuthenticated()
                && !SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString().equals("anonymousUser")) {
            User principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (principal != null && user.getUsername().equals(principal.getUsername())) {
                isOwner = true;
            }
        }

        if (!isOwner) {
            return Result.success("没有操作权限");
        }

        try {
            commentService.removeVote(commentId, id);
            voteService.removeVote(id);

            User blogUser = (User) userDetailsService.loadUserByUsername(commentFormUserName);
            if (blogUser.getVoteSize() > 0) {
                blogUser.setVoteSize(blogUser.getVoteSize() - 1);
                userService.saveUser(blogUser);
            }
        } catch (ConstraintViolationException e) {
            throw e;
        } catch (Exception e) {
            throw e;
        }

        return Result.success("取消点赞成功");
    }



    @PostMapping("/getRank")
    @PreAuthorize("isAuthenticated()")
    @ResponseBody
    public Result createVote(String loginUsername) throws ParseException {
//        User user = (User) userDetailsService.loadUserByUsername(loginUsername);
//        user.getVoteSize();
        UserVoteSizeRankVO userVoteSizeRankVO = voteService.selectRankByVoteSize(loginUsername);

        Top top=topService.findOne((long) 1);
        if (loginUsername.equals(top.getTopUsername())&&top.getHaveSend()==0){
            userVoteSizeRankVO.setHadSend("not");
        }else {
            userVoteSizeRankVO.setHadSend("yes");
        }
        Long userCount = userRepository.count();
        double p =userVoteSizeRankVO.getRank()/(double)userCount;
        //如果属于前百分之十，就有秒杀资格
        if (p<=0.10){
            userVoteSizeRankVO.setHaveQua("yes");

            long now = System.currentTimeMillis();
            SimpleDateFormat sdfOne = new SimpleDateFormat("yyyy-MM-dd");
            long overTime = (now - (sdfOne.parse(sdfOne.format(now)).getTime()))/1000;
            //当前毫秒数
//            System.out.println(now);
            //当前时间  距离当天凌晨  秒数 也就是今天过了多少秒
//            System.out.println(overTime);
            //当前时间  距离当天晚上22:59:59  秒数 也就是今天还剩多少秒
            long TimeNext = 23*60*60 - overTime;
            //如果到了秒杀时间而且还没被秒杀，那就允许显示秒杀按钮
            Top betop=topService.findOne((long) 1);
            if ((TimeNext<=0)&&("".equals(betop.getTopUsername())||betop.getTopUsername()==null)){
                userVoteSizeRankVO.setCanGrab("yes");
            }else {
                userVoteSizeRankVO.setCanGrab("not");
            }
            //System.out.println(TimeNext);
        }else {
            userVoteSizeRankVO.setHaveQua("not");
            userVoteSizeRankVO.setCanGrab("not");
        }
        DecimalFormat decimalFormat=new DecimalFormat("0.00%");
        userVoteSizeRankVO.setPercentage(decimalFormat.format(p));
        return Result.success(userVoteSizeRankVO);
    }
}
