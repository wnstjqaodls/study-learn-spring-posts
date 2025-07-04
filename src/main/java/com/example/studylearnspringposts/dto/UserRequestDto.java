package com.example.studylearnspringposts.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
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

    @Pattern(regexp = "^[a-z0-9]{4,10}$", message = "사용자명은 4~10자의 소문자 알파벳과 숫자로만 가능합니다.")
    @NotBlank(message = "사용자명은 필수입니다.")
    private String username;

    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]+$", message = "password 는 최소 8자 이상, 15자 이하이며 알파벳 대소문자(a~z, A~Z), 숫자(0~9), 특수문자`로 구성되어야 합니다.")
    @NotBlank(message = "비밀번호는 필수입니다.")
    private String password;
    
    @Builder.Default
    private String role = "USER"; // 기본값은 USER

    // 요구사항 목록
    //- username, password를 Client에서 전달받기
    //- username은  `최소 4자 이상, 10자 이하이며 알파벳 소문자(a~z), 숫자(0~9)`로 구성되어야 한다.
    //- password는  `최소 8자 이상, 15자 이하이며 알파벳 대소문자(a~z, A~Z), 숫자(0~9), 특수문자`로 구성되어야 한다.

    // 유효성 검사 메서드 > 아래 방식은 비추천 > SpringBoot 표준방식이아니고, Bean Validation 을 사용안함, 유지보수힘듦.
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
