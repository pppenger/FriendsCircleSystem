package com.pppenger.microblog.domin;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

/**
 * Blog 实体
 *
 * @since 1.0.0 2019年11月14日
 * @author 卢启鹏
 */
@Entity // 实体
public class Blog implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id // 主键
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 自增长策略
    private Long id; // 用户的唯一标识

    @NotEmpty(message = "标题不能为空")
    @Size(min=2, max=20)
    @Column(nullable = false, length = 50) // 映射为字段，值不能为空
    private String title;

    @NotEmpty(message = "摘要不能为空")
    @Size(max=150)
    @Column(length = 150) // 映射为字段，值不能为空
    private String summary;

    @OneToOne(cascade = CascadeType.DETACH, fetch = FetchType.LAZY)
    @JoinColumn(name="user_id")
    private User user;

    @Column(nullable = false) // 映射为字段，值不能为空
    @org.hibernate.annotations.CreationTimestamp  // 由数据库自动创建时间
    //@JsonFormat(shape= JsonFormat.Shape.STRING,pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Timestamp createTime;

    @Column(name="readSize")
    private Integer readSize = 0; // 访问量、阅读量

    @Column(name="commentSize")
    private Integer commentSize = 0;  // 评论量

    @Column(name="voteSize")
    private Integer voteSize = 0;  // 点赞量

    @Column(name="report_size")
    private Integer reportSize = 0;  // 举报量


    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(name = "blog_comment", joinColumns = @JoinColumn(name = "blog_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "comment_id", referencedColumnName = "id"))
    private List<Comment> comments;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(name = "blog_vote", joinColumns = @JoinColumn(name = "blog_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "vote_id", referencedColumnName = "id"))
    private List<Vote> votes;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(name = "blog_pictures", joinColumns = @JoinColumn(name = "blog_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "picture_id", referencedColumnName = "id"))
    private List<Picture> pictures;

//    @OneToOne(cascade = CascadeType.DETACH, fetch = FetchType.LAZY)
//    @JoinColumn(name="catalog_id")
//    private Partition catalog;


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

    public List<Picture> getPictures() {
        return pictures;
    }

    public void setPictures(List<Picture> pictures) {
        this.pictures = pictures;
    }

    /**
     * 添加评论
     * @param comment
     */
    public void addComment(Comment comment) {
        this.comments.add(comment);
        this.commentSize = this.comments.size();
    }
    /**
     * 删除评论
     * @param
     */
    public void removeComment(Long commentId) {
        for (int index=0; index < this.comments.size(); index ++ ) {
            if (comments.get(index).getId() == commentId) {
                this.comments.remove(index);
                break;
            }
        }

        this.commentSize = this.comments.size();
    }
}
