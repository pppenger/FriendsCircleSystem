package com.pppenger.microblog.service;

import java.util.List;

import com.pppenger.microblog.domin.Catalog;
import com.pppenger.microblog.domin.User;
import com.pppenger.microblog.domin.UserCatalog;

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


   List<Catalog> listCatalogs(String userName);

   List<Catalog> listCatalogNames(String catalogId);

   List<Catalog> listCatalogs();

   List<UserCatalog> listUsernamesByCatalog(String catalogId);
}
