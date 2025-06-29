package com.example.studylearnspringposts.dto;

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
public class UserRequestDto {
    
    private String username;
    private String password;
    
    @Builder.Default
    private String role = "USER"; // 기본값은 USER
    
    // 유효성 검사 메서드
    public boolean isValidUsername() {
        if (username == null) return false;
        return username.length() >= 4 && username.length() <= 10 && 
               username.matches("^[a-z0-9]+$");
    }
    
    public boolean isValidPassword() {
        if (password == null) return false;
        return password.length() >= 8 && password.length() <= 15 && 
               password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]+$");
    }
} 