package com.pppenger.microblog.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.ConstraintViolationException;

import com.pppenger.microblog.domin.Catalog;
import com.pppenger.microblog.domin.User;
import com.pppenger.microblog.domin.UserCatalog;
import com.pppenger.microblog.result.Result;
import com.pppenger.microblog.service.CatalogService;
import com.pppenger.microblog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


/**
 * 分类 控制器.
 * 
 * @since 1.0.0 2017年4月10日
 * @author <a href="https://waylau.com">Way Lau</a> 
 */
@Controller
@RequestMapping("/catalogs")
public class CatalogController {
	
	@Autowired
	private CatalogService catalogService;

	@Autowired
	private UserDetailsService userDetailsService;

    @Autowired
    private UserService userService;
//	/**
//	 * 获取分类列表
//	 * @param blogId
//	 * @param model
//	 * @return
//	 */
//	@GetMapping
//	public String listComments(@RequestParam(value="username",required=true) String username, Model model) {
//		User user = (User)userDetailsService.loadUserByUsername(username);
//		List<Catalog> catalogs = catalogService.listCatalogs(user);
//
//		// 判断操作用户是否是分类的所有者
//		boolean isOwner = false;
//
//		if (SecurityContextHolder.getContext().getAuthentication() !=null && SecurityContextHolder.getContext().getAuthentication().isAuthenticated()
//				 &&  !SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString().equals("anonymousUser")) {
//			User principal = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//			if (principal !=null && user.getUsername().equals(principal.getUsername())) {
//				isOwner = true;
//			}
//		}
//
//		model.addAttribute("isCatalogsOwner", isOwner);
//		model.addAttribute("catalogs", catalogs);
//		return "/userspace/u :: #catalogRepleace";
//	}
    /**
	 * 我关注的分区
	 * @param username
	 * @param model
	 * @return
	 */
	@GetMapping("/my")
    @PreAuthorize("authentication.name.equals(#username)")
	public String myCatalogs(@PathVariable("username") String username, Model model) {
		User user = (User)userDetailsService.loadUserByUsername(username);
		List<Catalog> catalogs = catalogService.listCatalogs(username);

		model.addAttribute("catalogs", catalogs);
		return "/userspace/myCategory";
	}


    /**
     * 所有分区
     * @param model
     * @return
     */
    @GetMapping("/all")
    public String allCatalogs(Model model) {
        List<Catalog> catalogs = catalogService.listCatalogs();

        model.addAttribute("catalogs", catalogs);
        return "/userspace/allCategory";
    }

    /**
     * 分区成员
     * @param catalogId
     * @param model
     * @return
     */
    @GetMapping("/users")
    public String listCatalogUsers(@RequestParam(value="catalogId",required=true) String catalogId, Model model) {
        List list = new ArrayList();
        list.add(catalogId);
        List<UserCatalog> catalogs = catalogService.listUsernamesByCatalog(catalogId);
        List<String> userNames = catalogs.stream().map(UserCatalog::getUsername).collect(Collectors.toList());
        List<User> userList = userService.loadUserByUsernames(userNames);
        model.addAttribute("userList", userList);
        return "/userspace/category";
    }

    /**
     * 提议新建分类界面
     * @return
     */
    @GetMapping("/add")
    public String addCatalog() {
        return "/userspace/addCategory";
    }

    /**
	 * 提议新建分类
	 * @param catalogName
	 * @param catalogSummary
	 * @return
	 */
	@PostMapping("/add")
	public Result createCatalog(@RequestParam(value="catalogName",required=true) String catalogName,
                         @RequestParam(value="catalogSummary",required=true) String catalogSummary) {

        String catalogOwner = "";
        User principal = null;
        if (SecurityContextHolder.getContext().getAuthentication() !=null && SecurityContextHolder.getContext().getAuthentication().isAuthenticated()
                &&  !SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString().equals("anonymousUser")) {
            principal = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (principal !=null) {
                catalogOwner = principal.getUsername();
            }
        }

        Catalog catalog = new Catalog();
        catalog.setName(catalogName);
        catalog.setSummary(catalogSummary);
        catalog.setUsername(catalogOwner);
        catalog.setIsOpen(0);

		try {
			catalogService.saveCatalog(catalog);
		} catch (ConstraintViolationException e)  {
            throw e;
		} catch (Exception e) {
			throw e;
		}

		return Result.success("提议成功");
	}



//	/**
//	 * 发表分类
//	 * @param blogId
//	 * @param commentContent
//	 * @return
//	 */
//	@PostMapping
//	@PreAuthorize("authentication.name.equals(#catalogVO.username)")// 指定用户才能操作方法
//	public ResponseEntity<Response> create(@RequestBody CatalogVO catalogVO) {
//
//		String username = catalogVO.getUsername();
//		Catalog catalog = catalogVO.getCatalog();
//
//		User user = (User)userDetailsService.loadUserByUsername(username);
//
//		try {
//			catalog.setUser(user);
//			catalogService.saveCatalog(catalog);
//		} catch (ConstraintViolationException e)  {
//			return ResponseEntity.ok().body(new Response(false, ConstraintViolationExceptionHandler.getMessage(e)));
//		} catch (Exception e) {
//			return ResponseEntity.ok().body(new Response(false, e.getMessage()));
//		}
//
//		return ResponseEntity.ok().body(new Response(true, "处理成功", null));
//	}
//
//	/**
//	 * 删除分类
//	 * @return
//	 */
//	@DeleteMapping("/{id}")
//	@PreAuthorize("authentication.name.equals(#username)")  // 指定用户才能操作方法
//	public ResponseEntity<Response> delete(String username, @PathVariable("id") Long id) {
//		try {
//			catalogService.removeCatalog(id);
//		} catch (ConstraintViolationException e)  {
//			return ResponseEntity.ok().body(new Response(false, ConstraintViolationExceptionHandler.getMessage(e)));
//		} catch (Exception e) {
//			return ResponseEntity.ok().body(new Response(false, e.getMessage()));
//		}
//
//		return ResponseEntity.ok().body(new Response(true, "处理成功", null));
//	}
//
//	/**
//	 * 获取分类编辑界面
//	 * @param id
//	 * @param model
//	 * @return
//	 */
//	@GetMapping("/edit")
//	public String getCatalogEdit(Model model) {
//		Catalog catalog = new Catalog(null, null);
//		model.addAttribute("catalog",catalog);
//		return "/userspace/catalogedit";
//	}
//	/**
//	 * 根据 Id 获取分类信息
//	 * @param id
//	 * @param model
//	 * @return
//	 */
//	@GetMapping("/edit/{id}")
//	public String getCatalogById(@PathVariable("id") Long id, Model model) {
//		Catalog catalog = catalogService.getCatalogById(id);
//		model.addAttribute("catalog",catalog);
//		return "/userspace/catalogedit";
//	}
	
}
