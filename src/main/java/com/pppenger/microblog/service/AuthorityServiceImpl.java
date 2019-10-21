/**
 * 
 */
package com.pppenger.microblog.service;

import com.pppenger.microblog.domin.Authority;
import com.pppenger.microblog.repository.AuthorityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Authority 服务.
 * 
 * @since 1.0.0 2017年3月30日
 * @author <a href="https://waylau.com">Way Lau</a>
 */
@Service
public class AuthorityServiceImpl  implements AuthorityService {
	
	@Autowired
	private AuthorityRepository authorityRepository;
//	Optional 类是一个可以为null的容器对象。如果值存在则isPresent()方法会返回true，调用get()方法会返回该对象。
//
//	Optional 是个容器：它可以保存类型T的值，或者仅仅保存null。Optional提供很多有用的方法，这样我们就不用显式进行空值检测。
//
//	Optional 类的引入很好的解决空指针异常。
	@Override
	public Authority getAuthorityById(Long id) {

		return authorityRepository.findOne(id);
	}


}
