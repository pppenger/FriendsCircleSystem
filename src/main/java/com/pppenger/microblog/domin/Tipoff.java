package com.pppenger.microblog.domin;

import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.sql.Timestamp;

@Entity // 实体
public class Tipoff implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id // 主键
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 自增长策略
    private Long id; // 用户的唯一标识

    @NotEmpty(message = "举报者不能为空")
    @Column(nullable = false, length =20) // 映射为字段，值不能为空
    private String formUsername;

    @NotEmpty(message = "被举报者不能为空")
    @Column(nullable = false, length =20) // 映射为字段，值不能为空
    private String toUsername;

    private int toUserIsClosed;

    @NotEmpty(message = "微博拥有者不能为空")
    @Column(nullable = false, length =20) // 映射为字段，值不能为空
    private String blogOwner;

    @NotNull(message = "blogId不能为空")
    @Column(nullable = false) // 映射为字段，值不能为空
    private Long blogId;

    @Column(nullable = true) // 映射为字段，值不能为空
    private Long commentId;

    @NotEmpty(message = "举报原因不能为空")
    @Size(min=10,max=1000)
    @Column(length = 1000) // 映射为字段，值不能为空
    private String reason;

    @NotEmpty(message = "举报类型不能为空(blog为微博举报，comment为评论举报)")
    @Column(nullable = false, length =20) // 映射为字段，值不能为空
    private String type;

    //是否已经启用(0为未解决，1为已解决)
//    @Column(nullable = false, columnDefinition = "TINYINT", length = 2)
//    private Integer isSolve;

    @Column(nullable = false) // 映射为字段，值不能为空
    @org.hibernate.annotations.CreationTimestamp  // 由数据库自动创建时间
    //@JsonFormat(shape= JsonFormat.Shape.STRING,pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Timestamp createTime;

    protected Tipoff() {
    }

    public int getToUserIsClosed() {
        return toUserIsClosed;
    }

    public void setToUserIsClosed(int toUserIsClosed) {
        this.toUserIsClosed = toUserIsClosed;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFormUsername() {
        return formUsername;
    }

    public void setFormUsername(String formUsername) {
        this.formUsername = formUsername;
    }

    public String getToUsername() {
        return toUsername;
    }

    public void setToUsername(String toUsername) {
        this.toUsername = toUsername;
    }

    public Long getBlogId() {
        return blogId;
    }

    public void setBlogId(Long blogId) {
        this.blogId = blogId;
    }

    public Long getCommentId() {
        return commentId;
    }

    public void setCommentId(Long commentId) {
        this.commentId = commentId;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

//    public Integer getIsSolve() {
//        return isSolve;
//    }
//
//    public void setIsSolve(Integer isSolve) {
//        this.isSolve = isSolve;
//    }

    public String getBlogOwner() {
        return blogOwner;
    }

    public void setBlogOwner(String blogOwner) {
        this.blogOwner = blogOwner;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }
}
