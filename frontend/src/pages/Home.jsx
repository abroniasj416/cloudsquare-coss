import { useEffect, useState } from "react";

import { getMyInfo } from "../api";
import Card from "../components/ui/Card";
import SectionTitle from "../components/ui/SectionTitle";
import AlertBox from "../components/ui/AlertBox";

export default function Home({ auth }) {
  const [me, setMe] = useState(null);

  useEffect(() => {
    let mounted = true;

    async function load() {
      if (!auth.authenticated) {
        setMe(null);
        return;
      }

      try {
        const data = await getMyInfo();
        if (mounted) {
          setMe(data);
        }
      } catch {
        if (mounted) {
          setMe(null);
        }
      }
    }

    load();
    return () => {
      mounted = false;
    };
  }, [auth.authenticated]);

  return (
    <section className="stack-lg">
      <SectionTitle
        eyebrow="Overview"
        title="Learning Dashboard"
        subtitle="Manage lectures, enroll students, upload videos, and stream securely."
      />

      {!auth.authenticated && (
        <AlertBox type="warning">Login is required to call protected APIs and view personalized data.</AlertBox>
      )}

      <div className="feature-grid">
        <Card>
          <h3>Admin Flow</h3>
          <p>Create lecture, initialize upload, upload to Object Storage, and finalize metadata.</p>
        </Card>
        <Card>
          <h3>Student Flow</h3>
          <p>Enroll first, then request playback URL and stream video with expiration refresh.</p>
        </Card>
        <Card>
          <h3>Security</h3>
          <p>JWT authentication with enrollment checks and private storage objects.</p>
        </Card>
      </div>

      {me && (
        <Card>
          <h3>Current Identity Payload</h3>
          <pre>{JSON.stringify(me, null, 2)}</pre>
        </Card>
      )}
    </section>
  );
}