# Cloudsquare COSS Demo

목표: 인프라 배포 시연에서 **프론트 UI + Keycloak 로그인/회원가입 + API 호출**까지 최소 기능을 빠르게 재현한다.

## 1. 구성

- Keycloak (issuer): `http://localhost:8080`
- API (Spring Boot Resource Server): `http://localhost:8081`
- Frontend (Vite React): `http://localhost:5173`

## 2. Keycloak 설정 가이드

### 2.1 Realm 설정

1. Realm `coss` 선택
2. `Realm settings -> Login`에서 `User registration` 활성화

이 설정으로 프론트의 "회원가입" 버튼이 Keycloak 기본 Registration 화면으로 이동한다.

### 2.2 `coss-frontend` 클라이언트 생성 (신규)

다음 값으로 생성한다.

- Client type: `OpenID Connect`
- Client ID: `coss-frontend`
- Client authentication: `Off` (public client)
- Standard flow: `On` (Authorization Code)
- Direct access grants: `Off` 권장
- PKCE: `S256` 사용

### 2.3 Redirect / Logout / Web origins

`coss-frontend`의 URL 관련 항목은 환경변수 기준으로 관리한다.

로컬 예시:

- Valid redirect URIs:
  - `http://localhost:5173/*`
- Valid post logout redirect URIs:
  - `http://localhost:5173/*`
- Web origins:
  - `http://localhost:5173`

배포 예시:

- Valid redirect URIs:
  - `https://<FRONTEND_PUBLIC_URL>/*`
- Valid post logout redirect URIs:
  - `https://<FRONTEND_PUBLIC_URL>/*`
- Web origins:
  - `https://<FRONTEND_PUBLIC_URL>`

주의: Keycloak이 private subnet에 있어도 브라우저에서 접근 가능한 **issuer public URL**로 설정해야 한다.

## 3. 환경변수

### 3.1 Frontend (`frontend/.env`)

`frontend/.env.example` 참고:

- `VITE_API_BASE_URL`: API base URL
- `VITE_KEYCLOAK_URL`: Keycloak 공개 URL
- `VITE_KEYCLOAK_REALM`: `coss`
- `VITE_KEYCLOAK_CLIENT_ID`: `coss-frontend`
- `VITE_KEYCLOAK_REGISTER_URL`: Keycloak registration endpoint

### 3.2 API (`backend/api/.env`)

`backend/api/.env.example` 참고:

- `DB_USERNAME`, `DB_PASSWORD`
- `ISSUER_URI` (예: `http://localhost:8080/realms/coss`)
- `CORS_ALLOWED_ORIGINS` (예: `http://localhost:5173`)

## 4. 로컬 실행

### 4.1 개별 실행

1. API DB(MySQL) 준비
2. Keycloak 실행 + Realm/Client 설정
3. API 실행

```bash
cd backend/api
./gradlew bootRun
```

4. Frontend 실행

```bash
cd frontend
npm install
npm run dev
```

### 4.2 Docker Compose 실행

```bash
docker compose -f docker-compose.local.yml up --build
```

포트:
- Frontend: `5173`
- API: `8081`
- Keycloak: `8080`

## 5. 브라우저 시연 스크립트

### 시나리오 A: 학생(STUDENT)

1. `http://localhost:5173` 접속
2. 로그인 버튼 클릭 (Keycloak 로그인)
3. Student 계정 로그인
4. `StudentPage` 이동
5. `강의 목록 새로고침` 클릭 -> `/api/student/courses` 성공 확인
6. `관리자 API 403 확인` 클릭 -> `/api/admin/courses` 403 확인

### 시나리오 B: 관리자(ADMIN)

1. 로그아웃 후 관리자 계정으로 로그인
2. `AdminPage` 이동
3. 강의 생성 폼 입력 후 `강의 생성`
4. 생성 응답(JSON) 확인
5. `StudentPage`에서 목록 새로고침 후 방금 생성한 강의 확인

## 6. 현재 구현 범위

- 프론트 페이지 3개:
  - Home(로그인 상태 + `/api/me`)
  - StudentPage(강의 목록)
  - AdminPage(강의 생성)
- 로그인/로그아웃/회원가입 버튼
- Access Token 자동 첨부 (`Authorization: Bearer ...`)
- 토큰 갱신(`updateToken(30)`) 주기 처리

## 7. 참고 파일

- `frontend/src/auth.js`
- `frontend/src/api.js`
- `frontend/src/pages/Home.jsx`
- `frontend/src/pages/StudentPage.jsx`
- `frontend/src/pages/AdminPage.jsx`
- `backend/api/src/main/java/com/cloudsquare/coss/api/config/SecurityConfig.java`
- `backend/api/src/main/resources/application.yaml`
- `docker-compose.local.yml`
