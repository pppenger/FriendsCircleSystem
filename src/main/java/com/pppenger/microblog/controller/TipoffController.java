package com.pppenger.microblog.controller;


import com.pppenger.microblog.domin.Catalog;
import com.pppenger.microblog.domin.Tipoff;
import com.pppenger.microblog.domin.User;
import com.pppenger.microblog.result.CodeMsg;
import com.pppenger.microblog.result.Result;
import com.pppenger.microblog.service.TipoffService;
import com.pppenger.microblog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.ConstraintViolationException;
import javax.validation.Valid;

/**
 * 举报 控制器.
 *
 * @since 1.0.0 2017年4月10日
 * @author <a href="https://waylau.com">Way Lau</a>
 */
@Controller
@RequestMapping("/tipoff")
public class TipoffController {


    @Value("${tipoffScore}")
    private Integer tipoffScore;

    @Autowired
    private UserService userService;
    @Autowired
    private TipoffService tipoffService;
    /**
     * 提交举报
     * @return
     */
    @PostMapping
    @ResponseBody
    @PreAuthorize("isAuthenticated()")
//    public Result createCatalog(@RequestParam(value="catalogName") String formUsername,
//                                @RequestParam(value="catalogSummary") String toUsername,
//                                @RequestParam(value="catalogSummary",required=false,defaultValue = "0") Integer isSovle,
//                                @RequestParam(value="catalogSummary") Long blogId,
//                                @RequestParam(value="catalogSummary",required=false) Long commentId,
//                                @RequestParam(value="catalogSummary") String reason,
//                                @RequestParam(value="catalogSummary") String type) {
    public Result createTipoff(@Valid Tipoff tipoff) {
        User loaduser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (userService.getUserById(loaduser.getId()).getScore()<tipoffScore){
            return Result.error(CodeMsg.HAVE_NOT_SCORE);
        }
        //tipoff.setIsSolve(0);
        try {
            tipoffService.createTipoff(tipoff);
        } catch (ConstraintViolationException e)  {
            throw e;
        } catch (Exception e) {
            throw e;
        }

        return Result.success("举报成功，等待审核员审核...");
    }
}
