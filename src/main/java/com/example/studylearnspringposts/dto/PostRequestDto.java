package com.example.studylearnspringposts.dto;

import com.example.studylearnspringposts.domain.post.vo.Post;
import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostRequestDto {
    private String title;
    private String author;
    private String password;
    private String content;

    public Post toEntity() {
        return Post.builder()
                .title(title)
                .author(author)
                .password(password)
                .content(content)
                .writeDate(LocalDateTime.now())
                .build();
    }
} 