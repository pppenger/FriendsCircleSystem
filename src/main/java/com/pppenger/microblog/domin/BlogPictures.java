package com.pppenger.microblog.domin;

import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Entity // 实体
public class BlogPictures  implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id // 主键
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 自增长策略
    private Long id; // 用户的唯一标识


    @NotEmpty(message = "userId为空")
    @Column(nullable = false, unique = true)
    private Long userId;

    @NotEmpty(message = "blogPicture为空")
    @Column(length = 200)
    private String blogPicture; // 微博图片地址
}
