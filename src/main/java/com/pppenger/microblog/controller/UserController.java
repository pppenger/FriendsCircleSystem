package com.pppenger.microblog.controller;

import com.pppenger.microblog.domin.Authority;
import com.pppenger.microblog.domin.User;
import com.pppenger.microblog.exception.ConstraintViolationExceptionHandler;
import com.pppenger.microblog.result.CodeMsg;
import com.pppenger.microblog.result.Result;
import com.pppenger.microblog.service.AuthorityService;
import com.pppenger.microblog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/users")

public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthorityService authorityService;


    @GetMapping
    public ModelAndView getUserAdmin(){
        return new ModelAndView("admin/index");
    }

    @GetMapping("/userlist")
    public ModelAndView list(@RequestParam(value = "pageIndex",required = false,defaultValue = "0")int pageIndex,
                             @RequestParam(value = "pageSize",required = false,defaultValue = "10")int pageSize,
                             @RequestParam(value = "name",required = false,defaultValue = "")String searchName,
                             Model model
                           ){
        Page<User> page=userService.listUsersByNameLike(searchName,pageIndex,pageSize);
        List<User> list = page.getContent();	// 当前所在页面数据列表

        model.addAttribute("searchUsername", searchName);
        model.addAttribute("page", page);
        model.addAttribute("userList", list);
        //return page;
        return new ModelAndView("admin/list", "userModel", model);
    }

    //////用来测试page的
//    @GetMapping("/userpage")
//    public Page test(@RequestParam(value = "pageIndex",required = false,defaultValue = "0")int pageIndex,
//                             @RequestParam(value = "pageSize",required = false,defaultValue = "10")int pageSize,
//                             @RequestParam(value = "name",required = false,defaultValue = "")String name,
//                             Model model
//    ){
//        Page<User> page=userService.listUsersByNameLike(name,0,pageSize);
//        List<User> list = page.getContent();	// 当前所在页面数据列表
//
//        //return page;
//        return page;
//    }


    /**
     * 获取修改用户的数据
     * @param user
     * @return
     */
    @GetMapping(value = "edit/{id}")
    public Result<User> modifyForm(@PathVariable("id") Long id) {
        User user = userService.getUserById(id);
        return Result.success(user);
    }

//    @GetMapping(value = "edit/{id}")
//    public User modifyForm(@PathVariable("id") Long id) {
//        User user = userService.getUserById(id);
//        return user;
//    }
//    @GetMapping(value = "edit/{id}")
//    public String modifyForm(@PathVariable("id") Long id) {
//        User user = userService.getUserById(id);
//        return "你好";
//    }

//    @GetMapping(value = "edit/{id}")
//    public Authority modifyForm(@PathVariable("id") Long id) {
//        Authority user = new Authority();
//        user.setName("2");
//        return user;
//    }


    /**
     * 管理员新建/修改用户
     * @param user
     * @param result
     * @param redirect
     * @return
     */
    @PostMapping
    public Result create( @Valid User user, Long authorityId) {
        List<Authority> authorities = new ArrayList<>();
        authorities.add(authorityService.getAuthorityById(authorityId));
        user.setAuthorities(authorities);
        //不存在，新建用户
        if(user.getId() == null) {
            if (userService.hadRegister(user.getUsername(),user.getEmail())!=null){
                return Result.error(CodeMsg.BIND_ERROR.fillArgs(userService.hadRegister(user.getUsername(),user.getEmail())));
            }

            user.setEncodePassword(user.getPassword()); // 加密密码
        }
        //存在，更改用户
        else {
            // 判断密码是否做了变更
            User originalUser = userService.getUserById(user.getId());
            if (userService.hadRegister(user.getUsername(),user.getEmail(),originalUser)!=null){
                return Result.error(CodeMsg.BIND_ERROR.fillArgs(userService.hadRegister(user.getUsername(),user.getEmail(),originalUser)));
            }
            //原来数据库里的密码
            String rawPassword = originalUser.getPassword();
            PasswordEncoder encoder = new BCryptPasswordEncoder();
            //输入之后且加密过的密码
            String encodePasswd = encoder.encode(user.getPassword());
            boolean isMatch = encoder.matches(rawPassword, encodePasswd);
            if (!isMatch) {
                user.setEncodePassword(user.getPassword());
            }else {
                user.setPassword(user.getPassword());
            }
        };

//        try {
//            userService.saveUser(user);
//        }catch (TransactionSystemException e) {
//            Throwable t = e.getCause();
//            while ((t != null) && !(t instanceof ConstraintViolationException)) {
//                t = t.getCause();
//            }
//            if (t instanceof ConstraintViolationException) {
//                return Result.success(ConstraintViolationExceptionHandler.getMessage((ConstraintViolationException) t));
//            }
//        }
        userService.saveUser(user);

        return Result.success();
    }


    /**
     * 删除用户
     * @param id
     * @return
     */
    @DeleteMapping(value = "/{id}")
    public Result delete(@PathVariable("id") Long id) {
        try {
            userService.removeUser(id);
        } catch (Exception e) {
            return  Result.error(CodeMsg.SERVER_ERROR2.fillArgs(e.getMessage()));
        }
        return  Result.success();
    }


}
