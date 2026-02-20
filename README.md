# Cloudsquare COSS LMS Video

React(Vite) + Spring Boot + Keycloak + MySQL + NCP Object Storage(S3 Ìò∏Ìôò) Í∏∞Î∞ò LMS ÏòÅÏÉÅ ÏóÖÎ°úÎìú/Ïä§Ìä∏Î¶¨Î∞ç ÏòàÏ†úÏûÖÎãàÎã§.

## 1. Í∏∞Îä•

- Í¥ÄÎ¶¨Ïûê
  - `POST /api/admin/lectures`: Í∞ïÏùò ÏÉùÏÑ±
  - `POST /api/admin/lectures/{id}/video/upload-init`: presigned PUT URL Î∞úÍ∏â
  - `POST /api/admin/lectures/{id}/video/upload-complete`: ÏóÖÎ°úÎìú ÏôÑÎ£å + HEAD Í≤ÄÏ¶ù + Ïç∏ÎÑ§Ïùº ÎπÑÎèôÍ∏∞ Ï≤òÎ¶¨ ÏãúÏûë
- ÌïôÏÉù
  - `GET /api/lectures`: Í∞ïÏùò Î™©Î°ù
  - `GET /api/lectures/{id}`: Í∞ïÏùò ÏÉÅÏÑ∏
  - `POST /api/lectures/{id}/enroll`: ÏàòÍ∞ï Ïã†Ï≤≠
  - `GET /api/lectures/{id}/playback`: ÏàòÍ∞ïÏÉùÎßå Ïû¨ÏÉù URL Î∞úÍ∏â

## 2. ÌôòÍ≤ΩÎ≥ÄÏàò

Î∞±ÏóîÎìú(`backend/api/.env`):

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

ÌîÑÎ°†Ìä∏(`frontend/.env`):

- `VITE_API_BASE_URL`
- `VITE_KEYCLOAK_URL`
- `VITE_KEYCLOAK_REALM`
- `VITE_KEYCLOAK_CLIENT_ID`
- `VITE_KEYCLOAK_REGISTER_URL`

Î≥¥Ïïà Í∑úÏπô:

- `NCP_ACCESS_KEY`, `NCP_SECRET_KEY`Îäî Î∞±ÏóîÎìúÏóêÏÑúÎßå ÏÇ¨Ïö©
- ÌîÑÎ°†Ìä∏ ÏΩîÎìú/Î∏åÎùºÏö∞Ï†ÄÏóê Ï†àÎåÄ ÎÖ∏Ï∂úÌïòÏßÄ ÏïäÏùå
- Object Storage Î≤ÑÌÇ∑ÏùÄ private Ïú†ÏßÄ

## 3. FFmpeg ÏÑ§Ïπò

- Windows(choco): `choco install ffmpeg`
- macOS(brew): `brew install ffmpeg`
- Ubuntu: `sudo apt-get install ffmpeg`

ÏÑ§Ïπò ÌõÑ `ffmpeg -version`ÏúºÎ°ú ÌôïÏù∏ÌïòÍ≥†, ÌïÑÏöî Ïãú `FFMPEG_PATH`Ïóê Ï†àÎåÄ Í≤ΩÎ°úÎ•º ÏÑ§Ï†ïÌï©ÎãàÎã§.

## 4. Î°úÏª¨ Ïã§Ìñâ

1. Keycloak + MySQL Ïã§Ìñâ (compose ÎòêÎäî Í∞úÎ≥Ñ)
2. API Ïã§Ìñâ

```bash
cd backend/api
./gradlew bootRun
```

3. Frontend Ïã§Ìñâ

```bash
cd frontend
npm install
npm run dev
```

4. ÌÜµÌï© Ïã§Ìñâ(ÏÑ†ÌÉù)

```bash
docker compose -f docker-compose.local.yml up --build
```

## 5. Ïç∏ÎÑ§Ïùº Ï≤òÎ¶¨ ÌùêÎ¶Ñ

1. `upload-complete` ÏàòÏã†
2. Object Storage HEADÎ°ú ÌååÏùº Ï°¥Ïû¨ ÌôïÏù∏
3. `lecture_videos` Ï†ÄÏû•(status=`UPLOADED`)
4. ÎπÑÎèôÍ∏∞ ÏûëÏóÖÏóêÏÑú status=`PROCESSING`
5. FFmpegÎ°ú 2Ï¥à ÌîÑÎ†àÏûÑ Ï∫°Ï≤ò
6. Ïç∏ÎÑ§Ïùº ÏóÖÎ°úÎìú ÌõÑ `thumbnail_key` Ï†ÄÏû•
7. status=`READY`

## 6. ÌÖåÏä§Ìä∏

Î∞±ÏóîÎìú ÌÖåÏä§Ìä∏ Ïã§Ìñâ:

```bash
cd backend/api
./gradlew test
```

Ìè¨Ìï® ÌÖåÏä§Ìä∏:

- Presigned URL ÏÉùÏÑ± ÌÖåÏä§Ìä∏
- ÏàòÍ∞ï Ïó¨Î∂Ä Ï†ëÍ∑º Ï†úÏñ¥ ÌÖåÏä§Ìä∏
- Ïç∏ÎÑ§Ïùº ÏÉÅÌÉú Ï†ÑÏù¥ ÌÖåÏä§Ìä∏
## 7. Completion Certificate µ•∏

### Serial Number ±‘ƒ¢
- «¸Ωƒ: "lectureId-studentId"
- ∞¢ ID¥¬ 4¿⁄∏Æ 0-padding
- øπΩ√: `lectureId=1`, `studentId=2` -> "0001-0002"

### API
- `POST /api/lectures/{lectureId}/complete`
  - «ˆ¿Á ∑Œ±◊¿Œ ªÁøÎ¿⁄ ±‚¡ÿ ºˆ∞≠ øœ∑· √≥∏Æ + ºˆ∑·¡ı πﬂ±ﬁ
  - ¿ÃπÃ πﬂ±ﬁµ» ∞ÊøÏ ±‚¡∏ ºˆ∑·¡ı π›»Ø
- `GET /api/certificates/me`
  - «ˆ¿Á ∑Œ±◊¿Œ ªÁøÎ¿⁄ ºˆ∑·¡ı ∏Ò∑œ ¡∂»∏
- `GET /api/certificates?userId={id}`
  - External ø¨µøøÎ µ•∏ API
  - øÓøµ »Ø∞Êø°º≠¥¬ ≥ª∫Œ∏¡/πÊ»≠∫Æ/ACG ¡¶«— ¿¸¡¶
- `GET /api/certificates/lectures/{lectureId}/me`
  - ∆Ø¡§ ∞≠¿«¿« ≥ª ºˆ∑·¡ı ¡∂»∏

### Demo Ω√≥™∏Æø¿
1. «–ª˝ ∞Ë¡§¿∏∑Œ ∑Œ±◊¿Œ
2. `Lectures` -> ∞≠¿« ªÛºº -> `Complete Lecture` ≈¨∏Ø
3. πﬂ±ﬁµ» `serialNumber` »Æ¿Œ
4. ªÛ¥‹ `Certificates` ∏ﬁ¥∫ø°º≠ ≥ª ºˆ∑·¡ı ∏Ò∑œ »Æ¿Œ
