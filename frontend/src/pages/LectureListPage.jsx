import { useEffect, useState } from "react";
import { Link } from "react-router-dom";

import { getLectures } from "../api";

export default function LectureListPage({ auth }) {
  const [lectures, setLectures] = useState([]);
  const [error, setError] = useState("");

  async function loadLectures() {
    try {
      const data = await getLectures();
      setLectures(data);
      setError("");
    } catch (e) {
      setError(e.message);
    }
  }

  useEffect(() => {
    if (auth.authenticated) {
      loadLectures();
    }
  }, [auth.authenticated]);

  if (!auth.authenticated) {
    return <p>로그인 후 강의 목록을 확인할 수 있습니다.</p>;
  }

  return (
    <section>
      <h2>강의 목록</h2>
      <div className="button-row">
        <button onClick={loadLectures}>새로고침</button>
      </div>
      {error && <pre className="error">{error}</pre>}
      <div className="card-grid">
        {lectures.map((lecture) => (
          <article key={lecture.id} className="lecture-card">
            {lecture.thumbnailUrl ? (
              <img src={lecture.thumbnailUrl} alt={`${lecture.title} thumbnail`} className="thumb" />
            ) : (
              <div className="thumb placeholder">No Thumbnail</div>
            )}
            <h3>{lecture.title}</h3>
            <p>{lecture.description}</p>
            <p>Status: {lecture.videoStatus || "EMPTY"}</p>
            <Link to={`/lectures/${lecture.id}`}>상세 보기</Link>
          </article>
        ))}
      </div>
    </section>
  );
}
