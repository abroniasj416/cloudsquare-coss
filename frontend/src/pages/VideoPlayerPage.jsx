import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";

import { getPlayback } from "../api";

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

  if (!auth.authenticated) {
    return <p>로그인 후 영상을 시청할 수 있습니다.</p>;
  }

  return (
    <section>
      <h2>영상 재생</h2>
      <div className="button-row">
        <button onClick={loadPlayback}>재생 URL 갱신</button>
      </div>
      {error && <pre className="error">{error}</pre>}
      {playbackUrl && <video src={playbackUrl} controls className="video-player" />}
    </section>
  );
}
