package com.example.studylearnspringposts.dto;

import com.example.studylearnspringposts.domain.post.vo.Post;
import com.example.studylearnspringposts.exception.PostNotFoundException;
import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.Optional;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostResponseDto {
    private Long id;
    private String title;
    private String author;
    private String content;
    private LocalDateTime writeDate;

    public static PostResponseDto fromEntity(Post post) {
        return PostResponseDto.builder()
                .id(post.getId())
                .title(post.getTitle())
                .author(post.getAuthor())
                .content(post.getContent())
                .writeDate(post.getWriteDate())
                .build();
    }

    public static PostResponseDto fromOptionalEntity(Optional<Post> postOptional) {
        if (postOptional.isPresent()) {
            return fromEntity(postOptional.get());
        }
        throw new PostNotFoundException("게시글을 찾을 수 없습니다.");
    }
} 
