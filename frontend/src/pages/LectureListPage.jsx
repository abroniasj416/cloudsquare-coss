import { useEffect, useState } from "react";
import { Link } from "react-router-dom";

import { getLectures } from "../api";
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

  return (
    <section className="stack-lg">
      <SectionTitle
        eyebrow="Lecture Catalog"
        title="Available Lectures"
        subtitle="Browse the catalog, check processing status, and open lecture detail."
        actions={
          <button className="btn primary" onClick={loadLectures} disabled={!auth.authenticated}>
            Refresh
          </button>
        }
      />

      {!auth.authenticated && <AlertBox type="warning">Login is required to load lectures.</AlertBox>}
      {error && <AlertBox type="error">{error}</AlertBox>}

      <div className="lecture-grid">
        {lectures.map((lecture) => (
          <Card key={lecture.id} className="lecture-item">
            {lecture.thumbnailUrl ? (
              <img src={lecture.thumbnailUrl} alt={`${lecture.title} thumbnail`} className="thumb" />
            ) : (
              <div className="thumb placeholder">No thumbnail yet</div>
            )}
            <div className="lecture-meta">
              <div className="lecture-title-row">
                <h3>{lecture.title}</h3>
                <Badge variant={statusVariant(lecture.videoStatus)}>{lecture.videoStatus || "EMPTY"}</Badge>
              </div>
              <p>{lecture.description}</p>
              <Link className="btn-link" to={`/lectures/${lecture.id}`}>
                Open Detail
              </Link>
            </div>
          </Card>
        ))}
      </div>
    </section>
  );
}