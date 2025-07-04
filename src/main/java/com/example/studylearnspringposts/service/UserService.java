package com.example.studylearnspringposts.service;

import com.example.studylearnspringposts.domain.user.vo.User;
import com.example.studylearnspringposts.dto.UserRequestDto;
import com.example.studylearnspringposts.dto.UserResponseDto;
import com.example.studylearnspringposts.exception.GlobalExceptionHandler;
import com.example.studylearnspringposts.repository.MyRepository;
import com.example.studylearnspringposts.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final MyRepository myRepository;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    // 세터 주입 활용해봄
    public UserService(MyRepository myRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.myRepository = myRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    public void signup (UserRequestDto userRequestDto) {

        // 회원이존재하는지 검증
        // DB에 중복된 username이 없다면 회원을 저장하고 Client 로 성공했다는 메시지, 상태코드 반환하기

        boolean isExist = myRepository.existsByUsername(userRequestDto.getUsername());

        if (isExist) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미존재하는 사용자입니다.");
        }

        User newUser = new User();

        newUser.setUsername(userRequestDto.getUsername());
//        newUser.setPassword(userRequestDto.getPassword()); // 그대로 저장하면 위험!
        newUser.setPassword(bCryptPasswordEncoder.encode(userRequestDto.getPassword()));
        newUser.setRole("ROLE_USER");

        myRepository.save(newUser);


    }



   /* @Autowired
    private MyRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtUtil jwtUtil;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    // 기존 로그인 메서드 (하위 호환성을 위해 유지)
    public User login(String username, String password) {
        return userRepository.findByUsernameAndPassword(username, password);
    }
    
    // 회원가입
    public UserResponseDto signup(UserRequestDto userRequestDto) {
        // 사용자명 중복 확인
        if (userRepository.existsByUsername(userRequestDto.getUsername())) {
            throw new IllegalArgumentException("이미 존재하는 사용자명입니다");
        }
        
        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(userRequestDto.getPassword());
        
        // 사용자 생성
        User user = new User(
            userRequestDto.getUsername(),
            encodedPassword,
            userRequestDto.getRole()
        );
        
        User savedUser = userRepository.save(user);
        
        return UserResponseDto.builder()
                .id(savedUser.getId())
                .username(savedUser.getUsername())
                .role(savedUser.getRole())
                .message("회원가입이 성공적으로 완료되었습니다")
                .build();
    }
    
    // JWT 로그인
    public UserResponseDto loginWithJwt(String username, String password) {
        // 사용자 조회
        Optional<User> userOptional = userRepository.findByUsername(username);
        
        if (userOptional.isEmpty()) {
            throw new IllegalArgumentException("존재하지 않는 사용자입니다");
        }
        
        User user = userOptional.get();
        
        // 비밀번호 검증
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다");
        }
        
        // JWT 토큰 생성
        String token = jwtUtil.generateToken(user.getUsername(), user.getRole());
        
        return UserResponseDto.successWithToken(
            user, 
            token, 
            "로그인이 성공적으로 완료되었습니다"
        );
    }*/
}
