package com.pppenger.microblog.controller;

import com.pppenger.microblog.domin.*;
import com.pppenger.microblog.result.CodeMsg;
import com.pppenger.microblog.result.Result;
import com.pppenger.microblog.service.CatalogService;
import com.pppenger.microblog.service.CollectionService;
import com.pppenger.microblog.vo.BlogVO;
import com.pppenger.microblog.vo.CollectionBlogVO;
import com.pppenger.microblog.vo.CollectionVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.ConstraintViolationException;
import javax.websocket.server.PathParam;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/collection")
public class CollectionController {

    @Autowired
    private CollectionService collectionService;
    @Autowired
    private UserDetailsService userDetailsService;

    @GetMapping("/{username}")
    @PreAuthorize("authentication.name.equals(#username)")
    public String collectionList(@PathVariable("username") String username, Model model) {
        User user = (User) userDetailsService.loadUserByUsername(username);
        List<Collection> collectionList = collectionService.findByUser(user);

        model.addAttribute("collectionList", collectionList);
        return "/userspace/collection";
    }

    @GetMapping("/{username}/select")
    @PreAuthorize("authentication.name.equals(#username)")
    @ResponseBody
    public Result collectionSelectList(@PathVariable("username") String username, Model model) {
        User user = (User) userDetailsService.loadUserByUsername(username);
        List<Collection> collectionList = collectionService.findByUser(user);
        List<CollectionVO> collectionVOS = collectionList.stream().map(c -> new CollectionVO(c.getId(), c.getTitle())).collect(Collectors.toList());
        return Result.success(collectionVOS);
    }

    @GetMapping("/getblog")
    @PreAuthorize("authentication.name.equals(#username)")
    public String getblog(@RequestParam("username") String username,
                          @RequestParam("id") Long id, Model model) {

        Collection collection = collectionService.findById(id);

        User user = (User) userDetailsService.loadUserByUsername(username);

        //对评论进行筛选
        collection.getBlogs().stream().forEach(blog ->
        {
            blog.setComments(
                    blog.getComments().stream().sorted(
                            Comparator.comparing(
                                    Comment::getVoteSize).reversed()).limit(2).collect(Collectors.toList())
            );
        });

        for (Blog blog : collection.getBlogs()){
            List list1 = new ArrayList();
            for (Vote vote : blog.getVotes()){
                if (vote.getUser().getUsername().equals(username)){
                    list1.add(vote);
                    break;
                }
            }
            blog.setVotes(list1);
            for (Comment comment : blog.getComments()){
                List list2 = new ArrayList();
                for (Vote vote : comment.getVotes()){
                    if (vote.getUser().getUsername().equals(username)){
                        list2.add(vote);
                        break;
                    }
                }
                comment.setVotes(list2);
            }
        }


        List<BlogVO> blogVOS = collection.getBlogs().stream().map(B -> new BlogVO(B.getId(), B.getTitle(), B.getSummary(), B.getUser(), B.getCreateTime(), B.getReadSize(), B.getCommentSize(), B.getVoteSize(), B.getReportSize(), B.getComments(), B.getVotes(), B.getPictures(), B.getCatalog(), null)).collect(Collectors.toList());
        List<Collection> collectionList = collectionService.findByUser(user);
        List<CollectionBlogVO> collectionBlogVOS = collectionService.selectCBbyCollIds(collectionList.stream().map(Collection::getId).collect(Collectors.toList()));
        Map<Long, Long> BlogIdCollIdMap = collectionBlogVOS.stream().collect(Collectors.toMap(CollectionBlogVO::getBlogId, CollectionBlogVO::getCollectionId, (o, n) -> o));
        if (!StringUtils.isEmpty(BlogIdCollIdMap)){
            blogVOS.stream().forEach(CBV->CBV.setCollectionId(BlogIdCollIdMap.get(CBV.getId())));
        }
        blogVOS = blogVOS.stream().filter(blogVO -> blogVO.getUser().getClose()==0).collect(Collectors.toList());
        for (BlogVO blog : blogVOS){
            blog.getComments().forEach(comment ->{
                if (comment.getFormUser().getClose()==1){
                    comment.setContent("该用户已被封号，相关内容被隐藏！");
                }
            });
        }

        model.addAttribute("blogs", blogVOS);
        return "/userspace/collection :: #blogDIV";
    }


    /**
     * 删除收藏夹
     *
     * @param id
     * @param model
     * @return
     */
    @PostMapping("/del/{username}")
    @PreAuthorize("authentication.name.equals(#username)")
    @ResponseBody
    public Result deleteBlog(@PathVariable("username") String username,
                             @RequestParam(value = "collectionId", required = true, defaultValue = "-1") Long collectionId) {

        try {
            collectionService.removeCollection(collectionId);
        } catch (Exception e) {
            return Result.error(CodeMsg.SERVER_ERROR);
        }

        String redirectUrl = "/collection/" + username;
        return Result.success(redirectUrl);
    }


    /**
     * 新建收藏夹
     *
     * @param catalogName
     * @param catalogSummary
     * @return
     */
    @PostMapping("/add")
    @ResponseBody
    @PreAuthorize("authentication.name.equals(#username)")
    public Result addColl(@RequestParam("title") String title,
                          @RequestParam("username") String username) {


        User user = (User) userDetailsService.loadUserByUsername(username);
        List<Collection> collectionList = collectionService.findByUserAndTitle(user, title);
        if (collectionList.size() > 0) {
            return Result.error(CodeMsg.COLLECTION_REPEAT);
        }
        Collection collection = new Collection();
        collection.setTitle(title);
        collection.setUser(user);

        try {
            collectionService.saveCollection(collection);
        } catch (ConstraintViolationException e) {
            throw e;
        } catch (Exception e) {
            throw e;
        }

        String redirectUrl = "/collection/" + username;
        return Result.success(redirectUrl);
    }


    /**
     * 收藏微博
     * @param blogId
     * @param VoteContent
     * @return
     */
    @PostMapping("collBlog")
    @ResponseBody
    @PreAuthorize("authentication.name.equals(#username)")
    public Result collBlog( @RequestParam("inCollId")Long inCollId, @RequestParam("blogId")Long blogId, @RequestParam("username") String username) {
        Long collId;
        try {
            collId = collectionService.collAddBlog(inCollId,blogId);
        } catch (ConstraintViolationException e)  {
            throw e;
        } catch (Exception e) {
            return Result.error(CodeMsg.HAD_COLLECTION);
        }

        return Result.success(collId);
    }

    /**
     * 删除收藏
     * @return
     */
    @PostMapping("/uncollBlog")
    @PreAuthorize("authentication.name.equals(#username)")
    @ResponseBody
    public Result uncollBlog(@RequestParam("inCollId")Long inCollId, @RequestParam("blogId")Long blogId, @RequestParam("username") String username) {
        try {
            collectionService.removeBlog(inCollId, blogId );
        } catch (ConstraintViolationException e)  {
            throw e;
        } catch (Exception e) {
            throw e;
        }

        return Result.success( "取消收藏成功");
    }


    /**
     * 编辑收藏夹名
     *
     * @param catalogName
     * @param catalogSummary
     * @return
     */
    @PostMapping("/edit")
    @ResponseBody
    @PreAuthorize("authentication.name.equals(#username)")
    public Result editColl(@RequestParam("id") Long id,
                           @RequestParam("title") String title,
                           @RequestParam("username") String username) {


        User user = (User) userDetailsService.loadUserByUsername(username);
        List<Collection> collectionList = collectionService.findByUserAndTitleAndIdIsNot(user, title, id);
        if (collectionList.size() > 0) {
            return Result.error(CodeMsg.COLLECTION_REPEAT);
        }
        Collection collection = collectionService.findById(id);
        collection.setTitle(title);

        try {
            collectionService.saveCollection(collection);
        } catch (ConstraintViolationException e) {
            throw e;
        } catch (Exception e) {
            throw e;
        }

        String redirectUrl = "/collection/" + username;
        return Result.success(redirectUrl);
    }
}
