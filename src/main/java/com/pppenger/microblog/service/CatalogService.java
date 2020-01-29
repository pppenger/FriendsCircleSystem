package com.pppenger.microblog.service;

import java.util.List;

import com.pppenger.microblog.domin.Catalog;
import com.pppenger.microblog.domin.User;
import com.pppenger.microblog.domin.UserCatalog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Catalog 服务接口.
 * 
 * @since 1.0.0 2017年4月10日
 * @author <a href="https://waylau.com">Way Lau</a>
 */
public interface CatalogService {
//	/**
//	 * 保存Catalog
//	 * @param catalog
//	 * @return
//	 */
//	Catalog saveCatalog(Catalog catalog);
//
//	/**
//	 * 删除Catalog
//	 * @param id
//	 * @return
//	 */
//	void removeCatalog(Long id);
//
//	/**
//	 * 根据id获取Catalog
//	 * @param id
//	 * @return
//	 */
//	Catalog getCatalogById(Long id);
//
//	/**
//	 * 获取Catalog列表
//	 * @return
//	 */
//	List<Catalog> listCatalogs(User user);
   List<UserCatalog> findCatalogUserByUsername(String userName);

   List<Catalog> listCatalogs(String userName);

//   List<Catalog> listCatalogNames(String catalogId);

   List<Catalog> listCatalogs(Integer isopen);

   Page<Catalog> listUnOpenCatalogs(Integer isopen, int pageIndex, int pageSize);

   List<UserCatalog> listUserCatalog();

   List<UserCatalog> listUsernamesByCatalog(Long catalogId);

   Catalog saveCatalog(Catalog catalog);

   UserCatalog saveUserCatalog(UserCatalog userCatalog);

   List<UserCatalog> findByUsernameAndCatalogId(String username, Long catalogId);

   void deleteByUsernameAndCatalogId(String username, Long catalogId);

   Catalog getCatalogById(Long catalogId);
}
