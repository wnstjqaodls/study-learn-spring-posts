package com.example.studylearnspringposts.controller;

import com.example.studylearnspringposts.dto.PostRequestDto;
import com.example.studylearnspringposts.dto.PostResponseDto;
import com.example.studylearnspringposts.domain.user.vo.User;
import com.example.studylearnspringposts.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 레거시 컨트롤러 (하위 호환성을 위해 유지)
 * 새로운 API는 ApiGatewayController를 사용하세요
 */
@RestController
public class MainController {
    
    private final ApiGatewayController apiGatewayController;
    private final UserService userService;

    public MainController(ApiGatewayController apiGatewayController, UserService userService) {
        this.apiGatewayController = apiGatewayController;
        this.userService = userService;
    }

    @GetMapping("/demo")
    public String demo() {
        return "Demo Page - API Gateway Pattern Applied";
    }

    /**
     * @deprecated 새로운 API 엔드포인트 /api/v1/posts 를 사용하세요
     */
    @Deprecated
    @GetMapping("/board")
    public List<PostResponseDto> getAllPosts() {
        return apiGatewayController.getAllPosts();
    }

    /**
     * @deprecated 새로운 API 엔드포인트 /api/v1/posts/{id} 를 사용하세요
     */
    @Deprecated
    @GetMapping("/board/{id}")
    public PostResponseDto getPostById(@PathVariable Long id) {
        return apiGatewayController.getPostById(id);
    }

    /**
     * @deprecated 새로운 API 엔드포인트 /api/v1/posts 를 사용하세요
     */
    @Deprecated
    @PostMapping("/board")
    public PostResponseDto createPost(@RequestBody PostRequestDto dto) {
        return apiGatewayController.createPost(dto);
    }
}
