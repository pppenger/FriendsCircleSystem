package com.pppenger.microblog.service;

import javax.transaction.Transactional;

import com.pppenger.microblog.domin.Blog;
import com.pppenger.microblog.domin.Comment;
import com.pppenger.microblog.domin.User;
import com.pppenger.microblog.domin.Vote;
import com.pppenger.microblog.repository.BlogRepository;
import com.pppenger.microblog.vo.PictureVO;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


/**
 * Blog 服务.
 * 
 * @since 1.0.0 2017年4月7日
 * @author <a href="https://waylau.com">Way Lau</a>
 */
@Service
public class BlogServiceImpl implements BlogService {

	@Autowired
	UserDetailsService userDetailsService;

	@Autowired
	private BlogRepository blogRepository;

	/* (non-Javadoc)
	 * @see com.waylau.spring.boot.blog.service.BlogService#saveBlog(com.waylau.spring.boot.blog.domain.Blog)
	 */
	@Transactional
	@Override
	public Blog saveBlog(Blog blog) {
		return blogRepository.save(blog);
	}

	/* (non-Javadoc)
	 * @see com.waylau.spring.boot.blog.service.BlogService#removeBlog(java.lang.Long)
	 */
	@Transactional
	@Override
	public void removeBlog(Long id) {
		blogRepository.delete(id);
	}

	/* (non-Javadoc)
	 * @see com.waylau.spring.boot.blog.service.BlogService#updateBlog(com.waylau.spring.boot.blog.domain.Blog)
	 */
	@Transactional
	@Override
	public Blog updateBlog(Blog blog) {
		return blogRepository.save(blog);
	}

	/* (non-Javadoc)
	 * @see com.waylau.spring.boot.blog.service.BlogService#getBlogById(java.lang.Long)
	 */
	@Override
	public Blog getBlogById(Long id) {
		return blogRepository.findOne(id);
	}

	@Override
	public Page<Blog> listBlogsByTitleLike(User user, String title, Pageable pageable) {
		// 模糊查询
		title = "%" + title + "%";
		Page<Blog> blogs = blogRepository.findByUserAndTitleLikeOrderByCreateTimeDesc(user, title, pageable);
		return blogs;
	}

	@Override
	public Page<Blog> listBlogsByTitleLikeAndSort(User user, String title, Pageable pageable) {
		// 模糊查询
		title = "%" + title + "%";
		Page<Blog> blogs = blogRepository.findByUserAndTitleLike(user, title, pageable);
		return blogs;
	}

	@Override
	public void readingIncrease(Long id) {
		Blog blog = blogRepository.findOne(id);
		blog.setReadSize(blog.getCommentSize() + 1);
		blogRepository.save(blog);
	}

	@Override
	public Blog createComment(Long blogId, String commentContent, String toUserName) {

		Blog originalBlog = blogRepository.findOne(blogId);
		User formuser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Comment comment;
		if (StringUtils.isNotEmpty(toUserName)){
			User toUser = (User) userDetailsService.loadUserByUsername(toUserName);
			commentContent="@"+toUserName+"："+toUser.getName();
			 comment = new Comment(formuser,toUser, commentContent);
			 //添加提醒信息——未完成
		}else{

			 comment = new Comment(formuser, commentContent);
		}
		originalBlog.addComment(comment);
		return blogRepository.save(originalBlog);
	}

	@Override
	public void removeComment(Long blogId, Long commentId) {
		Blog originalBlog = blogRepository.findOne(blogId);
		originalBlog.removeComment(commentId);
		blogRepository.save(originalBlog);
	}

	@Override
	public List uploadPictures(MultipartFile[] multipartFiles) {
		List list = new ArrayList();

		if (multipartFiles.length > 0) {
			for (MultipartFile multipartFile : multipartFiles) {
				//String filename = multipartFile.getOriginalFilename();
				String uuid = UUID.randomUUID().toString().replaceAll("-", "");
				String newFileName = uuid + ".png";

				String path = "D:/uploadImages/";
				String compresspath = "D:/uploadImages/compress/";
				// 生成保存路径名
				String realPath = path + "/" + newFileName;
				File dest = new File(realPath);
				//判断文件父目录是否存在
				if (!dest.getParentFile().exists()) {
					dest.getParentFile().mkdirs();
				}
				try {
					//保存文件
					multipartFile.transferTo(dest);
				} catch (IllegalStateException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				try {
					Thumbnails.of(realPath)
							.size(50, 50)
							.toFile(compresspath + newFileName);
				} catch (IOException e) {
					e.printStackTrace();
				}
				//读取相对路径
				String getpath = "/" + newFileName;
				String getcompresspath = "/compress/" + newFileName;
				PictureVO pictureVO = new PictureVO(getpath, getcompresspath);
				list.add(pictureVO);
			}
		}
		return list;
	}

	@Override
	public Blog createVote(Long blogId) {
		Blog originalBlog = blogRepository.findOne(blogId);
		User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Vote vote = new Vote(user);
		boolean isExist = originalBlog.addVote(vote);
		if (isExist) {
			throw new IllegalArgumentException("该用户已经点过赞了");
		}
		return blogRepository.save(originalBlog);
	}

	@Override
	public void removeVote(Long blogId, Long voteId) {
		Blog originalBlog = blogRepository.findOne(blogId);
		originalBlog.removeVote(voteId);
		blogRepository.save(originalBlog);
	}
}
