package com.example.studylearnspringposts.service;

import com.example.studylearnspringposts.domain.post.vo.Post;
import com.example.studylearnspringposts.repository.PostRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PostService {
    private final PostRepository postRepository;

    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    // 전체 게시글 조회 (작성 날짜 기준 내림차순)
    public List<Post> getAllPosts() {
        return postRepository.findAllByOrderByWriteDateDesc();
    }

    // 특정 게시글 조회
    public Optional<Post> getPostById(Long id) {
        return postRepository.findById(id);
    }

    // 게시글 작성
    public Post createPost(Post post) {
        post.setWriteDate(LocalDateTime.now());
        return postRepository.save(post);
    }
}
