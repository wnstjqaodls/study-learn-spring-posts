package com.example.studylearnspringposts.service;

import com.example.studylearnspringposts.domain.post.vo.Post;
import com.example.studylearnspringposts.repository.PostRepository;
import org.springframework.stereotype.Service;
import java.util.List;
@Service
public class PostService {
        private final PostRepository postRepository;

        public PostService(PostRepository postRepository) {
            this.postRepository = postRepository;
        }

        public List<Post> getAllPosts() {
            return postRepository.findAll();
        }

        public Post getPostById(Long id) {
            return postRepository.findById(id).orElse(null);
        }
}
