# 게시판 API 문서 - API Gateway Pattern

## 프로젝트 개요
Spring Boot를 사용하여 API 게이트웨이 패턴으로 구현된 게시판 시스템입니다.

## 아키텍처 설계

### API 게이트웨이 패턴 적용
- **ApiGatewayController**: 클라이언트의 모든 요청을 받아서 적절한 마이크로서비스로 라우팅
- **PostController**: 게시글 관련 비즈니스 로직을 처리하는 마이크로서비스 컨트롤러
- **MainController**: 하위 호환성을 위한 레거시 API 엔드포인트 지원

### 기술 스택
- **Spring Boot**: 2.7.18
- **JPA/Hibernate**: 데이터베이스 ORM
- **H2 Database**: 인메모리 데이터베이스
- **Java**: 11

## API 엔드포인트

### 1. API Gateway (권장)

#### 게이트웨이 상태 확인
```http
GET /api/v1/health
```
**응답:**
```json
"API Gateway is running"
```

#### 전체 게시글 목록 조회
```http
GET /api/v1/posts
```
**기능:**
- 제목, 작성자명, 작성 내용, 작성 날짜를 조회
- 작성 날짜 기준 내림차순으로 정렬

**응답 예시:**
```json
[
  {
    "id": 3,
    "title": "API Gateway Test",
    "author": "Backend Engineer",
    "content": "Testing the microservice architecture with API Gateway pattern in Spring Boot.",
    "writeDate": "2025-06-20T20:18:31.656923"
  },
  {
    "id": 2,
    "title": "Second Post", 
    "author": "Developer",
    "content": "This is the second test post to verify the API Gateway pattern implementation.",
    "writeDate": "2025-06-20T20:17:53.572145"
  }
]
```

#### 게시글 작성
```http
POST /api/v1/posts
Content-Type: application/json
```
**요청 본문:**
```json
{
  "title": "게시글 제목",
  "author": "작성자명",
  "password": "비밀번호",
  "content": "게시글 내용"
}
```

**응답:**
```json
{
  "id": 1,
  "title": "게시글 제목",
  "author": "작성자명",
  "content": "게시글 내용",
  "writeDate": "2025-06-20T20:16:43.73213"
}
```

#### 선택한 게시글 조회
```http
GET /api/v1/posts/{id}
```
**기능:**
- 선택한 게시글의 제목, 작성자명, 작성 날짜, 작성 내용을 조회

**응답 예시:**
```json
{
  "id": 1,
  "title": "Test Post",
  "author": "Tester",
  "content": "This is a test post created via API Gateway pattern!",
  "writeDate": "2025-06-20T20:16:43.73213"
}
```

### 2. Legacy API (하위 호환성)

#### 전체 게시글 목록 조회 (deprecated)
```http
GET /board
```

#### 특정 게시글 조회 (deprecated)
```http
GET /board/{id}
```

#### 게시글 작성 (deprecated)
```http
POST /board
```

> **참고:** Legacy API는 하위 호환성을 위해 제공되며, 새로운 개발에서는 `/api/v1/` 엔드포인트 사용을 권장합니다.

## 에러 처리

### 404 Not Found
존재하지 않는 게시글을 조회할 때:
```json
{
  "timestamp": "2025-06-20T11:20:09.292+00:00",
  "status": 404,
  "error": "Not Found",
  "message": "게시글을 찾을 수 없습니다. ID: 999",
  "path": "/api/v1/posts"
}
```

## 데이터베이스 스키마

### POST 테이블
```sql
CREATE TABLE post (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(255) NOT NULL,
    author VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    write_date TIMESTAMP NOT NULL,
    created_date TIMESTAMP,
    updated_date TIMESTAMP
);
```

## 실행 방법

1. **애플리케이션 실행:**
   ```bash
   ./gradlew bootRun
   ```

2. **H2 콘솔 접속:**
   - URL: http://localhost:8080/h2-console
   - JDBC URL: jdbc:h2:mem:db
   - Username: sa
   - Password: (빈 값)

3. **API 테스트:**
   ```bash
   # 상태 확인
   curl -X GET http://localhost:8080/api/v1/health
   
   # 전체 게시글 조회
   curl -X GET http://localhost:8080/api/v1/posts
   
   # 게시글 작성
   curl -X POST http://localhost:8080/api/v1/posts \
     -H "Content-Type: application/json" \
     -d '{"title":"테스트","author":"작성자","password":"1234","content":"내용"}'
   ```

## API Gateway 패턴의 장점

1. **단일 진입점**: 모든 클라이언트 요청이 하나의 게이트웨이를 통해 처리
2. **서비스 추상화**: 내부 마이크로서비스 구조를 클라이언트로부터 숨김
3. **하위 호환성**: 기존 API를 유지하면서 새로운 API 제공
4. **중앙집중식 관리**: 인증, 로깅, 모니터링 등을 게이트웨이에서 일괄 처리 가능
5. **확장성**: 새로운 마이크로서비스 추가 시 게이트웨이만 수정하면 됨 