package com.pppenger.microblog.vo;

public class CollectionVO {

    private Long id; // 用户的唯一标识

    private String title;

    public CollectionVO() {
    }

    public CollectionVO(Long id, String title) {
        this.id = id;
        this.title = title;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
