package com.pppenger.microblog.repository;

import com.pppenger.microblog.domin.Catalog;
import com.pppenger.microblog.domin.UserCatalog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserCatalogRepository extends JpaRepository<UserCatalog, Long> {

    /**
     * 根据用户查询
     * @param username
     * @return
     */
    List<UserCatalog> findByUsername(String username);

    /**
     * 根据用户查询
     * @param usernames
     * @return
     */
    List<UserCatalog> findByUsernameIn(List usernames);

    /**
     * 根据分区id查询
     * @param catalogId
     * @return
     */
    List<UserCatalog> findByCatalogId(String catalogId);


    /**
     * 根据用户查询
     * @param user
     * @param name
     * @return
     */
    //List<Catalog> findByUserAndName(User user, String name);
}
