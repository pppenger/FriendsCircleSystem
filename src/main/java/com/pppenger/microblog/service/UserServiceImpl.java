package com.pppenger.microblog.service;

import com.pppenger.microblog.domin.User;
import com.pppenger.microblog.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Collection;
import java.util.List;

/**
 * User 服务.
 * 
 * @since 1.0.0 2017年3月18日
 * @author <a href="https://waylau.com">Way Lau</a>
 */
@Service

public class UserServiceImpl implements UserService,UserDetailsService {

	@Autowired
	private UserRepository userRepository;
	
	@Transactional
	@Override
	public User saveUser(User user) {
		return userRepository.save(user);
	}

	@Transactional
	@Override
	public void removeUser(Long id) {
		userRepository.delete(id);
	}

	@Transactional
	@Override
	public void removeUsersInBatch(List<User> users) {
		userRepository.deleteInBatch(users);
	}
	
	@Transactional
	@Override
	public User updateUser(User user) {
		return userRepository.save(user);
	}

	@Override
	public User getUserById(Long id) {
		return userRepository.getOne(id);
	}

	@Override
	public Page<User> listUsers(Pageable pageable) {
		return userRepository.findAll(pageable);
	}

	@Override
	public Page<User> listUsersByNameLike(String name, int pageIndex,int pageSize) {
		Pageable pageable = new PageRequest(pageIndex, pageSize);
		// 模糊查询
		name = "%" + name + "%";
		Page<User> users = userRepository.findByUsernameLike(name, pageable);
		return users;
	}

	@Override
	public String hadRegister(String username, String email) {
		if (userRepository.findByUsername(username)!=null){
			return "用户名已被注册";
		}else if (userRepository.findByEmail(email)!=null){
			return "邮箱已被注册";
		}else {
			return null;
		}
	}

	@Override
	public String hadRegister(String username, String email, User user) {
		if (!user.getUsername().equals(username)){
			if (userRepository.findByUsername(username)!=null){
				return "用户名已被注册";
			}
		}
		if (!user.getEmail().equals(email)){
			if (userRepository.findByEmail(email)!=null){
				return "邮箱已被注册";
			}
		}

		return null;
	}

	//重写UserDetailsService的loadUserByUsername方法
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		UserDetails userDetails=userRepository.findByUsername(username);
		if(userDetails==null){
			throw new UsernameNotFoundException("The acccount not exist【账号不存在】");
		}
		return userDetails;
	}

	@Override
	public List<User> loadUserByUsernames(List usernames) throws UsernameNotFoundException {
		List<User> userList=userRepository.findByUsernameIn(usernames);
		if(userList==null){
			throw new UsernameNotFoundException("查询用户异常");
		}
		return userList;
	}


}
