package com.pppenger.microblog.repository;

import java.util.List;

import com.pppenger.microblog.domin.Catalog;
import org.springframework.data.jpa.repository.JpaRepository;


/**
 * Catalog 仓库.
 *
 * @since 1.0.0 2017年4月10日
 * @author <a href="https://waylau.com">Way Lau</a> 
 */
public interface CatalogRepository extends JpaRepository<Catalog, Long>{
	
	/**
	 * 根据用户查询
	 * @param user
	 * @return
	 */
	//List<Catalog> findByUser(User user);
	
	/**
	 * 根据用户查询
	 * @param user
	 * @param name
	 * @return
	 */
	//List<Catalog> findByUserAndName(User user, String name);


	List<Catalog> findByUsernameIn(List usernames);

	List<Catalog> findByCatalogIdIn(List catalogIds);
}
