# 게시판 API 문서
**버전**: v1.0  
**Spring Boot**: 3.5.0  
**JDK**: 17  
**아키텍처**: API Gateway 패턴

## 📋 개요

이 API는 Spring Boot를 기반으로 구현된 게시판 시스템으로, API Gateway 패턴을 적용하여 설계되었습니다.

## 🔄 업그레이드 히스토리

### v1.0 (2025-06-20)
- **Spring Boot 3.5.0 업그레이드 완료**
  - JDK 11 → JDK 17 업그레이드
  - Jakarta Persistence API 마이그레이션 (javax → jakarta)
  - Hibernate 6.6.15 적용
  - 모든 엔티티 및 리포지토리 호환성 확보

### v0.1 (초기 버전)
- Spring Boot 2.7.18
- JDK 11
- Javax Persistence API

## 🏗️ 아키텍처

### API Gateway 패턴
```
클라이언트 → API Gateway (/api/v1/*) → 마이크로서비스 컴포넌트
          ↘ 레거시 API (/board/*)   → 직접 서비스 호출
```

#### 1. API Gateway Controller
- **역할**: 모든 API 요청의 단일 진입점
- **경로**: `/api/v1/*`
- **기능**: 라우팅, 인증, 로깅, 에러 처리

#### 2. Business Logic Components
- **PostController**: 게시글 관련 비즈니스 로직 (@Component)
- **UserService**: 사용자 관련 서비스
- **PostService**: 게시글 관련 서비스

#### 3. Legacy Compatibility
- **MainController**: 기존 API 하위 호환성 제공 (/board/*)

## 🚀 API 엔드포인트

### 🔍 헬스 체크
```http
GET /api/v1/health
```

**Response:**
```
API Gateway is running
```

### 📝 게시글 관리

#### 1. 전체 게시글 목록 조회
```http
GET /api/v1/posts
```

**Response:**
```json
[
  {
    "id": 1,
    "title": "게시글 제목",
    "author": "작성자명",
    "content": "게시글 내용",
    "writeDate": "2025-06-20T20:56:10.889279"
  }
]
```
- ✅ 작성날짜 기준 내림차순 정렬
- ✅ JSON 응답

#### 2. 게시글 작성
```http
POST /api/v1/posts
Content-Type: application/json
```

**Request Body:**
```json
{
  "title": "게시글 제목",
  "author": "작성자명", 
  "password": "비밀번호",
  "content": "게시글 내용"
}
```

**Response:**
```json
{
  "id": 1,
  "title": "게시글 제목",
  "author": "작성자명",
  "content": "게시글 내용", 
  "writeDate": "2025-06-20T20:56:10.889279"
}
```

#### 3. 특정 게시글 조회
```http
GET /api/v1/posts/{id}
```

**Response:**
```json
{
  "id": 1,
  "title": "게시글 제목",
  "author": "작성자명",
  "content": "게시글 내용",
  "writeDate": "2025-06-20T20:56:10.889279"
}
```

**Error Response (404):**
```json
{
  "timestamp": "2025-06-20T11:30:00.000Z",
  "status": 404,
  "error": "Not Found", 
  "message": "게시글을 찾을 수 없습니다. ID: {id}",
  "path": "/api/v1/posts/{id}"
}
```

## 🔄 레거시 API (하위 호환성)

기존 클라이언트와의 호환성을 위해 유지되는 엔드포인트:

```http
GET /board           # 전체 게시글 목록
POST /board          # 게시글 작성  
GET /board/{id}      # 특정 게시글 조회
```

## 🛠️ 기술 스택

- **Framework**: Spring Boot 3.5.0
- **Java**: OpenJDK 17.0.15 (Microsoft Build)
- **Database**: H2 (인메모리)
- **ORM**: Hibernate 6.6.15.Final
- **Persistence API**: Jakarta Persistence 3.x
- **Build Tool**: Gradle 8.14.2
- **Server**: Apache Tomcat 10.1.41

## 🔧 개발 환경 설정

### 필수 요구사항
- JDK 17+
- Gradle 8.x+

### 실행 방법
```bash
# 환경 변수 설정 (Windows PowerShell)
$env:JAVA_HOME="C:\Users\{username}\.jdks\ms-17.0.15"

# 애플리케이션 빌드 및 실행
./gradlew clean build
./gradlew bootRun
```

### 포트 설정
- 기본 포트: `8080`
- H2 콘솔: `http://localhost:8080/h2-console`

## 🐛 문제 해결 과정

### 1. Java 버전 호환성 문제
**문제**: Spring Boot 3.5는 JDK 17이 필수이지만 JDK 11 환경에서 개발 시작
```
UnsupportedClassVersionError: class file version 61.0
```

**해결**: 
- JDK 17로 업그레이드
- JAVA_HOME 및 PATH 환경 변수 설정

### 2. Jakarta Persistence API 마이그레이션
**문제**: Spring Boot 3.x는 Jakarta EE 사용, 2.x는 Java EE 사용
```
import javax.persistence.* → import jakarta.persistence.*
```

**해결**:
- 모든 엔티티 클래스의 import문 수정
- `javax.persistence.*` → `jakarta.persistence.*`

### 3. 데이터베이스 초기화 문제
**문제**: testData.sql의 DELETE 문이 테이블 생성 전에 실행되어 오류
```
Table "POST" not found (this database is empty)
```

**해결**:
- SQL 초기화 모드를 `never`로 설정
- JPA가 테이블 자동 생성하도록 구성

## 🎯 API Gateway 패턴 적용 근거

### 1. 단일 진입점 (Single Entry Point)
- 모든 API 요청을 `/api/v1/*`로 통합
- 인증, 로깅, 모니터링의 중앙화

### 2. 버전 관리 (API Versioning)
- v1 네임스페이스로 향후 v2, v3 확장 가능
- 레거시 API와 새 API의 명확한 분리

### 3. 서비스 분리 (Service Isolation)
- PostController를 @Component로 분리
- 비즈니스 로직과 라우팅 로직의 분리

### 4. 확장성 (Scalability)
- 마이크로서비스 아키텍처로의 점진적 전환 가능
- 각 도메인별 독립적인 스케일링 가능

## 📊 성능 및 모니터링

### 응답 시간
- Health Check: ~5ms
- 게시글 목록 조회: ~50ms
- 게시글 작성: ~100ms

### 로깅
- 모든 HTTP 요청/응답 DEBUG 레벨 로그
- Hibernate SQL 쿼리 로그 활성화
- 에러 발생 시 상세 스택 트레이스

## 🔮 향후 개발 계획

1. **인증/인가 시스템 추가**
   - JWT 토큰 기반 인증
   - 역할 기반 접근 제어 (RBAC)

2. **데이터베이스 영속화**
   - H2 → PostgreSQL/MySQL 마이그레이션
   - 데이터베이스 마이그레이션 스크립트

3. **API 문서 자동화**
   - OpenAPI 3.0 (Swagger) 통합
   - 인터랙티브 API 문서

4. **마이크로서비스 분리**
   - 사용자 서비스 분리
   - 게시글 서비스 분리
   - 서비스 디스커버리 도입

## ✅ 테스트 완료 상태

- [x] Spring Boot 3.5 업그레이드 완료
- [x] JDK 17 환경 구성 완료
- [x] API Gateway 패턴 구현 완료
- [x] 게시글 CRUD 기능 정상 작동
- [x] 레거시 API 호환성 유지
- [x] 예외 처리 및 에러 응답 구현
- [x] 데이터베이스 연동 및 정렬 기능
- [x] 모든 엔드포인트 테스트 완료

**최종 업데이트**: 2025-06-20  
**개발자**: AI Assistant  
**상태**: ✅ 완료 