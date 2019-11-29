package com.pppenger.microblog.controller;

import com.pppenger.microblog.domin.Blog;
import com.pppenger.microblog.domin.User;
import com.pppenger.microblog.service.BlogService;
import com.pppenger.microblog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;


@Controller
@RequestMapping("/u")
public class UserspaceController {

        @Autowired
        private UserDetailsService userDetailsService;

        @Autowired
        private UserService userService;

        @Autowired
        private BlogService blogService;

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
     * @param user
     * @param result
     * @param redirect
     * @return
     */
    @PostMapping("/{username}/profile")
    @PreAuthorize("authentication.name.equals(#username)")
    public String saveProfile(@PathVariable("username") String username,User user) {
        User originalUser = userService.getUserById(user.getId());
        originalUser.setEmail(user.getEmail());
        originalUser.setUsername(user.getUsername());

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


//    @GetMapping("/{username}/blogs")
//    public String listBlogsByOrder(@PathVariable("username") String username,
//                                   @RequestParam(value="order",required=false,defaultValue="new") String order,
//                                   @RequestParam(value="category",required=false ) Long category,
//                                   @RequestParam(value="keyword",required=false,defaultValue="" ) String keyword,
//                                   @RequestParam(value="async",required=false) boolean async,
//                                   @RequestParam(value="pageIndex",required=false,defaultValue="0") int pageIndex,
//                                   @RequestParam(value="pageSize",required=false,defaultValue="10") int pageSize,
//                                   Model model) {
//        User  user = (User)userDetailsService.loadUserByUsername(username);
//        model.addAttribute("user", user);
//        //类别
//        if (category != null) {
//
//            System.out.print("category:" +category );
//            System.out.print("selflink:" + "redirect:/u/"+ username +"/blogs?category="+category);
//            return "/u";
//
//        }
//
//
//
//        Page<Blog> page = null;
//        if (order.equals("hot")) { // 最热查询
//            Sort sort = new Sort(Direction.DESC,"reading","comments","likes");
//            Pageable pageable = new PageRequest(pageIndex, pageSize, sort);
//            page = blogService.listBlogsByTitleLikeAndSort(user, keyword, pageable);
//        }
//        if (order.equals("new")) { // 最新查询
//            Pageable pageable = new PageRequest(pageIndex, pageSize);
//            page = blogService.listBlogsByTitleLike(user, keyword, pageable);
//        }
//
//
//        List<Blog> list = page.getContent();	// 当前所在页面数据列表
//
//        model.addAttribute("order", order);
//        model.addAttribute("page", page);
//        model.addAttribute("blogList", list);
//        return (async==true?"/userspace/u :: #mainContainerRepleace":"/userspace/u");
//    }



}
