package com.example.studylearnspringposts.domain.post.vo;

import com.example.studylearnspringposts.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@Entity
public class Post extends BaseEntity {
    @Id
    @Column(nullable = false ,unique = true, length = 12)
    private Long id;

    @Column(nullable = false ,unique = true, length = 12)
    private String postSerialNo;

    private String title;
    private String content;
    private LocalDateTime writeDate;
    



}
