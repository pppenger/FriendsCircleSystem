package com.pppenger.microblog.service;

import com.pppenger.microblog.domin.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Collection;
import java.util.List;

/**
 * User 服务接口.
 * 
 * @since 1.0.0 2017年3月18日
 * @author <a href="https://waylau.com">Way Lau</a>
 */
public interface UserService {
	/**
	 * 保存用户
	 * @param user
	 * @return
	 */
	User saveUser(User user);
	
	/**
	 * 删除用户
	 * @param id
	 * @return
	 */
	void removeUser(Long id);
	
	/**
	 * 删除列表里面的用户
	 * @param users
	 * @return
	 */
	void removeUsersInBatch(List<User> users);
	
	/**
	 * 更新用户
	 * @param user
	 * @return
	 */
	User updateUser(User user);
	
	/**
	 * 根据id获取用户
	 * @param id
	 * @return
	 */
	User getUserById(Long id);
	
	/**
	 * 获取用户列表
	 * @return
	 */
	Page<User> listUsers(Pageable pageable);
	
	/**
	 * 根据用户名进行分页模糊查询
	 * @param name
	 * @param pageable
	 * @return
	 */
	Page<User> listUsersByNameLike(String name,  int pageIndex,int pageSize);

	/**
	 * 更具名称邮箱查询是否已被注册
	 * @param hadRegister
	 * @return
	 */

	String hadRegister(String username,String email);

	String hadRegister(String username,String email,User user);

	List<User> loadUserByUsernames(List usernames) throws UsernameNotFoundException;
}
