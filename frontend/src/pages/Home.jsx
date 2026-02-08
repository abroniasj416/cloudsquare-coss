import { useEffect, useState } from "react";

import { getMyInfo } from "../api";

export default function Home({ auth }) {
  const [me, setMe] = useState(null);
  const [error, setError] = useState("");

  useEffect(() => {
    let mounted = true;

    async function load() {
      if (!auth.authenticated) {
        setMe(null);
        setError("");
        return;
      }

      try {
        const data = await getMyInfo();
        if (mounted) {
          setMe(data);
          setError("");
        }
      } catch (e) {
        if (mounted) {
          setError(e.message);
        }
      }
    }

    load();
    return () => {
      mounted = false;
    };
  }, [auth.authenticated]);

  return (
    <section>
      <h2>Home</h2>
      <p>Show authentication status and the `/api/me` result.</p>
      {!auth.authenticated && <p>Not authenticated. Please login to call APIs.</p>}
      {error && <pre className="error">Request failed: {error}</pre>}
      {me && <pre>{JSON.stringify(me, null, 2)}</pre>}
    </section>
  );
}
