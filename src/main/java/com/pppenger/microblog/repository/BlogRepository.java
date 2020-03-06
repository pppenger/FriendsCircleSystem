package com.pppenger.microblog.repository;

import com.pppenger.microblog.domin.Blog;
import com.pppenger.microblog.domin.Catalog;
import com.pppenger.microblog.domin.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Blog 仓库.
 *
 * @since 1.0.0 2017年4月7日
 * @author <a href="https://waylau.com">Way Lau</a> 
 */
public interface BlogRepository extends JpaRepository<Blog, Long>{
	/**
	 * 根据用户名分页查询用户列表
	 * @param user
	 * @param title
	 * @param pageable
	 * @return
	 */
	Page<Blog> findByUserAndTitleLikeOrderByCreateTimeDesc(User user, String title, Pageable pageable);

//	Page<Blog> findAllOrderByCreateTimeDesc(Pageable pageable);
//	Page<Blog> findAllByOrOrderByCreateTimeDesc(Pageable pageable);
	/**
	 * 根据用户名分页查询用户列表
	 * @param user
	 * @param title
	 * @param sort
	 * @param pageable
	 * @return
	 */
//	@Query(value = "select id,title,summary,user,createTime,readSize,commentSize,voteSize,reportSize,comments,votes,pictures" +
//			" from Blog b where b.user = :user and b.title like :title")
	Page<Blog> findByUserAndTitleLike(User user, String title, Pageable pageable);

	/**
	 * 根据用户名分页查询用户列表
	 * @param user
	 * @param title
	 * @param pageable
	 * @return
	 */
	Page<Blog> findByUserAndCatalogAndTitleLikeOrderByCreateTimeDesc(User user, Catalog catalog, String title, Pageable pageable);

	Page<Blog> findByCatalogOrderByCreateTimeDesc(Catalog catalog, Pageable pageable);

	/**
	 * 根据用户名分页查询用户列表
	 * @param user
	 * @param title
	 * @param sort
	 * @param pageable
	 * @return
	 */
	Page<Blog> findByUserAndCatalogAndTitleLike(User user,Catalog catalog, String title, Pageable pageable);

	Page<Blog> findByCatalog(Catalog catalog,Pageable pageable);
}
