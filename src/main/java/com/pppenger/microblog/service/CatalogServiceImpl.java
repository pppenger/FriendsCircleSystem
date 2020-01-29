package com.pppenger.microblog.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.pppenger.microblog.domin.Catalog;
import com.pppenger.microblog.domin.UserCatalog;
import com.pppenger.microblog.repository.CatalogRepository;
import com.pppenger.microblog.repository.UserCatalogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

/**
 * Catalog 服务.
 * 
 * @since 1.0.0 2017年4月10日
 * @author <a href="https://waylau.com">Way Lau</a>
 */
@Service
public class CatalogServiceImpl implements CatalogService{

    @Autowired
	private UserCatalogRepository userCatalogRepository;

	@Autowired
	private CatalogRepository catalogRepository;
//
//	@Override
//	public Catalog saveCatalog(Catalog catalog) {
//		// 判断重复
//		List<Catalog> list = catalogRepository.findByUserAndName(catalog.getUser(), catalog.getName());
//		if(list !=null && list.size() > 0) {
//			throw new IllegalArgumentException("该分类已经存在了");
//		}
//		return catalogRepository.save(catalog);
//	}
//
//	@Override
//	public void removeCatalog(Long id) {
//		catalogRepository.delete(id);
//	}
//
//	@Override
//	public Catalog getCatalogById(Long id) {
//		return catalogRepository.findOne(id);
//	}
//
//	@Override
//	public List<Catalog> listCatalogs(User user) {
//		return catalogRepository.findByUser(user);
//	}
    @Override
    public List<UserCatalog> findCatalogUserByUsername(String userName) {
        List<UserCatalog> userCatalogList = userCatalogRepository.findByUsername(userName);
        return userCatalogList;
    }

    @Override
	public List<Catalog> listCatalogs(String userName) {
        List<UserCatalog> userCatalogList = userCatalogRepository.findByUsername(userName);
        List<Long> catalogIds = userCatalogList.stream().map(UserCatalog::getCatalogId).collect(Collectors.toList());
		return catalogRepository.findByIdInAndIsOpen(catalogIds,1);
	}

//    @Override
//    public List<Catalog> listCatalogNames(String catalogId) {
//        List list = new ArrayList();
//        list.add(catalogId);
//        return catalogRepository.findByIdInAndIsOpen(list,1);
//    }

    @Override
    public List<UserCatalog> listUsernamesByCatalog(Long catalogId) {
        return userCatalogRepository.findByCatalogId(catalogId);
    }

    @Override
    public List<UserCatalog> listUserCatalog() {
        return userCatalogRepository.findAll();
    }

    @Override
    public List<Catalog> listCatalogs(Integer isopen) {
        return catalogRepository.findByIsOpen(isopen);
    }

    @Override
    public Page<Catalog> listUnOpenCatalogs(Integer isopen, int pageIndex, int pageSize) {
        Pageable pageable = new PageRequest(pageIndex, pageSize);
        return catalogRepository.findByIsOpen(isopen,pageable);
    }


    @Transactional
    @Override
    public Catalog saveCatalog(Catalog catalog) {
        return catalogRepository.save(catalog);
    }

    @Transactional
    @Override
    public UserCatalog saveUserCatalog(UserCatalog userCatalog) {
        return userCatalogRepository.save(userCatalog);
    }

    @Override
    public List<UserCatalog> findByUsernameAndCatalogId(String username, Long catalogId) {
        return userCatalogRepository.findByUsernameAndCatalogId(username,catalogId);
    }

    @Transactional
    @Override
    public void deleteByUsernameAndCatalogId(String username, Long catalogId) {
        userCatalogRepository.deleteByUsernameAndCatalogId(username,catalogId);
        return;
    }

    @Override
    public Catalog getCatalogById(Long catalogId) {
        return catalogRepository.getOne(catalogId);
    }
}
