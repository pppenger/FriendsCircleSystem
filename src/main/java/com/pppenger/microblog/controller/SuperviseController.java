package com.pppenger.microblog.controller;

import com.pppenger.microblog.domin.Catalog;
import com.pppenger.microblog.domin.Collection;
import com.pppenger.microblog.domin.Tipoff;
import com.pppenger.microblog.domin.User;
import com.pppenger.microblog.result.CodeMsg;
import com.pppenger.microblog.result.Result;
import com.pppenger.microblog.service.TipoffService;
import com.pppenger.microblog.service.UserService;
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
    UserDetailsService userDetailsService;

    @Autowired
    private TipoffService tipoffService;
    @Autowired
    private UserService userService;

    @GetMapping
    public ModelAndView getUserAdmin(@RequestParam(value = "pageIndex",required = false,defaultValue = "0")int pageIndex,
                                     @RequestParam(value = "pageSize",required = false,defaultValue = "5")int pageSize,
                                     @RequestParam(value="async",required=false) boolean async,Model model){
        Page<Tipoff> page = tipoffService.getAllTipoff(pageIndex, pageSize);
        List<Tipoff> list = page.getContent();	// 当前所在页面数据列表

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
            tipoffService.removeTipoff(tipoffId);
        } catch (ConstraintViolationException e) {
            throw e;
        } catch (Exception e) {
            throw e;
        }

        return Result.success("已处理");
    }

}
