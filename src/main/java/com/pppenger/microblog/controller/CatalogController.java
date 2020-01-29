package com.pppenger.microblog.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.validation.ConstraintViolationException;

import com.pppenger.microblog.domin.Catalog;
import com.pppenger.microblog.domin.User;
import com.pppenger.microblog.domin.UserCatalog;
import com.pppenger.microblog.result.Result;
import com.pppenger.microblog.service.CatalogService;
import com.pppenger.microblog.service.UserService;
import com.pppenger.microblog.vo.CatalogVO;
import com.sun.javafx.collections.MappingChange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;


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
    @PreAuthorize("isAuthenticated()")
	public String myCatalogs(@RequestParam(value="toURL",required=false) String toURL,
                             Model model) {
        String catalogOwner = "";
        User principal = null;
        if (SecurityContextHolder.getContext().getAuthentication() !=null && SecurityContextHolder.getContext().getAuthentication().isAuthenticated()
                &&  !SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString().equals("anonymousUser")) {
            principal = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (principal !=null) {
                catalogOwner = principal.getUsername();
            }
        }

		User user = (User)userDetailsService.loadUserByUsername(catalogOwner);
		List<Catalog> catalogs = catalogService.listCatalogs(catalogOwner);

		model.addAttribute("catalogs", catalogs);

		if ("/u".equals(toURL)){
            return "/userspace/u :: #catalogmsg";
        }
        if ("/blog".equals(toURL)){
            return "/userspace/blog :: #catalogmsg";
        }
		return "/userspace/myCategory";
	}


    /**
     * 所有分区
     * @param model
     * @return
     */
    @GetMapping("/all")
    @PreAuthorize("isAuthenticated()")
    public String allCatalogs(Model model) {

        String catalogOwner = "";
        User principal = null;
        if (SecurityContextHolder.getContext().getAuthentication() !=null && SecurityContextHolder.getContext().getAuthentication().isAuthenticated()
                &&  !SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString().equals("anonymousUser")) {
            principal = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (principal !=null) {
                catalogOwner = principal.getUsername();
            }
        }

        List<Catalog> catalogs = catalogService.listCatalogs(1);
        if (catalogOwner!=""){
            List<CatalogVO> catalogVOS = new ArrayList<>();
            List<UserCatalog> userCatalogList = catalogService.findCatalogUserByUsername(catalogOwner);
            Map<Long,Object> userCatalogMap = userCatalogList.stream().collect(Collectors.toMap(UserCatalog::getCatalogId, Function.identity(),(old, n)->old));
            for (Catalog catalog:catalogs){
                if (!StringUtils.isEmpty(userCatalogMap.get(catalog.getId()))){
                    catalogVOS.add(new CatalogVO(catalog.getId(),catalog.getName(),catalog.getSummary(),catalog.getUsername(),catalog.getIsOpen(),1));
                }else {
                    catalogVOS.add(new CatalogVO(catalog.getId(),catalog.getName(),catalog.getSummary(),catalog.getUsername(),catalog.getIsOpen(),0));
                }
            }
            model.addAttribute("catalogs", catalogVOS);
        }else {
            model.addAttribute("catalogs", catalogs);
        }
        return "/userspace/allCategory";
    }

    /**
     * 分区成员
     * @param catalogId
     * @param model
     * @return
     */
    @GetMapping("/users")
    public String listCatalogUsers(Model model) {
        List<Catalog> catalogs = catalogService.listCatalogs(1);
        model.addAttribute("catalogs", catalogs);
        return "/userspace/categoryUsers";
    }

    /**
     * 关注分区
     * @param catalogId
     * @param model
     * @return
     */
    @PostMapping("/star")
    @PreAuthorize("authentication.name.equals(#username)")
    @ResponseBody
    public Result starCatalog(@RequestParam(value="catalogId",required=true) Long catalogId,
                              @RequestParam(value="username",required=true) String username,
                              Model model) {
        List<UserCatalog> list = catalogService.findByUsernameAndCatalogId(username, catalogId);
        if (list.size() != 0){
            return Result.success("您已经关注过了！");
        }

        UserCatalog userCatalog = new UserCatalog();
        userCatalog.setCatalogId(catalogId);
        userCatalog.setUsername(username);
        try {
            catalogService.saveUserCatalog(userCatalog);
        } catch (ConstraintViolationException e)  {
            throw e;
        } catch (Exception e) {
            throw e;
        }
        return Result.success("成功关注！");
    }

    /**
     * 取消关注分区
     * @param catalogId
     * @param model
     * @return
     */
    @PostMapping("/unstar")
    @PreAuthorize("authentication.name.equals(#username)")
    @ResponseBody
    public Result unstarCatalog(@RequestParam(value="catalogId",required=true) Long catalogId,
                              @RequestParam(value="username",required=true) String username,
                              Model model) {
        List<UserCatalog> list = catalogService.findByUsernameAndCatalogId(username, catalogId);
        if (list.size() == 0){
            return Result.success("您尚未关注");
        }
        try {
            catalogService.deleteByUsernameAndCatalogId(username, catalogId);
        } catch (ConstraintViolationException e)  {
            throw e;
        } catch (Exception e) {
            throw e;
        }
        return Result.success("已取消关注！");
    }

    /**
     * 分区成员
     * @param catalogId
     * @param model
     * @return
     */
    @GetMapping("/usersByCatalog")
    public String oneCatalogUsers(@RequestParam(value="catalogId",required=true) Long catalogId, Model model) {
        List<UserCatalog> userCatalogs;
        List<String> userNames;
        if (catalogId == -1){
            userNames = userService.listUsers().stream().map(User::getUsername).distinct().collect(Collectors.toList());
        }else {
            userCatalogs = catalogService.listUsernamesByCatalog(catalogId);
            userNames = userCatalogs.stream().map(u->u.getUsername()).distinct().collect(Collectors.toList());
        }
        List<User> userList = userService.loadUserByUsernames(userNames);
        model.addAttribute("userList", userList);
        return "/userspace/categoryUsers :: #catalogUsersDiv";
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
    @ResponseBody
    @PreAuthorize("isAuthenticated()")
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

    @GetMapping("/unOpenCatalogs")
    public ModelAndView list(@RequestParam(value = "pageIndex",required = false,defaultValue = "0")int pageIndex,
                             @RequestParam(value = "pageSize",required = false,defaultValue = "10")int pageSize,
                             Model model
    ){
        Page<Catalog> page=catalogService.listUnOpenCatalogs(0,pageIndex,pageSize);
        List<Catalog> list = page.getContent();	// 当前所在页面数据列表

        model.addAttribute("page", page);
        model.addAttribute("catalogList", list);
        //return page;
        return new ModelAndView("admin/catalog", "model", model);
    }

    /**
     * 审批分区通过
     * @param username
     * @param blog
     * @return
     */
    @PostMapping("/passCatalog")
    @ResponseBody
    public Result saveBlog(@RequestParam(value = "catalogId",required=true)  Long catalogId) {
        Catalog catalog = catalogService.getCatalogById(catalogId);
        if (catalog.getIsOpen()==1){
            return Result.success("原先就已经开启了哦");
        }
		try {
			catalog.setIsOpen(1);
			catalogService.saveCatalog(catalog);
		} catch (ConstraintViolationException e)  {
			throw e;
		} catch (Exception e) {
            throw e;
		}
        //String redirectUrl = "/u/" + username + "/blogs/" + blog.getId();
        return Result.success("处理成功");
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
