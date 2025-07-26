package com.example.studylearnspringposts.service;

import com.example.studylearnspringposts.domain.user.vo.User;
import com.example.studylearnspringposts.dto.UserRequestDto;
import com.example.studylearnspringposts.dto.UserResponseDto;
import com.example.studylearnspringposts.repository.MyRepository;
import com.example.studylearnspringposts.util.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final MyRepository myRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtUtil jwtUtil;

    public UserService(MyRepository myRepository, BCryptPasswordEncoder bCryptPasswordEncoder, JwtUtil jwtUtil) {
        this.myRepository = myRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public void signup(UserRequestDto userRequestDto) {
        // 회원이존재하는지 검증
        boolean isExist = myRepository.existsByUsername(userRequestDto.getUsername());

        if (isExist) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미존재하는 사용자입니다.");
        }

        User newUser = new User();
        newUser.setUsername(userRequestDto.getUsername());
        newUser.setPassword(bCryptPasswordEncoder.encode(userRequestDto.getPassword()));
        newUser.setRole("ROLE_USER");

        myRepository.save(newUser);
    }

    public List<User> getAllUsers() {
        return myRepository.findAll();
    }

    public Optional<User> getUserById(Long id) {
        return myRepository.findById(id);
    }

    // 기존 로그인 메서드 (하위 호환성을 위해 유지)
    public User login(String username, String password) {
        return myRepository.findByUsernameAndPassword(username, password);
    }
    
    // JWT 로그인
    public UserResponseDto loginWithJwt(String username, String password) {
        // 사용자 조회
        Optional<User> userOptional = myRepository.findByUsername(username);
        
        if (userOptional.isEmpty()) {
            throw new IllegalArgumentException("존재하지 않는 사용자입니다");
        }
        
        User user = userOptional.get();
        
        // 비밀번호 검증
        if (!bCryptPasswordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다");
        }
        
        // JWT 토큰 생성
        String token = jwtUtil.generateToken(user.getUsername(), user.getRole());
        
        return UserResponseDto.successWithToken(
            user, 
            token, 
            "로그인이 성공적으로 완료되었습니다"
        );
    }
}
