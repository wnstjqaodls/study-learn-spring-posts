package com.example.studylearnspringposts.dto;

import com.example.studylearnspringposts.domain.user.vo.User;
import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Builder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponseDto {
    private Long id;
    private String username;
    private String role;
    private String message;
    private String token;
    
    public static UserResponseDto fromEntity(User user) {
        return UserResponseDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .role(user.getRole())
                .build();
    }
    
    public static UserResponseDto success(String message) {
        return UserResponseDto.builder()
                .message(message)
                .build();
    }
    
    public static UserResponseDto successWithToken(User user, String token, String message) {
        return UserResponseDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .role(user.getRole())
                .token(token)
                .message(message)
                .build();
    }
} 