package com.example.studylearnspringposts.repository;

import com.example.studylearnspringposts.domain.post.vo.Post;
import org.springframework.data.jpa.repository.JpaRepository;


public interface PostRepository extends JpaRepository<Post, Long> {


}

