package com.pppenger.microblog.service;

import com.pppenger.microblog.domin.Blog;
import com.pppenger.microblog.domin.Collection;
import com.pppenger.microblog.domin.User;
import com.pppenger.microblog.repository.BlogRepository;
import com.pppenger.microblog.repository.CollectionRepository;
import com.pppenger.microblog.vo.CollectionBlogVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Service
public class CollectionServiceImpl implements CollectionService{

    @Autowired
    private CollectionRepository collectionRepository;


    @Autowired
    private BlogRepository blogRepository;

    @Override
    public Collection findById(Long id) {
        Collection collection = collectionRepository.findOne(id);
        return collection;
    }

    @Override
    public List<Collection> findByUser(User user) {
        List<Collection> collectionList = collectionRepository.findByUser(user);
        return collectionList;
    }

    @Transactional
    @Override
    public void removeCollection(Long id) {
        collectionRepository.delete(id);
    }


    @Transactional
    @Override
    public void delByBlogId(Long blogId) {
        collectionRepository.delByBlogId(blogId);
    }

    @Transactional
    @Override
    public void delByBlogIdAndCollectionId(Long blogId,Long collectionId) {
        collectionRepository.delByBlogIdAndCollectionId(blogId, collectionId);
    }

    @Override
    public List<CollectionBlogVO> selectCBbyCollIds(List<Long> collectionIds) {
        List<Object[]> objects = collectionRepository.selectCBbyCollIds(collectionIds);
        List<CollectionBlogVO> collectionBlogVOS = new ArrayList<>();
        for (Object[] ostr:objects){
            BigInteger a,b;
            a=(BigInteger)ostr[0];b=(BigInteger)ostr[1];
            collectionBlogVOS.add(new CollectionBlogVO(a.longValue(),b.longValue()));
        }
        return collectionBlogVOS;
    }

    @Transactional
    @Override
    public Collection saveCollection(Collection collection) {
        return collectionRepository.save(collection);
    }

    @Override
    public List<Collection> findByUserAndTitle(User user,String title ) {
        List<Collection> collectionList = collectionRepository.findByUserAndTitle(user, title );
        return collectionList;
    }

    @Override
    public List<Collection> findByUserAndTitleAndIdIsNot(User user,String title,Long id) {
        List<Collection> collectionList = collectionRepository.findByUserAndTitleAndIdIsNot(user, title, id);
        return collectionList;
    }



    @Override
    public Long collAddBlog(Long inCollId,Long blogId) {
        Collection collection = collectionRepository.getOne(inCollId);
        Blog blog = blogRepository.findOne(blogId);
        boolean isExist = collection.addBlog(blog);
        if (isExist) {
            throw new IllegalArgumentException("您已经收藏过该文章了");
        }
        collectionRepository.save(collection);

        return inCollId;
    }

    @Override
    public void removeBlog(Long inCollId,Long blogId) {
        Collection collection = collectionRepository.findOne(inCollId);
        collection.removeBlog(blogId);
        collectionRepository.save(collection);
    }
}
