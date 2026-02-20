import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";

import { completeLecture, enrollLecture, getLecture } from "../api";
import AlertBox from "../components/ui/AlertBox";
import Badge from "../components/ui/Badge";
import Card from "../components/ui/Card";
import SectionTitle from "../components/ui/SectionTitle";

function statusVariant(status) {
  if (status === "READY") {
    return "success";
  }
  if (status === "PROCESSING") {
    return "warning";
  }
  if (status === "UPLOADED") {
    return "info";
  }
  return "neutral";
}

export default function LectureDetailPage({ auth }) {
  const { lectureId } = useParams();
  const navigate = useNavigate();
  const [lecture, setLecture] = useState(null);
  const [error, setError] = useState("");
  const [enrollResult, setEnrollResult] = useState("");
  const [completeResult, setCompleteResult] = useState("");

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
      setEnrollResult("Enrollment completed.");
      setError("");
    } catch (e) {
      setError(e.message);
    }
  }

  async function handleComplete() {
    try {
      const result = await completeLecture(lectureId);
      setCompleteResult(`Completion certificate issued: ${result.serialNumber}`);
      setError("");
    } catch (e) {
      setError(e.message);
    }
  }

  return (
    <section className="stack-lg">
      <SectionTitle
        eyebrow={`Lecture #${lectureId}`}
        title="Lecture Detail"
        subtitle="Review lecture metadata, enroll, complete, and move to the watch page."
      />

      {!auth.authenticated && <AlertBox type="warning">Login is required to view lecture detail.</AlertBox>}
      {error && <AlertBox type="error">{error}</AlertBox>}
      {enrollResult && <AlertBox type="success">{enrollResult}</AlertBox>}
      {completeResult && <AlertBox type="success">{completeResult}</AlertBox>}

      {lecture && (
        <div className="detail-layout">
          <Card className="detail-hero">
            {lecture.thumbnailUrl ? (
              <img src={lecture.thumbnailUrl} alt={`${lecture.title} thumbnail`} className="detail-thumb" />
            ) : (
              <div className="detail-thumb placeholder">No thumbnail yet</div>
            )}
            <div className="detail-body">
              <div className="lecture-title-row">
                <h3>{lecture.title}</h3>
                <Badge variant={statusVariant(lecture.videoStatus)}>{lecture.videoStatus || "EMPTY"}</Badge>
              </div>
              <p>{lecture.description}</p>
            </div>
          </Card>

          <Card>
            <h3>Actions</h3>
            <p>Enrollment is required before playback.</p>
            <div className="button-row">
              <button className="btn primary" onClick={handleEnroll}>
                Enroll
              </button>
              <button className="btn secondary" onClick={handleComplete}>
                Complete Lecture
              </button>
              <button className="btn secondary" onClick={() => navigate(`/lectures/${lectureId}/watch`)}>
                Go To Player
              </button>
            </div>
          </Card>
        </div>
      )}
    </section>
  );
}
