package com.pppenger.microblog.vo;

import com.pppenger.microblog.domin.*;
import java.sql.Timestamp;
import java.util.List;

public class BlogVO {

    private Long id; // 用户的唯一标识

    private String title;

    private String summary;

    private User user;

    private Timestamp createTime;

    private Integer readSize = 0; // 访问量、阅读量

    private Integer commentSize = 0;  // 评论量

    private Integer voteSize = 0;  // 点赞量

    private Integer reportSize = 0;  // 举报量

    private List<Comment> comments;

    private List<Vote> votes;

    private List<Picture> pictures;

    private Catalog catalog;

    private Long collectionId;

    public BlogVO() {
    }

    public BlogVO(Long id, String title, String summary, User user, Timestamp createTime, Integer readSize, Integer commentSize, Integer voteSize, Integer reportSize, List<Comment> comments, List<Vote> votes, List<Picture> pictures, Catalog catalog, Long collectionId) {
        this.id = id;
        this.title = title;
        this.summary = summary;
        this.user = user;
        this.createTime = createTime;
        this.readSize = readSize;
        this.commentSize = commentSize;
        this.voteSize = voteSize;
        this.reportSize = reportSize;
        this.comments = comments;
        this.votes = votes;
        this.pictures = pictures;
        this.catalog = catalog;
        this.collectionId = collectionId;
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

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    public Integer getReadSize() {
        return readSize;
    }

    public void setReadSize(Integer readSize) {
        this.readSize = readSize;
    }

    public Integer getCommentSize() {
        return commentSize;
    }

    public void setCommentSize(Integer commentSize) {
        this.commentSize = commentSize;
    }

    public Integer getVoteSize() {
        return voteSize;
    }

    public void setVoteSize(Integer voteSize) {
        this.voteSize = voteSize;
    }

    public Integer getReportSize() {
        return reportSize;
    }

    public void setReportSize(Integer reportSize) {
        this.reportSize = reportSize;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public List<Vote> getVotes() {
        return votes;
    }

    public void setVotes(List<Vote> votes) {
        this.votes = votes;
    }

    public List<Picture> getPictures() {
        return pictures;
    }

    public void setPictures(List<Picture> pictures) {
        this.pictures = pictures;
    }

    public Catalog getCatalog() {
        return catalog;
    }

    public void setCatalog(Catalog catalog) {
        this.catalog = catalog;
    }

    public Long getCollectionId() {
        return collectionId;
    }

    public void setCollectionId(Long collectionId) {
        this.collectionId = collectionId;
    }
}
