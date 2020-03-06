package com.pppenger.microblog.controller;

import com.pppenger.microblog.domin.Authority;
import com.pppenger.microblog.domin.User;
import com.pppenger.microblog.result.CodeMsg;
import com.pppenger.microblog.result.Result;
import com.pppenger.microblog.service.AuthorityService;
import com.pppenger.microblog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

/**
 * 主页控制器.
 * 
 * @since 1.0.0 2017年3月8日
 * @author <a href="https://waylau.com">Way Lau</a> 
 */
@Controller
public class MainController {
	
	private static final Long ROLE_USER_AUTHORITY_ID = 2L;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private AuthorityService authorityService;
	
	@GetMapping("/")
	public String root() {
		return "redirect:/index";
	}
	
	@GetMapping("/index")
	public String index() {
		return "redirect:/blogs";
	}

	@GetMapping("/403")
	public String fuckme() {
		return "404";
	}

    @GetMapping("/404")
    public String fuckyou() {
        return "redirect:/403";
    }

    @GetMapping("/error")
	public String error() {
		return "index";
	}

	/**
	 * 获取登录界面
	 * @return
	 */
	@GetMapping("/login")
	public String login() {
		return "login";
	}

	@GetMapping("/login-error")
	public String loginError(Model model) {
		model.addAttribute("loginError", true);
		model.addAttribute("errorMsg", "登陆失败，账号或者密码错误！");
		return "login";
	}
	
	@GetMapping("/register")
	public String register() {
		return "register";
	}
	
	/**
	 * 注册用户
	 * @param user
	 * @param result
	 * @param redirect
	 * @return
	 */
//	@PostMapping("/register")
//	public String registerUser(User user) {
//
//		List<Authority> authorities = new ArrayList<>();
//		authorities.add(authorityService.getAuthorityById(ROLE_USER_AUTHORITY_ID));
//		user.setAuthorities(authorities);
//		user.setEncodePassword(user.getPassword());
//		userService.saveUser(user);
//		return "redirect:/login";
//	}
	@PostMapping("/register")
	@ResponseBody
	public Result registerUser(@Valid User user) {
		if (userService.hadRegister(user.getUsername(),user.getEmail())!=null){
			return Result.error(CodeMsg.BIND_ERROR.fillArgs(userService.hadRegister(user.getUsername(),user.getEmail())));
		}

		List<Authority> authorities = new ArrayList<>();
		authorities.add(authorityService.getAuthorityById(ROLE_USER_AUTHORITY_ID));
		user.setAuthorities(authorities);
		//user.setName(user.getName());
		user.setEncodePassword(user.getPassword());
		user.setScore(100);
		userService.saveUser(user);
		return Result.success();
	}


}
