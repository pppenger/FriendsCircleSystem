package com.pppenger.microblog.domin;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
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
	@Column(nullable = false) // 映射为字段，值不能为空
	private String name;

	@NotEmpty(message = "分区规则不能为空")
	@Size(max=200)
	@Column(length = 200) // 映射为字段，值不能为空
	private String summary;

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinTable(name = "catalog_blogs", joinColumns = @JoinColumn(name = "catalog_id", referencedColumnName = "id"),
			inverseJoinColumns = @JoinColumn(name = "blog_id", referencedColumnName = "id"))
	private List<Blog> blogs;
 
	protected Catalog() {
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

	public List<Blog> getBlogs() {
		return blogs;
	}

	public void setBlogs(List<Blog> blogs) {
		this.blogs = blogs;
	}
}
