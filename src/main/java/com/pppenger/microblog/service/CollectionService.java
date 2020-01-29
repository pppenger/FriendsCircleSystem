package com.pppenger.microblog.service;

import com.pppenger.microblog.domin.Collection;
import com.pppenger.microblog.domin.User;
import com.pppenger.microblog.vo.CollectionBlogVO;

import java.util.List;

public interface CollectionService {

    Collection findById(Long id);

    List<Collection> findByUser(User user);

    public void removeCollection(Long id);

    void delByBlogId(Long blogId);

    void delByBlogIdAndCollectionId(Long blogId,Long collectionId);

    List<CollectionBlogVO> selectCBbyCollIds(List<Long> collectionIds);

    Collection saveCollection(Collection collection);

    List<Collection> findByUserAndTitle(User user,String title );

    List<Collection> findByUserAndTitleAndIdIsNot(User user,String title,Long id);

    Long collAddBlog(Long inCollId,Long blogId);

    void removeBlog(Long inCollId,Long blogId);
}
