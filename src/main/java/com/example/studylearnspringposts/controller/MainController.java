package com.example.studylearnspringposts.controller;


import com.example.studylearnspringposts.domain.post.vo.Post;
import com.example.studylearnspringposts.service.PostService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class MainController {
    private final PostService postService;

    public MainController (PostService postService) {
        this.postService = postService;
    }

    @GetMapping("/demo")
    public String demo() {
        return "Demo Page";
    }

    // 전체 게시글 조회
    @GetMapping
    @RequestMapping("/posts")
    public List<Post> getAllPosts() {
        return postService.getAllPosts();
    }

    // 게시글 단건 조회
    @GetMapping("/{id}")
    public Post getPostById(@PathVariable Long id) {
        return postService.getPostById(id);
    }


}
