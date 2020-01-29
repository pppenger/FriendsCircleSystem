package com.pppenger.microblog.vo;

public class CollectionBlogVO {

    private Long collectionId;
    private Long blogId;

    public Long getCollectionId() {
        return collectionId;
    }

    public void setCollectionId(Long collectionId) {
        this.collectionId = collectionId;
    }

    public Long getBlogId() {
        return blogId;
    }

    public void setBlogId(Long blogId) {
        this.blogId = blogId;
    }

    public CollectionBlogVO(Long collectionId, Long blogId) {
        this.collectionId = collectionId;
        this.blogId = blogId;
    }
}
