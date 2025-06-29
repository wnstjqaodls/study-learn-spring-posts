package com.example.studylearnspringposts.repository;

import com.example.studylearnspringposts.domain.post.vo.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface PostRepository extends JpaRepository<Post, Long> {

        List<Post> findByTitle(String title);

        List<Post> findAllByTitleContaining(String title);

        Optional<Post> findAllById(Long id);

        List<Post> findAllByOrderByWriteDateDesc();

        @Override
        void deleteById (Long id);
}

