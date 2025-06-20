package com.example.studylearnspringposts.controller;

import com.example.studylearnspringposts.dto.PostRequestDto;
import com.example.studylearnspringposts.dto.PostResponseDto;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * API 게이트웨이 컨트롤러
 * 클라이언트의 모든 요청을 받아서 적절한 마이크로서비스로 라우팅하는 역할
 */
@RestController
@RequestMapping("/api/v1")
@CrossOrigin(origins = "*")
public class ApiGatewayController {
    
    private final PostController postController;
    
    public ApiGatewayController(PostController postController) {
        this.postController = postController;
    }
    
    /**
     * 게이트웨이 상태 확인
     */
    @GetMapping("/health")
    public String healthCheck() {
        return "API Gateway is running";
    }
    
    /**
     * 전체 게시글 목록 조회 API
     * - 제목, 작성자명, 작성 내용, 작성 날짜를 조회
     * - 작성 날짜 기준 내림차순으로 정렬
     */
    @GetMapping("/posts")
    public List<PostResponseDto> getAllPosts() {
        return postController.getAllPosts();
    }
    
    /**
     * 게시글 작성 API
     * - 제목, 작성자명, 비밀번호, 작성 내용을 저장
     * - 저장된 게시글을 Client로 반환
     */
    @PostMapping("/posts")
    public PostResponseDto createPost(@RequestBody PostRequestDto postRequestDto) {
        return postController.createPost(postRequestDto);
    }
    
    /**
     * 선택한 게시글 조회 API
     * - 선택한 게시글의 제목, 작성자명, 작성 날짜, 작성 내용을 조회
     */
    @GetMapping("/posts/{id}")
    public PostResponseDto getPostById(@PathVariable Long id) {
        return postController.getPostById(id);
    }
} 