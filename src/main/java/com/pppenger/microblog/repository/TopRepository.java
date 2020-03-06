package com.pppenger.microblog.repository;

import com.pppenger.microblog.domin.Top;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;

public interface TopRepository extends JpaRepository<Top, Long> {

    @Modifying
    @Transactional
    @Query(value = "UPDATE top set top_username =?1 WHERE id=1 and top_username is NULL;", nativeQuery=true)
    int pickTop(String username);
}
