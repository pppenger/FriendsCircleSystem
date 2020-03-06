package com.pppenger.microblog.repository;

import com.pppenger.microblog.domin.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Collection;
import java.util.List;

/**
 * 用户仓库.
 *
 * @since 1.0.0 2017年3月2日
 * @author <a href="https://waylau.com">Way Lau</a> 
 */

public interface UserRepository extends JpaRepository<User, Long> {

	/**
	 * 根据用户名分页查询用户列表
	 * @param name
	 * @param pageable
	 * @return
	 */
	Page<User> findByUsernameLike(String name, Pageable pageable);
	/**
	 * 根据名称查询
	 * @param username
	 * @return
	 */
	User findByUsername(String username);

	User findByEmail(String email);

	List<User> findByUsernameIn(List username);

	List<User> findByScoreLessThan(Integer score);

}
