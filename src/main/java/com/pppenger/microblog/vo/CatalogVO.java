package com.pppenger.microblog.vo;

public class CatalogVO {
    private Long id; // 用户的唯一标识

    private String name;

    private String summary;

    private String username;

    private Integer isOpen;

    private Integer isStar;

    public CatalogVO() {
    }

    public CatalogVO(Long id, String name, String summary, String username, Integer isOpen, Integer isStar) {
        this.id = id;
        this.name = name;
        this.summary = summary;
        this.username = username;
        this.isOpen = isOpen;
        this.isStar = isStar;
    }

    public Integer getIsStar() {
        return isStar;
    }

    public void setIsStar(Integer isStar) {
        this.isStar = isStar;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getIsOpen() {
        return isOpen;
    }

    public void setIsOpen(Integer isOpen) {
        this.isOpen = isOpen;
    }
}
