# backend/api

Spring Boot API 서버의 인증/인가 검증 결과를 기준으로 API 계약을 고정한 문서입니다.

- 인증: OAuth2 Resource Server (JWT)
- 토큰 검증: Keycloak issuer-uri + JWK
- 역할(Role): `ROLE_STUDENT`, `ROLE_ADMIN`
- 범위(Scope): 예시로 `SCOPE_api.read`

## 1) 전체 엔드포인트 목록

| Method | Path | 설명 | 인증 필요 | 접근 권한 | 적용 근거 |
|---|---|---|---|---|---|
| GET | `/api/me` | 현재 인증 주체/권한 확인 | Y | 인증 사용자 | `SecurityConfig` (`anyRequest().authenticated()`) |
| GET | `/api/student/hello` | 학생/관리자 접근 테스트 | Y | `ROLE_STUDENT` or `ROLE_ADMIN` | `SecurityConfig` + `@PreAuthorize` |
| GET | `/api/admin/hello` | 관리자 접근 테스트 | Y | `ROLE_ADMIN` | `SecurityConfig` + `@PreAuthorize` |
| GET | `/api/scope/read` | scope 접근 테스트 | Y | `SCOPE_api.read` | `SecurityConfig` + `@PreAuthorize` |
| GET | `/api/student/courses` | 강의 목록 조회 | Y | `ROLE_STUDENT` or `ROLE_ADMIN` | `SecurityConfig` + `@PreAuthorize` |
| GET | `/api/student/enrollments` | 내 수강 신청 조회 | Y | `ROLE_STUDENT` or `ROLE_ADMIN` | `SecurityConfig` + `@PreAuthorize` |
| POST | `/api/admin/courses` | 강의 생성 | Y | `ROLE_ADMIN` | `SecurityConfig` + `@PreAuthorize` |
| PUT | `/api/admin/courses/{courseId}` | 강의 수정 | Y | `ROLE_ADMIN` | `SecurityConfig` + `@PreAuthorize` |
| DELETE | `/api/admin/courses/{courseId}` | 강의 삭제 | Y | `ROLE_ADMIN` | `SecurityConfig` + `@PreAuthorize` |

## 2) 권한 매트릭스

| Endpoint | ADMIN | STUDENT | 미인증 |
|---|---|---|---|
| GET `/api/me` | 200 | 200 | 401 |
| GET `/api/student/hello` | 200 | 200 | 401 |
| GET `/api/admin/hello` | 200 | 403 | 401 |
| GET `/api/scope/read` | 200 또는 403 (`SCOPE_api.read` 보유 여부) | 200 또는 403 (`SCOPE_api.read` 보유 여부) | 401 |
| GET `/api/student/courses` | 200 | 200 | 401 |
| GET `/api/student/enrollments` | 200 | 200 | 401 |
| POST `/api/admin/courses` | 201 | 403 | 401 |
| PUT `/api/admin/courses/{courseId}` | 200 | 403 | 401 |
| DELETE `/api/admin/courses/{courseId}` | 204 | 403 | 401 |

## 3) 요청/응답 JSON 예시

### 3.1 GET `/api/me`

응답 예시:

```json
{
  "name": "student1",
  "authorities": [
    { "authority": "ROLE_STUDENT" },
    { "authority": "SCOPE_profile" }
  ]
}
```

### 3.2 GET `/api/student/courses`

응답 예시:

```json
[
  {
    "courseId": 2001,
    "courseCode": "COSS-JAVA-101",
    "title": "Java Basics",
    "instructorName": "Kim",
    "capacity": 40,
    "enrolledCount": 12
  }
]
```

### 3.3 GET `/api/student/enrollments`

응답 예시:

```json
[
  {
    "enrollmentId": 9001,
    "courseId": 2001,
    "courseCode": "COSS-JAVA-101",
    "courseTitle": "Java Basics",
    "status": "ENROLLED",
    "enrolledAt": "2026-02-07T02:00:00Z"
  }
]
```

### 3.4 POST `/api/admin/courses`

요청 예시:

```json
{
  "courseCode": "COSS-SEC-301",
  "title": "Cloud Security",
  "instructorName": "Park",
  "capacity": 30
}
```

응답 예시 (201):

```json
{
  "courseId": 2003,
  "courseCode": "COSS-SEC-301",
  "title": "Cloud Security",
  "instructorName": "Park",
  "capacity": 30,
  "enrolledCount": 0
}
```

### 3.5 PUT `/api/admin/courses/{courseId}`

요청 예시:

```json
{
  "courseCode": "COSS-SEC-301",
  "title": "Cloud Security (Updated)",
  "instructorName": "Park",
  "capacity": 35
}
```

응답 예시 (200):

```json
{
  "courseId": 2003,
  "courseCode": "COSS-SEC-301",
  "title": "Cloud Security (Updated)",
  "instructorName": "Park",
  "capacity": 35,
  "enrolledCount": 0
}
```

### 3.6 DELETE `/api/admin/courses/{courseId}`

- 요청 바디 없음
- 응답: `204 No Content`

### 3.7 에러 응답 예시

401 Unauthorized:

```json
{
  "timestamp": "2026-02-07T09:00:00Z",
  "status": 401,
  "error": "Unauthorized",
  "path": "/api/student/courses"
}
```

403 Forbidden:

```json
{
  "timestamp": "2026-02-07T09:01:00Z",
  "status": 403,
  "error": "Forbidden",
  "path": "/api/admin/courses"
}
```

404 Not Found:

```json
{
  "timestamp": "2026-02-07T09:02:00Z",
  "status": 404,
  "error": "Not Found",
  "message": "course not found",
  "path": "/api/admin/courses/9999"
}
```

## 4) Keycloak 토큰 발급 curl 예시

아래 예시는 테스트를 위한 토큰 획득 절차이며, API 서버는 발급에 관여하지 않고 검증만 수행합니다.

### 4.1 Service Account 토큰 (client_credentials)

```bash
curl -X POST "http://localhost:8080/realms/coss/protocol/openid-connect/token" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "grant_type=client_credentials" \
  -d "client_id=coss-api" \
  -d "client_secret=<COSS_API_CLIENT_SECRET>"
```

### 4.2 액세스 토큰으로 API 호출

```bash
curl -X GET "http://localhost:8081/api/student/courses" \
  -H "Authorization: Bearer <ACCESS_TOKEN>"
```

## 5) SecurityConfig + @PreAuthorize를 함께 쓰는 이유

두 계층을 함께 사용하는 이유는 역할이 다르기 때문입니다.

1. `SecurityConfig` (URL 경로 기반 1차 차단)
- `/api/admin/**`, `/api/student/**` 같은 큰 범위를 빠르게 차단합니다.
- 잘못된 접근을 필터 체인 단계에서 조기에 막아 성능/안전성에 유리합니다.

2. `@PreAuthorize` (메서드 단위 2차 정책)
- 컨트롤러/서비스 단위에서 더 세밀한 정책을 명시합니다.
- 예: 같은 `/api/student/**`라도 특정 메서드에 scope 조건 추가 가능.

3. 결과
- 경로 정책 + 비즈니스 정책을 중첩 적용해 정책 누락 가능성을 줄입니다.
- 운영 중 API가 늘어나도 보안 규칙을 계층적으로 유지할 수 있습니다.

## 6) 참고

- OpenAPI 문서: `backend/api/openapi.yaml`
- 보안 설정 코드: `backend/api/src/main/java/com/cloudsquare/coss/api/config/SecurityConfig.java`
- 학생 API: `backend/api/src/main/java/com/cloudsquare/coss/api/course/controller/StudentCourseController.java`
- 관리자 API: `backend/api/src/main/java/com/cloudsquare/coss/api/course/controller/AdminCourseController.java`
- 데모 API: `backend/api/src/main/java/com/cloudsquare/coss/api/web/DemoProtectedController.java`
