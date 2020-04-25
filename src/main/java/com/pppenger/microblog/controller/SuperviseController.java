package com.pppenger.microblog.controller;

import com.pppenger.microblog.domin.Catalog;
import com.pppenger.microblog.domin.Collection;
import com.pppenger.microblog.domin.Tipoff;
import com.pppenger.microblog.domin.User;
import com.pppenger.microblog.result.CodeMsg;
import com.pppenger.microblog.result.Result;
import com.pppenger.microblog.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.ConstraintViolationException;
import java.util.List;

@RestController
@RequestMapping("/supervise")
public class SuperviseController {


    @Value("${superviserScore}")
    private Integer superviserScore;

    @Autowired
    private CollectionService collectionService;
    @Autowired
    private BlogService blogService;
    @Autowired
    UserDetailsService userDetailsService;

    @Autowired
    private TipoffService tipoffService;
    @Autowired
    private UserService userService;
    @Autowired
    private CommentService commentService;

    @GetMapping
    public ModelAndView getUserAdmin(@RequestParam(value = "pageIndex",required = false,defaultValue = "0")int pageIndex,
                                     @RequestParam(value = "pageSize",required = false,defaultValue = "5")int pageSize,
                                     @RequestParam(value="async",required=false) boolean async,Model model){
        Page<Tipoff> page = tipoffService.getAllTipoff(pageIndex, pageSize);
        List<Tipoff> list = page.getContent();	// 当前所在页面数据列表

        list.forEach(tipoff -> {
            User touser  = (User) userDetailsService.loadUserByUsername(tipoff.getToUsername());
            tipoff.setToUserIsClosed(touser.getClose());
        });
        model.addAttribute("page", page);
        model.addAttribute("tipoffList", list);

        return new ModelAndView(async==true?"admin/tipoff::#AdminIndex" : "admin/tipoff","model", model);
    }


    @GetMapping("/userScore/{username}")
    @ResponseBody
    public Result getScoreByUsername(@PathVariable("username") String username){
        User user = (User)userDetailsService.loadUserByUsername(username);
        return Result.success(user.getScore());
    }


    @PostMapping("/editScore")
    @ResponseBody
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_SUPERVISE')")  // 指定角色权限才能操作方法
    public Result editColl(@RequestParam("score") Integer score,
                           @RequestParam("username") String username,
                           @RequestParam("tipoffId") Long tipoffId) {
        //获取当前登录用户
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (userService.getUserById(user.getId()).getScore()<superviserScore){
            return Result.error(CodeMsg.HAVE_NOT_SCORE);
        }
        try {
            tipoffService.updateScore(username,score);
            //tipoffService.removeTipoff(tipoffId);
        } catch (ConstraintViolationException e) {
            throw e;
        } catch (Exception e) {
            throw e;
        }

        return Result.success("已处理");
    }



    @PostMapping("/ignore")
    @ResponseBody
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_SUPERVISE')")  // 指定角色权限才能操作方法
    public Result ignore(@RequestParam("tipoffId") Long tipoffId) {
        //获取当前登录用户
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (userService.getUserById(user.getId()).getScore()<superviserScore){
            return Result.error(CodeMsg.HAVE_NOT_SCORE);
        }
        try {
            tipoffService.removeTipoff(tipoffId);
        } catch (ConstraintViolationException e) {
            throw e;
        } catch (Exception e) {
            throw e;
        }

        return Result.success("已忽略");
    }


    @PostMapping("/close")
    @ResponseBody
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_SUPERVISE')")  // 指定角色权限才能操作方法
    public Result closeUser(@RequestParam("username") String username) {
        //获取当前登录用户
        User user1 = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (userService.getUserById(user1.getId()).getScore()<superviserScore){
            return Result.error(CodeMsg.HAVE_NOT_SCORE);
        }
        try {
            User user = (User) userDetailsService.loadUserByUsername(username);
            user.setClose(1);
            userService.updateUser(user);
        } catch (Exception e) {
            throw e;
        }

        return Result.success("已封号");
    }

    @PostMapping("/unclose")
    @ResponseBody
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_SUPERVISE')")  // 指定角色权限才能操作方法
    public Result unCloseUser(@RequestParam("username") String username) {
        //获取当前登录用户
        User user1 = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (userService.getUserById(user1.getId()).getScore()<superviserScore){
            return Result.error(CodeMsg.HAVE_NOT_SCORE);
        }
        try {
            User user = (User) userDetailsService.loadUserByUsername(username);
            user.setClose(0);
            userService.updateUser(user);
        } catch (Exception e) {
            throw e;
        }

        return Result.success("已解封");
    }


    /**
     * 获取被封号的用户
     * @return
     */
    @GetMapping("/closeUserlist")
    @ResponseBody
    public Result getCloseUser() {
        List users;
        try {
            users = userService.getUserByClose(1);
        } catch (Exception e) {
            return  Result.error(CodeMsg.SERVER_ERROR2.fillArgs(e.getMessage()));
        }
        return  Result.success(users);
    }


    @PostMapping("/handlerDel")
    @ResponseBody
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_SUPERVISE')")  // 指定角色权限才能操作方法
    public Result handlerDel(
                           @RequestParam("id") long id,
                           @RequestParam("type") String type,
                           @RequestParam("tipoffId") Long tipoffId) throws Exception{
        //获取当前登录用户
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (userService.getUserById(user.getId()).getScore()<superviserScore){
            return Result.error(CodeMsg.HAVE_NOT_SCORE);
        }
        if (type.equals("blog")){

            collectionService.delByBlogId(id);
            blogService.removeBlog(id);
        }else if (type.equals("comment")){
            commentService.updateComment(id);
        }else {
            return Result.success("类型错误");
        }
        tipoffService.removeTipoff(tipoffId);

        return Result.success("已强删");
    }
}
