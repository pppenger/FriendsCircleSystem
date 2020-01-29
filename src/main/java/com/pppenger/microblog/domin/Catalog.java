package com.pppenger.microblog.domin;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

import javax.persistence.*;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;

/**
 * Catalog 分区实体
 * 
 * @since 1.0.0 2019年12月19日
 * @author luqipeng
 */
@Entity // 实体
public class Catalog implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id // 主键
	@GeneratedValue(strategy = GenerationType.IDENTITY) // 自增长策略
	private Long id; // 用户的唯一标识

	@NotEmpty(message = "名称不能为空")
	@Size(min=2, max=8)
	@Column(nullable = false, length = 8, unique = true) // 映射为字段，值不能为空
	private String name;

	@NotEmpty(message = "分区规则不能为空")
	@Size(max=200)
	@Column(length = 200) // 映射为字段，值不能为空
	private String summary;

    //username提议者username
    @Column // 映射为字段，值不能为空
    private String username;

    //是否已经启用(0为未开启，1为开启)
    @Column(nullable = false, columnDefinition = "TINYINT", length = 2)
    private Integer isOpen;

	@Column(nullable = false) // 映射为字段，值不能为空
	@org.hibernate.annotations.CreationTimestamp  // 由数据库自动创建时间
	//@JsonFormat(shape= JsonFormat.Shape.STRING,pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
	private Timestamp createTime;


//	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//	@JoinTable(name = "catalog_blogs", joinColumns = @JoinColumn(name = "catalog_id", referencedColumnName = "id"),
//			inverseJoinColumns = @JoinColumn(name = "blog_id", referencedColumnName = "id"))
//	private List<Blog> blogs;

    public Catalog() {

    }

	public Catalog( String name) {
		this.name = name;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

//	public List<Blog> getBlogs() {
//		return blogs;
//	}
//
//	public void setBlogs(List<Blog> blogs) {
//		this.blogs = blogs;
//	}

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

	public Timestamp getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}
}
