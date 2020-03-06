package com.pppenger.microblog.domin;

import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Entity // 实体
public class Top  implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id // 主键
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 自增长策略
    private Long id; // 用户的唯一标识

    @NotEmpty(message = "微博拥有者不能为空")
    @Column(nullable = false, length =20) // 映射为字段，值不能为空
    private String blogOwnerUsername;

    @NotEmpty(message = "标题不能为空")
    @Column(nullable = false, length = 50) // 映射为字段，值不能为空
    private String title;

    @Column(nullable = false) // 映射为字段，值不能为空
    private Long blogId;


    @Column(nullable = true, length =20) // 映射为字段，值不能为空
    private String topUsername;


    //是否已经启用(0为未发表，1为已发表)
    @Column(nullable = false, columnDefinition = "TINYINT", length = 2)
    private Integer haveSend;

    public String getBlogOwnerUsername() {
        return blogOwnerUsername;
    }

    public void setBlogOwnerUsername(String blogOwnerUsername) {
        this.blogOwnerUsername = blogOwnerUsername;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getBlogId() {
        return blogId;
    }

    public void setBlogId(Long blogId) {
        this.blogId = blogId;
    }

    public String getTopUsername() {
        return topUsername;
    }

    public void setTopUsername(String topUsername) {
        this.topUsername = topUsername;
    }

    public Integer getHaveSend() {
        return haveSend;
    }

    public void setHaveSend(Integer haveSend) {
        this.haveSend = haveSend;
    }


    public Top() {
    }

    public Top(String blogOwnerUsername, String title, Long blogId, String topUsername, Integer haveSend) {
        this.blogOwnerUsername = blogOwnerUsername;
        this.title = title;
        this.blogId = blogId;
        this.topUsername = topUsername;
        this.haveSend = haveSend;
    }
}
