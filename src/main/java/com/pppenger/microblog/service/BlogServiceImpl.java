package com.pppenger.microblog.service;

import javax.transaction.Transactional;

import com.pppenger.microblog.domin.*;
import com.pppenger.microblog.domin.Collection;
import com.pppenger.microblog.domin.es.EsBlog;
import com.pppenger.microblog.repository.BlogRepository;
import com.pppenger.microblog.repository.CatalogRepository;
import com.pppenger.microblog.vo.BlogVO;
import com.pppenger.microblog.vo.CollectionBlogVO;
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
import java.util.*;
import java.util.stream.Collectors;


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

	@Autowired
	private CatalogRepository catalogRepository;

	@Autowired
	private EsBlogService esBlogService;


	@Autowired
	private CollectionService collectionService;

	/* (non-Javadoc)
	 * @see com.waylau.spring.boot.blog.service.BlogService#saveBlog(com.waylau.spring.boot.blog.domain.Blog)
	 */
	@Transactional
	@Override
	public Blog saveBlog(Blog blog) {

		boolean isNew = (blog.getId() == null);
		EsBlog esBlog = null;
		Blog returnBlog = blogRepository.save(blog);

		if (isNew) {
			esBlog = new EsBlog(returnBlog);
		} else {
			esBlog = esBlogService.getEsBlogByBlogId(blog.getId());
			esBlog.update(returnBlog);
		}

		esBlogService.updateEsBlog(esBlog);
		return returnBlog;
	}

	/* (non-Javadoc)
	 * @see com.waylau.spring.boot.blog.service.BlogService#removeBlog(java.lang.Long)
	 */
	@Transactional
	@Override
	public void removeBlog(Long id) {
		blogRepository.delete(id);
		EsBlog esblog = esBlogService.getEsBlogByBlogId(id);
		esBlogService.removeEsBlog(esblog.getId());
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
	public Page<Blog> listBlogsByUserAndCatalogAndTitleLike(User user,Long catalogId, String title, Pageable pageable) {
		// 模糊查询
		title = "%" + title + "%";
		Catalog catalog =null;
		if (!org.springframework.util.StringUtils.isEmpty(catalogId)){
			catalog = catalogRepository.findOne(catalogId);
		}

		Page<Blog> blogs;
		if (catalog!=null){
			blogs= blogRepository.findByUserAndCatalogAndTitleLikeOrderByCreateTimeDesc(user,catalog, title, pageable);
		}
		else {
			blogs= blogRepository.findByUserAndTitleLikeOrderByCreateTimeDesc(user, title, pageable);
		}
		return blogs;
	}

	@Override
	public Page<Blog> listBlogsByUserAndCatalogAndTitleLikeAndSort(User user,Long catalogId, String title, Pageable pageable) {

		Catalog catalog =null;
		if (!org.springframework.util.StringUtils.isEmpty(catalogId)){
			catalog = catalogRepository.findOne(catalogId);
		}
		// 模糊查询
		title = "%" + title + "%";
		Page<Blog> blogs;
		if (catalog!=null){
			blogs= blogRepository.findByUserAndCatalogAndTitleLike(user,catalog, title, pageable);
		}
		else {
			blogs= blogRepository.findByUserAndTitleLike(user, title, pageable);
		}
		return blogs;
	}


	@Override
	public Page<Blog> listBlogsByCatalogLike(Long catalogId, Pageable pageable) {
		Catalog catalog =null;
		if (!org.springframework.util.StringUtils.isEmpty(catalogId)){
			catalog = catalogRepository.findOne(catalogId);
		}

		Page<Blog> blogs;
		if (catalog!=null){
			blogs= blogRepository.findByCatalogOrderByCreateTimeDesc(catalog,pageable);
		}
		else {
			//blogs= blogRepository.findAllOrderByCreateTimeDesc(pageable);
			blogs= blogRepository.findAll(pageable);
			//List<Comment> timeList = comments.stream().sorted(Comparator.comparing(Comment::getCreateTime).reversed()).collect(Collectors.toList());

		}
		return blogs;
	}

	@Override
	public Page<Blog> listBlogsByCatalogLikeAndSort(Long catalogId, Pageable pageable) {

		Catalog catalog =null;
		if (!org.springframework.util.StringUtils.isEmpty(catalogId)){
			catalog = catalogRepository.findOne(catalogId);
		}
		Page<Blog> blogs;
		if (catalog!=null){
			blogs= blogRepository.findByCatalog(catalog, pageable);
		}
		else {
			blogs= blogRepository.findAll(pageable);
		}
		return blogs;
	}

	@Override
	public void readingIncrease(Long id) {
		Blog blog = blogRepository.findOne(id);
		blog.setReadSize(blog.getCommentSize() + 1);
		//blogRepository.save(blog);
		saveBlog(blog);
	}

	@Override
	public Blog createComment(Long blogId, String commentContent, String toUserName) {

		Blog originalBlog = blogRepository.findOne(blogId);
		User formuser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Comment comment;
		if (StringUtils.isNotEmpty(toUserName)){
			User toUser = (User) userDetailsService.loadUserByUsername(toUserName);
			commentContent="@"+toUserName+"："+commentContent;
			 comment = new Comment(formuser,toUser, commentContent);
			 //添加提醒信息——未完成
		}else{

			 comment = new Comment(formuser, commentContent);
		}
		originalBlog.addComment(comment);
		return saveBlog(originalBlog);
	}

	@Override
	public void removeComment(Long blogId, Long commentId) {
		Blog originalBlog = blogRepository.findOne(blogId);
		originalBlog.removeComment(commentId);
		//blogRepository.save(originalBlog);
		saveBlog(originalBlog);
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
	public Long createVote(Long blogId) {
		Blog originalBlog = blogRepository.findOne(blogId);
		//获取当前登录用户
		User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Vote newvote = new Vote(user);
		boolean isExist = originalBlog.addVote(newvote);
		if (isExist) {
			throw new IllegalArgumentException("该用户已经点过赞了");
		}
		//blogRepository.save(originalBlog);
		saveBlog(originalBlog);

		Long voteId=0L;
		for (Vote vote : originalBlog.getVotes()){
			if (vote.getUser().getUsername().equals(user.getUsername())){
				voteId = vote.getId();
				break;
			}
		}
		return voteId;
	}

	@Override
	public void removeVote(Long blogId, Long voteId) {
		Blog originalBlog = blogRepository.findOne(blogId);
		originalBlog.removeVote(voteId);
		saveBlog(originalBlog);
		//blogRepository.save(originalBlog);
	}

	@Override
	public List<Blog> getBlogTop2HotComment(List<Blog> list){
		list.stream().forEach(blog ->
		{
			blog.setComments(
					blog.getComments().stream().sorted(
							Comparator.comparing(
									Comment::getVoteSize).reversed()).limit(2).collect(Collectors.toList())
			);
		});
		return list;
	}

	@Override
	public List<Blog> setBlogVoteAndCommentVoteListToUser(User principal,List<Blog> list){
		if (principal !=null) {
			for (Blog blog : list){
				List list1 = new ArrayList();
				for (Vote vote : blog.getVotes()){
					if (vote.getUser().getUsername().equals(principal.getUsername())){
						list1.add(vote);
						break;
					}
				}
				//如果用户点过博客的赞，则list返回，不然就返回null
				blog.setVotes(list1);
				for (Comment comment : blog.getComments()){
					List list2 = new ArrayList();
					for (Vote vote : comment.getVotes()){
						if (vote.getUser().getUsername().equals(principal.getUsername())){
							list2.add(vote);
							break;
						}
					}
					//如果用户点过该评论的赞，则list返回，不然就返回null
					comment.setVotes(list2);
				}
			}
		}
		return list;
	}

	@Override
	public List<BlogVO> setBlogCollectionIdByUser(User principal,List<BlogVO> blogVOS){
		if (principal !=null) {
			//查询用户的收藏夹信息
			List<Collection> collectionList = collectionService.findByUser(principal);
			if (collectionList.size() > 0) {
				//查出该用户的各个博客是否有收藏过，有的话将收藏夹id设置进去
				List<CollectionBlogVO> collectionBlogVOS = collectionService.selectCBbyCollIds(collectionList.stream().map(Collection::getId).collect(Collectors.toList()));
				Map<Long, Long> BlogIdCollIdMap = collectionBlogVOS.stream().collect(Collectors.toMap(CollectionBlogVO::getBlogId, CollectionBlogVO::getCollectionId, (o, n) -> o));
				if (!org.springframework.util.StringUtils.isEmpty(BlogIdCollIdMap)) {
					blogVOS.stream().forEach(CBV -> CBV.setCollectionId(BlogIdCollIdMap.get(CBV.getId())));
				}
			}
		}
		return blogVOS;
	}


}
