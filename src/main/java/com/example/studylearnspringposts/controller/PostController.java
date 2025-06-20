package com.example.studylearnspringposts.controller;

import com.example.studylearnspringposts.domain.post.vo.Post;
import com.example.studylearnspringposts.dto.PostRequestDto;
import com.example.studylearnspringposts.dto.PostResponseDto;
import com.example.studylearnspringposts.service.PostService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 게시글 마이크로서비스 컨트롤러
 * 게시글 관련 비즈니스 로직을 처리
 */
@Component
public class PostController {
    
    private final PostService postService;
    
    public PostController(PostService postService) {
        this.postService = postService;
    }
    
    /**
     * 전체 게시글 목록 조회 (작성 날짜 기준 내림차순)
     */
    public List<PostResponseDto> getAllPosts() {
        List<Post> posts = postService.getAllPosts();
        return posts.stream()
                .map(PostResponseDto::fromEntity)
                .collect(Collectors.toList());
    }
    
    /**
     * 게시글 작성
     */
    public PostResponseDto createPost(PostRequestDto postRequestDto) {
        Post post = postRequestDto.toEntity();
        Post savedPost = postService.createPost(post);
        return PostResponseDto.fromEntity(savedPost);
    }
    
    /**
     * 특정 게시글 조회
     */
    public PostResponseDto getPostById(Long id) {
        Optional<Post> post = postService.getPostById(id);
        return PostResponseDto.fromOptionalEntity(post);
    }
} 