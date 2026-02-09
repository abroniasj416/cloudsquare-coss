# Cloudsquare COSS LMS Video

React(Vite) + Spring Boot + Keycloak + MySQL + NCP Object Storage(S3 호환) 기반 LMS 영상 업로드/스트리밍 예제입니다.

## 1. 기능

- 관리자
  - `POST /api/admin/lectures`: 강의 생성
  - `POST /api/admin/lectures/{id}/video/upload-init`: presigned PUT URL 발급
  - `POST /api/admin/lectures/{id}/video/upload-complete`: 업로드 완료 + HEAD 검증 + 썸네일 비동기 처리 시작
- 학생
  - `GET /api/lectures`: 강의 목록
  - `GET /api/lectures/{id}`: 강의 상세
  - `POST /api/lectures/{id}/enroll`: 수강 신청
  - `GET /api/lectures/{id}/playback`: 수강생만 재생 URL 발급

## 2. 환경변수

백엔드(`backend/api/.env`):

- `DB_USERNAME`
- `DB_PASSWORD`
- `ISSUER_URI`
- `CORS_ALLOWED_ORIGINS`
- `NCP_ACCESS_KEY`
- `NCP_SECRET_KEY`
- `NCP_S3_ENDPOINT`
- `NCP_OBJECT_STORAGE_BUCKET`
- `NCP_VIDEO_PREFIX`
- `CDN_BASE_URL`
- `PRESIGNED_PUT_EXPIRES_SECONDS`
- `PRESIGNED_GET_EXPIRES_SECONDS`
- `FFMPEG_PATH`

프론트(`frontend/.env`):

- `VITE_API_BASE_URL`
- `VITE_KEYCLOAK_URL`
- `VITE_KEYCLOAK_REALM`
- `VITE_KEYCLOAK_CLIENT_ID`
- `VITE_KEYCLOAK_REGISTER_URL`

보안 규칙:

- `NCP_ACCESS_KEY`, `NCP_SECRET_KEY`는 백엔드에서만 사용
- 프론트 코드/브라우저에 절대 노출하지 않음
- Object Storage 버킷은 private 유지

## 3. FFmpeg 설치

- Windows(choco): `choco install ffmpeg`
- macOS(brew): `brew install ffmpeg`
- Ubuntu: `sudo apt-get install ffmpeg`

설치 후 `ffmpeg -version`으로 확인하고, 필요 시 `FFMPEG_PATH`에 절대 경로를 설정합니다.

## 4. 로컬 실행

1. Keycloak + MySQL 실행 (compose 또는 개별)
2. API 실행

```bash
cd backend/api
./gradlew bootRun
```

3. Frontend 실행

```bash
cd frontend
npm install
npm run dev
```

4. 통합 실행(선택)

```bash
docker compose -f docker-compose.local.yml up --build
```

## 5. 썸네일 처리 흐름

1. `upload-complete` 수신
2. Object Storage HEAD로 파일 존재 확인
3. `lecture_videos` 저장(status=`UPLOADED`)
4. 비동기 작업에서 status=`PROCESSING`
5. FFmpeg로 2초 프레임 캡처
6. 썸네일 업로드 후 `thumbnail_key` 저장
7. status=`READY`

## 6. 테스트

백엔드 테스트 실행:

```bash
cd backend/api
./gradlew test
```

포함 테스트:

- Presigned URL 생성 테스트
- 수강 여부 접근 제어 테스트
- 썸네일 상태 전이 테스트