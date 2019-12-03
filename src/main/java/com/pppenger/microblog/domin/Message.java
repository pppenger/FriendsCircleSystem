package com.pppenger.microblog.domin;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.sql.Timestamp;

@Entity // 实体
public class Message  implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id // 主键
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 自增长策略
    private Long id; // 用户的唯一标识


    @NotEmpty(message = "userId为空")
    @Column(nullable = false, unique = true)
    private Long userId;

    @NotEmpty(message = "blogId为空")
    @Column(nullable = false, columnDefinition = "TINYINT default 0", length = 2)
    private String hadRead; // 微博图片地址


    @Column(nullable = false) // 映射为字段，值不能为空
    @org.hibernate.annotations.CreationTimestamp  // 由数据库自动创建时间
    private Timestamp createTime;
}
