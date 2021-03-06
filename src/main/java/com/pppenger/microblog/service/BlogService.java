package com.pppenger.microblog.service;

import com.pppenger.microblog.domin.Blog;
import com.pppenger.microblog.domin.User;
import com.pppenger.microblog.vo.BlogVO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


/**
 * Blog 服务接口.
 * 
 * @since 1.0.0 2017年4月7日
 * @author <a href="https://waylau.com">Way Lau</a>
 */
public interface BlogService {
	/**
	 * 保存Blog
	 * @param Blog
	 * @return
	 */
	Blog saveBlog(Blog blog);
	
	/**
	 * 删除Blog
	 * @param id
	 * @return
	 */
	void removeBlog(Long id);
	
	/**
	 * 更新Blog
	 * @param Blog
	 * @return
	 */
	Blog updateBlog(Blog blog);
	
	/**
	 * 根据id获取Blog
	 * @param id
	 * @return
	 */
	Blog getBlogById(Long id);
	
	/**
	 * 根据用户名进行分页模糊查询（最新）
	 * @param user
	 * @return
	 */
	Page<Blog> listBlogsByUserAndCatalogAndTitleLike(User user,Long catalogId, String title, Pageable pageable);
 
	/**
	 * 根据用户名进行分页模糊查询（最热）
	 * @param user
	 * @return
	 */
	Page<Blog> listBlogsByUserAndCatalogAndTitleLikeAndSort(User suser,Long catalogId, String title, Pageable pageable);

	Page<Blog> listBlogsByCatalogLike(Long catalogId, Pageable pageable);

	Page<Blog> listBlogsByCatalogLikeAndSort(Long catalogId, Pageable pageable);
	
	/**
	 * 阅读量递增
	 * @param id
	 */
	void readingIncrease(Long id);
	
	/**
	 * 发表评论
	 * @param blogId
	 * @param commentContent
	 * @return
	 */
	Blog createComment(Long blogId, String commentContent, String toUser);
	
	/**
	 * 删除评论
	 * @param blogId
	 * @param commentId
	 * @return
	 */
	void removeComment(Long blogId, Long commentId);


	/**
	 * 上传多张图片并返回真实链接和压缩链接
	 * @param multipartFiles
	 * @return
	 */
	List uploadPictures(MultipartFile[] multipartFiles);

	/**
	 * 点赞
	 * @param blogId
	 * @return
	 */
	Long createVote(Long blogId);

	/**
	 * 取消点赞
	 * @param blogId
	 * @param voteId
	 * @return
	 */
	void removeVote(Long blogId, Long voteId);

	List<Blog> getBlogTop2HotComment(List<Blog> list);

	List<Blog> setBlogVoteAndCommentVoteListToUser(User principal,List<Blog> list);

	List<BlogVO> setBlogCollectionIdByUser(User principal, List<BlogVO> blogVOS);
	}
