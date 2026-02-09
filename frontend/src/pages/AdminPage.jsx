import { useState } from "react";

import {
  completeVideoUpload,
  createAdminLecture,
  initVideoUpload,
  putObjectByPresignedUrl
} from "../api";
import AlertBox from "../components/ui/AlertBox";
import Card from "../components/ui/Card";
import SectionTitle from "../components/ui/SectionTitle";

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
      setResult(`Lecture created:\n${JSON.stringify(created, null, 2)}`);
      setError("");
    } catch (err) {
      setError(err.message);
      setResult("");
    }
  }

  async function handleUpload(e) {
    e.preventDefault();

    if (!lectureId || !file) {
      setError("Both lectureId and video file are required.");
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
          `upload-init:\n${JSON.stringify(init, null, 2)}`,
          `upload-complete:\n${JSON.stringify(complete, null, 2)}`
        ].join("\n\n")
      );
      setError("");
    } catch (err) {
      setError(err.message);
      setResult("");
    }
  }

  return (
    <section className="stack-lg">
      <SectionTitle
        eyebrow="Administration"
        title="Lecture Upload Console"
        subtitle="Create a lecture first, then upload video by presigned URL workflow."
      />

      {!auth.isAdmin && <AlertBox type="warning">ROLE_ADMIN is required for this page.</AlertBox>}
      {error && <AlertBox type="error">{error}</AlertBox>}

      <div className="admin-grid">
        <Card>
          <h3>1. Create Lecture</h3>
          <p className="muted">The returned ID is auto-filled below for upload.</p>
          <form onSubmit={handleCreateLecture} className="form">
            <label>
              Title
              <input
                value={lectureForm.title}
                onChange={(e) => setLectureForm({ ...lectureForm, title: e.target.value })}
                placeholder="e.g. Data Structures Week 1"
                required
              />
            </label>
            <label>
              Description
              <input
                value={lectureForm.description}
                onChange={(e) => setLectureForm({ ...lectureForm, description: e.target.value })}
                placeholder="Summary shown on lecture card"
                required
              />
            </label>
            <button className="btn primary" type="submit" disabled={!auth.isAdmin}>
              Create Lecture
            </button>
          </form>
        </Card>

        <Card>
          <h3>2. Upload Video</h3>
          <p className="muted">Upload directly to object storage, then register metadata.</p>
          <form onSubmit={handleUpload} className="form">
            <label>
              Lecture ID
              <input
                type="number"
                min="1"
                value={lectureId}
                onChange={(e) => setLectureId(e.target.value)}
                required
              />
            </label>
            <label>
              Video File
              <input
                type="file"
                accept="video/*"
                onChange={(e) => setFile(e.target.files?.[0] ?? null)}
                required
              />
            </label>
            <button className="btn primary" type="submit" disabled={!auth.isAdmin}>
              Start Upload
            </button>
          </form>
        </Card>
      </div>

      {result && (
        <Card>
          <h3>Result</h3>
          <pre>{result}</pre>
        </Card>
      )}
    </section>
  );
}