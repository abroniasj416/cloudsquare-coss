import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";

import { getPlayback } from "../api";
import AlertBox from "../components/ui/AlertBox";
import Card from "../components/ui/Card";
import SectionTitle from "../components/ui/SectionTitle";

export default function VideoPlayerPage({ auth }) {
  const { lectureId } = useParams();
  const [playbackUrl, setPlaybackUrl] = useState("");
  const [error, setError] = useState("");

  async function loadPlayback() {
    try {
      const data = await getPlayback(lectureId);
      setPlaybackUrl(data.playbackUrl);
      setError("");
    } catch (e) {
      setError(e.message);
      setPlaybackUrl("");
    }
  }

  useEffect(() => {
    if (auth.authenticated) {
      loadPlayback();
    }
  }, [auth.authenticated, lectureId]);

  return (
    <section className="stack-lg">
      <SectionTitle
        eyebrow={`Lecture #${lectureId}`}
        title="Video Player"
        subtitle="Refresh playback URL when it expires and continue streaming."
        actions={
          <button className="btn primary" onClick={loadPlayback} disabled={!auth.authenticated}>
            Refresh Playback URL
          </button>
        }
      />

      {!auth.authenticated && <AlertBox type="warning">Login is required to watch videos.</AlertBox>}
      {error && <AlertBox type="error">{error}</AlertBox>}

      <Card className="player-card">
        {playbackUrl ? (
          <video src={playbackUrl} controls className="video-player" />
        ) : (
          <div className="player-placeholder">Playback URL is not ready yet.</div>
        )}
      </Card>
    </section>
  );
}