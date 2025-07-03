package com.example.studylearnspringposts.domain.post.vo;

import com.example.studylearnspringposts.domain.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Post extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Size(max = 200, message = "제목은 200자를 초과할 수 없습니다")
    @Column(nullable = false)
    private String title;
    
    @Size(max = 100, message = "작성자명은 100자를 초과할 수 없습니다")
    @Column(nullable = false)
    private String author;
    
    @Column(nullable = false)
    private String password;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;
    
    @Column(nullable = false)
    private LocalDateTime writeDate;

}
