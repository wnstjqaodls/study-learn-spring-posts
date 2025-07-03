package com.example.studylearnspringposts.service;

import com.example.studylearnspringposts.domain.post.vo.Post;
import com.example.studylearnspringposts.dto.PostRequestDto;
import com.example.studylearnspringposts.exception.PostNotFoundException;
import com.example.studylearnspringposts.repository.PostRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true) // 읽기 전용 트랜잭션을 기본으로 설정
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
    @Transactional // 쓰기 작업이므로 readOnly = false (기본값)
    public Post createPost(Post post) {
        post.setWriteDate(LocalDateTime.now());
        return postRepository.save(post);
    }

    // 게시글 수정
    @Transactional // 쓰기 작업이므로 readOnly = false (기본값)
    public Post updatePost(Long id, PostRequestDto postRequestDto) {
        // 기존 게시글 조회
        Post existingPost = postRepository.findById(id)
            .orElseThrow(() -> new PostNotFoundException("게시글을 찾을 수 없습니다"));
        
        // 비밀번호 검증
        if (!existingPost.getPassword().equals(postRequestDto.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다");
        }
        
        // 게시글 정보 업데이트
        existingPost.setTitle(postRequestDto.getTitle());
        existingPost.setAuthor(postRequestDto.getAuthor());
        existingPost.setContent(postRequestDto.getContent());
        
        return postRepository.save(existingPost);
    }

    // 게시글 삭제
    @Transactional // 쓰기 작업이므로 readOnly = false (기본값)
    public void deletePost(Long id, PostRequestDto postRequestDto) {
        // 기존 게시글 조회
        Post existingPost = postRepository.findById(id)
            .orElseThrow(() -> new PostNotFoundException("게시글을 찾을 수 없습니다"));

        // 비밀번호 검증
        if (!existingPost.getPassword().equals(postRequestDto.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다");
        }

        postRepository.deleteById(existingPost.getId());
        // 성공적으로 삭제되면 void  (예외가 발생하지 않으면 성공으로 간주)
    }
}
