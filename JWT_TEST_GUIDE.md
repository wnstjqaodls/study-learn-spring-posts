# JWT 로그인 기능 테스트 가이드

## 🚀 구현 완료된 기능

### 1. JWT 토큰 생성 및 검증 (JwtUtil)
- 토큰 생성: `generateToken(username, role)`
- 토큰 검증: `validateToken(token, username)`
- 사용자명/역할 추출 기능

### 2. 로그인 API 엔드포인트
- **URL**: `POST /api/v1/auth/login`
- **기능**: 사용자명/비밀번호 검증 후 JWT 토큰 발급
- **응답**: Authorization 헤더에 Bearer 토큰 포함

### 3. 회원가입 API 엔드포인트  
- **URL**: `POST /api/v1/auth/signup`
- **기능**: 새 사용자 등록 (비밀번호 자동 암호화)

## 🧪 테스트 방법

### 1. 애플리케이션 실행
```bash
./gradlew bootRun
```

### 2. 테스트용 사용자 계정
- **사용자명**: `testuser` / **비밀번호**: `password123` / **역할**: `ROLE_USER`
- **사용자명**: `admin` / **비밀번호**: `admin123` / **역할**: `ROLE_ADMIN`

### 3. 로그인 테스트 (curl)
```bash
# 일반 사용자 로그인
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "password123"
  }'

# 관리자 로그인  
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin", 
    "password": "admin123"
  }'
```

### 4. 회원가입 테스트
```bash
curl -X POST http://localhost:8080/api/v1/auth/signup \
  -H "Content-Type: application/json" \
  -d '{
    "username": "newuser",
    "password": "newpass123"
  }'
```

### 5. 성공 응답 예시
```json
{
  "id": 1,
  "username": "testuser", 
  "role": "ROLE_USER",
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "message": "로그인이 성공적으로 완료되었습니다"
}
```

## 📝 주요 구현 특징

1. **보안**: BCrypt로 비밀번호 암호화
2. **토큰**: JWT 24시간 유효기간  
3. **응답**: Authorization 헤더와 Response Body 모두에 토큰 포함
4. **검증**: 로그인 시 사용자 존재 여부 및 비밀번호 일치 확인

## 🛠️ 다음 단계 (선택사항)

1. JWT 인증 필터 추가로 API 보호
2. 토큰 갱신(Refresh Token) 기능
3. 로그아웃 기능 (토큰 블랙리스트)
4. 권한별 접근 제어

---
**참고**: IntelliJ에서 JDK 17로 설정되어 있다면 정상 동작합니다! 