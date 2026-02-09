import { useState } from "react";

import {
  completeVideoUpload,
  createAdminLecture,
  initVideoUpload,
  putObjectByPresignedUrl
} from "../api";

const initialLectureForm = {
  title: "",
  description: ""
};

export default function AdminPage({ auth }) {
  const [lectureForm, setLectureForm] = useState(initialLectureForm);
  const [lectureId, setLectureId] = useState("");
  const [file, setFile] = useState(null);
  const [result, setResult] = useState("");
  const [error, setError] = useState("");

  async function handleCreateLecture(e) {
    e.preventDefault();

    try {
      const created = await createAdminLecture(lectureForm);
      setLectureId(String(created.id));
      setLectureForm(initialLectureForm);
      setResult(`강의 생성 완료: ${JSON.stringify(created, null, 2)}`);
      setError("");
    } catch (err) {
      setError(err.message);
      setResult("");
    }
  }

  async function handleUpload(e) {
    e.preventDefault();

    if (!lectureId || !file) {
      setError("lectureId와 파일을 모두 입력하세요.");
      return;
    }

    try {
      const init = await initVideoUpload(lectureId);
      await putObjectByPresignedUrl(init.presignedPutUrl, file);
      const complete = await completeVideoUpload(lectureId, {
        objectKey: init.objectKey,
        sizeBytes: file.size,
        contentType: file.type || "video/mp4"
      });

      setResult(
        [
          `upload-init: ${JSON.stringify(init, null, 2)}`,
          `upload-complete: ${JSON.stringify(complete, null, 2)}`
        ].join("\n\n")
      );
      setError("");
    } catch (err) {
      setError(err.message);
      setResult("");
    }
  }

  return (
    <section>
      <h2>관리자 영상 업로드</h2>
      {!auth.isAdmin && <p className="warn">ROLE_ADMIN 권한이 필요합니다.</p>}

      <form onSubmit={handleCreateLecture} className="form">
        <h3>1) 강의 생성</h3>
        <label>
          title
          <input
            value={lectureForm.title}
            onChange={(e) => setLectureForm({ ...lectureForm, title: e.target.value })}
            required
          />
        </label>
        <label>
          description
          <input
            value={lectureForm.description}
            onChange={(e) => setLectureForm({ ...lectureForm, description: e.target.value })}
            required
          />
        </label>
        <button type="submit" disabled={!auth.isAdmin}>강의 생성</button>
      </form>

      <form onSubmit={handleUpload} className="form">
        <h3>2) 영상 업로드</h3>
        <label>
          lectureId
          <input
            type="number"
            min="1"
            value={lectureId}
            onChange={(e) => setLectureId(e.target.value)}
            required
          />
        </label>
        <label>
          video file
          <input
            type="file"
            accept="video/*"
            onChange={(e) => setFile(e.target.files?.[0] ?? null)}
            required
          />
        </label>
        <button type="submit" disabled={!auth.isAdmin}>업로드 실행</button>
      </form>

      {error && <pre className="error">{error}</pre>}
      {result && <pre>{result}</pre>}
    </section>
  );
}
