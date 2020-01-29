package com.pppenger.microblog.repository;

import com.pppenger.microblog.domin.Collection;
import com.pppenger.microblog.domin.User;
import com.pppenger.microblog.vo.CollectionBlogVO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.List;

public interface CollectionRepository extends JpaRepository<Collection, Long> {

    List<Collection> findByUser(User user);

    List<Collection> findByUserAndTitleAndIdIsNot(User user,String title,Long id);

    List<Collection> findByUserAndTitle(User user,String title);

    @Modifying
    @Transactional
    @Query(value = "delete from collection_blog where blog_id=?1", nativeQuery=true)
    void delByBlogId(Long blogId);

    @Modifying
    @Transactional
    @Query(value = "delete from collection_blog where blog_id=?1 and collection_id =?2", nativeQuery=true)
    void delByBlogIdAndCollectionId(Long blogId,Long collectionId);


    @Query(value = "select collection_id,blog_id from collection_blog where collection_id in ?1", nativeQuery=true)
    List<Object[]> selectCBbyCollIds(List<Long> collectionIds);



}
