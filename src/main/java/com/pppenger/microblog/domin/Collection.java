package com.pppenger.microblog.domin;

import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

@Entity // 实体
public class Collection implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id // 主键
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 自增长策略
    private Long id; // 用户的唯一标识

    @NotEmpty(message = "收藏夹命名不能为空")
    @Size(min = 2, max = 10)
    @Column(nullable = false, length = 30) // 映射为字段，值不能为空
    private String title;

    @OneToOne(cascade = CascadeType.DETACH, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToMany(cascade = CascadeType.DETACH, fetch = FetchType.LAZY)
    @JoinTable(name = "collection_blog", joinColumns = @JoinColumn(name = "collection_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "blog_id", referencedColumnName = "id"))
    private List<Blog> blogs;

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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<Blog> getBlogs() {
        return blogs;
    }

    public void setBlogs(List<Blog> blogs) {
        this.blogs = blogs;
    }

    /**
     * 添加微博
     * @param comment
     */
    public boolean addBlog(Blog blog) {

        boolean isExist = false;
        // 判断重复
        for (int index=0; index < this.blogs.size(); index ++ ) {
            if (this.blogs.get(index).getId() == blog.getId()) {
                isExist = true;
                break;
            }
        }

        if (!isExist) {
            this.blogs.add(blog);
        }

        return isExist;
    }

    /**
     * 删除微博
     * @param
     */
    public void removeBlog(Long blogId) {
        for (int index=0; index < this.blogs.size(); index ++ ) {
            if (blogs.get(index).getId() == blogId) {
                this.blogs.remove(index);
                break;
            }
        }
    }
}
