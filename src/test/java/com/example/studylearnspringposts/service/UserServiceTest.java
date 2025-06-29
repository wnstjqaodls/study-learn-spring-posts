package com.example.studylearnspringposts.service;

import com.example.studylearnspringposts.domain.user.vo.User;
import com.example.studylearnspringposts.dto.UserRequestDto;
import com.example.studylearnspringposts.dto.UserResponseDto;
import com.example.studylearnspringposts.repository.MyRepository;
import com.example.studylearnspringposts.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private MyRepository userRepository;
    
    @Mock
    private PasswordEncoder passwordEncoder;
    
    @Mock
    private JwtUtil jwtUtil;
    
    @InjectMocks
    private UserService userService;
    
    private UserRequestDto validUserRequest;
    private User testUser;
    
    @BeforeEach
    void setUp() {
        validUserRequest = UserRequestDto.builder()
                .username("testuser")
                .password("Password1!")
                .role("USER")
                .build();
                
        testUser = new User("testuser", "encodedPassword", "USER");
        testUser.setId(1L);
    }
    
    @Test
    @DisplayName("회원가입 성공 - 유효한 사용자 정보")
    void testSignupSuccess() {
        // Given
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        
        // When
        UserResponseDto response = userService.signup(validUserRequest);
        
        // Then
        assertThat(response).isNotNull();
        assertThat(response.getUsername()).isEqualTo("testuser");
        assertThat(response.getRole()).isEqualTo("USER");
        assertThat(response.getMessage()).isEqualTo("회원가입이 성공적으로 완료되었습니다");
        
        verify(userRepository).existsByUsername("testuser");
        verify(passwordEncoder).encode("Password1!");
        verify(userRepository).save(any(User.class));
    }
    
    @Test
    @DisplayName("회원가입 실패 - 중복된 사용자명")
    void testSignupFailDuplicateUsername() {
        // Given
        when(userRepository.existsByUsername(anyString())).thenReturn(true);
        
        // When & Then
        assertThatThrownBy(() -> userService.signup(validUserRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("이미 존재하는 사용자명입니다");
        
        verify(userRepository).existsByUsername("testuser");
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(User.class));
    }
    
    @Test
    @DisplayName("JWT 로그인 성공")
    void testLoginWithJwtSuccess() {
        // Given
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(jwtUtil.generateToken(anyString(), anyString())).thenReturn("jwt-token");
        
        // When
        UserResponseDto response = userService.loginWithJwt("testuser", "Password1!");
        
        // Then
        assertThat(response).isNotNull();
        assertThat(response.getUsername()).isEqualTo("testuser");
        assertThat(response.getRole()).isEqualTo("USER");
        assertThat(response.getToken()).isEqualTo("jwt-token");
        assertThat(response.getMessage()).isEqualTo("로그인이 성공적으로 완료되었습니다");
        
        verify(userRepository).findByUsername("testuser");
        verify(passwordEncoder).matches("Password1!", "encodedPassword");
        verify(jwtUtil).generateToken("testuser", "USER");
    }
    
    @Test
    @DisplayName("JWT 로그인 실패 - 존재하지 않는 사용자")
    void testLoginWithJwtFailUserNotFound() {
        // Given
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());
        
        // When & Then
        assertThatThrownBy(() -> userService.loginWithJwt("nonexistent", "Password1!"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("존재하지 않는 사용자입니다");
        
        verify(userRepository).findByUsername("nonexistent");
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(jwtUtil, never()).generateToken(anyString(), anyString());
    }
    
    @Test
    @DisplayName("JWT 로그인 실패 - 잘못된 비밀번호")
    void testLoginWithJwtFailWrongPassword() {
        // Given
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);
        
        // When & Then
        assertThatThrownBy(() -> userService.loginWithJwt("testuser", "WrongPassword!"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("비밀번호가 일치하지 않습니다");
        
        verify(userRepository).findByUsername("testuser");
        verify(passwordEncoder).matches("WrongPassword!", "encodedPassword");
        verify(jwtUtil, never()).generateToken(anyString(), anyString());
    }
    
    @Test
    @DisplayName("ADMIN 역할로 회원가입 성공")
    void testSignupWithAdminRole() {
        // Given
        UserRequestDto adminRequest = UserRequestDto.builder()
                .username("admin")
                .password("AdminPass1!")
                .role("ADMIN")
                .build();
                
        User adminUser = new User("admin", "encodedPassword", "ADMIN");
        adminUser.setId(2L);
        
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(adminUser);
        
        // When
        UserResponseDto response = userService.signup(adminRequest);
        
        // Then
        assertThat(response).isNotNull();
        assertThat(response.getUsername()).isEqualTo("admin");
        assertThat(response.getRole()).isEqualTo("ADMIN");
        assertThat(response.getMessage()).isEqualTo("회원가입이 성공적으로 완료되었습니다");
        
        verify(userRepository).existsByUsername("admin");
        verify(passwordEncoder).encode("AdminPass1!");
        verify(userRepository).save(any(User.class));
    }
} 