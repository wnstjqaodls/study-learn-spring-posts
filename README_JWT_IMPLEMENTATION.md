# JWT 인증 시스템 구현 가이드

## 📋 구현된 기능

### 1. 회원가입 API (`POST /api/v1/auth/signup`)
- **유효성 검사**:
  - username: 4~10자, 소문자(a-z) + 숫자(0-9)만 허용
  - password: 8~15자, 대소문자 + 숫자 + 특수문자 모두 포함
- **중복 체크**: DB에서 username 중복 여부 확인
- **암호화**: BCrypt를 사용한 비밀번호 해싱
- **권한 설정**: USER 또는 ADMIN 역할 부여

### 2. 로그인 API (`POST /api/v1/auth/login`)
- **인증**: username/password 검증
- **JWT 토큰 발급**: 사용자 정보와 역할이 포함된 JWT 생성
- **토큰 반환**: Authorization 헤더에 Bearer 토큰 형태로 반환

## 🔧 기술 스택

### JWT 라이브러리
```gradle
implementation 'io.jsonwebtoken:jjwt-api:0.12.3'
implementation 'io.jsonwebtoken:jjwt-impl:0.12.3'
implementation 'io.jsonwebtoken:jjwt-jackson:0.12.3'
```

### Spring Security
```gradle
implementation 'org.springframework.boot:spring-boot-starter-security'
```

## 🏗️ 아키텍처

### 주요 클래스 구조
```
src/main/java/com/example/studylearnspringposts/
├── controller/
│   └── AuthController.java           # 인증 API 엔드포인트
├── service/
│   └── UserService.java             # 사용자 비즈니스 로직
├── dto/
│   ├── UserRequestDto.java          # 요청 DTO (유효성 검사 포함)
│   └── UserResponseDto.java         # 응답 DTO
├── util/
│   └── JwtUtil.java                 # JWT 토큰 생성/검증 유틸리티
├── security/
│   └── SecurityConfig.java          # Spring Security 설정
└── repository/
    └── MyRepository.java            # 사용자 데이터 접근 계층
```

## 🧪 테스트 시나리오

### 통합테스트 (`AuthControllerIntegrationTest`)

#### 1. 회원가입 테스트
- ✅ **정상 회원가입**: 유효한 데이터로 회원가입 성공
- ❌ **중복 사용자명**: 이미 존재하는 사용자명으로 가입 시도
- ❌ **잘못된 형식**: 유효성 검사 실패 시나리오

#### 2. 로그인 테스트
- ✅ **정상 로그인**: 올바른 credentials로 JWT 토큰 발급
- ❌ **존재하지 않는 사용자**: 미등록 사용자 로그인 시도
- ❌ **잘못된 비밀번호**: 틀린 비밀번호로 로그인 시도

#### 3. 권한 테스트
- ✅ **관리자 가입**: ADMIN 역할로 회원가입 성공

## 🚀 실행 방법

### 1. 환경 요구사항
- Java 17 이상
- Gradle 8.x

### 2. 빌드 및 실행
```bash
# 의존성 새로고침
./gradlew build --refresh-dependencies

# 애플리케이션 실행
./gradlew bootRun

# 테스트 실행
./gradlew test
```

### 3. API 테스트 예시

#### 회원가입
```bash
curl -X POST http://localhost:8080/api/v1/auth/signup \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser1",
    "password": "Password1!",
    "role": "USER"
  }'
```

#### 로그인
```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser1",
    "password": "Password1!"
  }'
```

## 📊 응답 형식

### 성공적인 회원가입 응답
```json
{
  "id": 1,
  "username": "testuser1",
  "role": "USER",
  "message": "회원가입이 성공적으로 완료되었습니다"
}
```

### 성공적인 로그인 응답
```json
{
  "id": 1,
  "username": "testuser1",
  "role": "USER",
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "message": "로그인이 성공적으로 완료되었습니다"
}
```

## 🔒 보안 설정

### JWT 토큰 설정
- **만료시간**: 24시간 (86400000 밀리초)
- **서명 알고리즘**: HS256
- **토큰 위치**: Authorization 헤더 (Bearer 형식)

### 암호화
- **비밀번호**: BCrypt 해싱
- **JWT 서명**: HMAC SHA-256

## 🧪 TDD 접근법

### 1. Red (실패하는 테스트 작성)
```java
@Test
void testSignupSuccess() throws Exception {
    // Given
    UserRequestDto signupRequest = UserRequestDto.builder()
        .username("testuser1")
        .password("Password1!")
        .role("USER")
        .build();

    // When & Then
    mockMvc.perform(post("/api/v1/auth/signup")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(signupRequest)))
    .andExpect(status().isCreated())
    .andExpect(jsonPath("$.username").value("testuser1"));
}
```

### 2. Green (테스트를 통과하는 최소 구현)
```java
@PostMapping("/signup")
public ResponseEntity<UserResponseDto> signup(@RequestBody UserRequestDto request) {
    UserResponseDto response = userService.signup(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
}
```

### 3. Refactor (코드 개선)
- 유효성 검사 추가
- 예외 처리 강화
- 코드 가독성 향상

## 🔄 향후 확장 가능 기능

1. **RefreshToken 구현**: 토큰 갱신 메커니즘
2. **역할 기반 접근 제어**: @PreAuthorize를 통한 메서드 레벨 보안
3. **JWT 필터**: 요청별 토큰 자동 검증
4. **댓글 API 보안**: JWT 토큰을 통한 댓글 CRUD 보호

## 🐛 트러블슈팅

### Java 버전 문제
```
> Dependency requires at least JVM runtime version 17
```
**해결방법**: Java 17 이상 설치 및 JAVA_HOME 설정

### 의존성 문제
```
> Could not resolve org.springframework.security
```
**해결방법**: `./gradlew build --refresh-dependencies` 실행 