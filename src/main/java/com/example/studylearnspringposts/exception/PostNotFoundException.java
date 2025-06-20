package com.example.studylearnspringposts.exception;

public class PostNotFoundException extends RuntimeException {
    public PostNotFoundException(Long id) {
        super("게시글을 찾을 수 없습니다. ID: " + id);
    }
    
    public PostNotFoundException(String message) {
        super(message);
    }
} 