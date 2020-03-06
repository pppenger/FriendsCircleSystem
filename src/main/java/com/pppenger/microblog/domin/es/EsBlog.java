package com.pppenger.microblog.domin.es;


import com.pppenger.microblog.domin.Blog;
import com.pppenger.microblog.domin.Picture;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldIndex;
import org.springframework.data.elasticsearch.annotations.FieldType;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

@Document(indexName = "blog", type = "blog")
@XmlRootElement // MediaType 转为 XML
public class EsBlog implements Serializable {
    private static final long serialVersionUID = 1L;


    @Id  // 主键
    private String id;
    @Field(index = FieldIndex.not_analyzed)
    private Long blogId; // Blog 的 id

    private String title;

    private String summary;

    @Field(index = FieldIndex.not_analyzed)  // 不做全文检索字段
    private String username;
    @Field(index = FieldIndex.not_analyzed)  // 不做全文检索字段
    private String avatar;
    @Field(index = FieldIndex.not_analyzed)  // 不做全文检索字段
    private Timestamp createTime;
    @Field(index = FieldIndex.not_analyzed)  // 不做全文检索字段
    private Integer readSize = 0; // 访问量、阅读量
    @Field(index = FieldIndex.not_analyzed)  // 不做全文检索字段
    private Integer commentSize = 0;  // 评论量
    @Field(index = FieldIndex.not_analyzed)  // 不做全文检索字段
    private Integer voteSize = 0;  // 点赞量
    @Field(index = FieldIndex.not_analyzed)  // 不做全文检索字段
    private Integer reportSize = 0;  // 举报量
    @Field(type= FieldType.Nested)
    private List<String> pictures;//市

    protected EsBlog() {  // JPA 的规范要求无参构造函数；设为 protected 防止直接使用
    }

    public EsBlog(Blog blog){
        this.blogId = blog.getId();
        this.title = blog.getTitle();
        this.summary = blog.getSummary();
        this.username = blog.getUser().getUsername();
        this.avatar = blog.getUser().getAvatar();
        this.createTime = blog.getCreateTime();
        this.readSize = blog.getReadSize();
        this.commentSize = blog.getCommentSize();
        this.voteSize = blog.getVoteSize();
        this.reportSize = blog.getReportSize();
        this.pictures = blog.getPictures()==null ? null : blog.getPictures().stream().map(Picture::getPictureURL).collect(Collectors.toList());
    }

    public void update(Blog blog){
        this.blogId = blog.getId();
        this.title = blog.getTitle();
        this.summary = blog.getSummary();
        this.username = blog.getUser().getUsername();
        this.avatar = blog.getUser().getAvatar();
        this.createTime = blog.getCreateTime();
        this.readSize = blog.getReadSize();
        this.commentSize = blog.getCommentSize();
        this.voteSize = blog.getVoteSize();
        this.reportSize = blog.getReportSize();
        this.pictures = blog.getPictures().stream().map(Picture::getPictureURL).collect(Collectors.toList());
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getBlogId() {
        return blogId;
    }

    public void setBlogId(Long blogId) {
        this.blogId = blogId;
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
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

    public List<String> getPictures() {
        return pictures;
    }

    public void setPictures(List<String> pictures) {
        this.pictures = pictures;
    }

    @Override
    public String toString() {
        return String.format(
                "User[id=%d, title='%s', summary='%s']",
                blogId, title, summary);
    }
}
