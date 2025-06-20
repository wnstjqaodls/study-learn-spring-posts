package com.example.studylearnspringposts.domain.post.vo;

import com.example.studylearnspringposts.domain.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.Date;

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

    // @Column(nullable = false ,unique = true, length = 12)
    // private String postSerialNo;

    @NotBlank(message = "제목은 필수입니다")
    @Size(max = 200, message = "제목은 200자를 초과할 수 없습니다")
    @Column(nullable = false)
    private String title;
    
    @NotBlank(message = "작성자는 필수입니다")
    @Size(max = 100, message = "작성자명은 100자를 초과할 수 없습니다")
    @Column(nullable = false)
    private String author;
    
    @NotBlank(message = "비밀번호는 필수입니다")
    @Column(nullable = false)
    private String password;
    
    @NotBlank(message = "내용은 필수입니다")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;
    
    @Column(nullable = false)
    private LocalDateTime writeDate;
    



}
