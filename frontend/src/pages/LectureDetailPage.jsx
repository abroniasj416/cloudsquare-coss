import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";

import { enrollLecture, getLecture } from "../api";

export default function LectureDetailPage({ auth }) {
  const { lectureId } = useParams();
  const navigate = useNavigate();
  const [lecture, setLecture] = useState(null);
  const [error, setError] = useState("");
  const [enrollResult, setEnrollResult] = useState("");

  async function loadLecture() {
    try {
      const data = await getLecture(lectureId);
      setLecture(data);
      setError("");
    } catch (e) {
      setError(e.message);
    }
  }

  useEffect(() => {
    if (auth.authenticated) {
      loadLecture();
    }
  }, [auth.authenticated, lectureId]);

  async function handleEnroll() {
    try {
      await enrollLecture(lectureId);
      setEnrollResult("수강 신청 완료");
      setError("");
    } catch (e) {
      setError(e.message);
    }
  }

  if (!auth.authenticated) {
    return <p>로그인 후 강의 상세를 확인할 수 있습니다.</p>;
  }

  return (
    <section>
      <h2>강의 상세</h2>
      {error && <pre className="error">{error}</pre>}
      {lecture && (
        <>
          {lecture.thumbnailUrl ? (
            <img src={lecture.thumbnailUrl} alt={`${lecture.title} thumbnail`} className="detail-thumb" />
          ) : (
            <div className="detail-thumb placeholder">No Thumbnail</div>
          )}
          <h3>{lecture.title}</h3>
          <p>{lecture.description}</p>
          <p>Status: {lecture.videoStatus || "EMPTY"}</p>
          <div className="button-row">
            <button onClick={handleEnroll}>수강 신청</button>
            <button onClick={() => navigate(`/lectures/${lectureId}/watch`)}>재생 페이지 이동</button>
          </div>
          {enrollResult && <pre>{enrollResult}</pre>}
        </>
      )}
    </section>
  );
}
