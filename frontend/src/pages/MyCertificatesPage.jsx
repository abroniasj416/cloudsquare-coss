import { useEffect, useState } from "react";

import { getMyCertificates } from "../api";
import AlertBox from "../components/ui/AlertBox";
import Card from "../components/ui/Card";
import SectionTitle from "../components/ui/SectionTitle";

export default function MyCertificatesPage({ auth }) {
  const [certificates, setCertificates] = useState([]);
  const [error, setError] = useState("");

  async function loadCertificates() {
    try {
      const data = await getMyCertificates();
      setCertificates(data);
      setError("");
    } catch (e) {
      setError(e.message);
    }
  }

  useEffect(() => {
    if (auth.authenticated) {
      loadCertificates();
    }
  }, [auth.authenticated]);

  return (
    <section className="stack-lg">
      <SectionTitle
        eyebrow="Certificates"
        title="My Completion Certificates"
        subtitle="Check issued serial numbers and issued timestamps."
        actions={
          <button className="btn primary" onClick={loadCertificates} disabled={!auth.authenticated}>
            Refresh
          </button>
        }
      />

      {!auth.authenticated && <AlertBox type="warning">Login is required to view certificates.</AlertBox>}
      {error && <AlertBox type="error">{error}</AlertBox>}

      {auth.authenticated && !error && (
        <Card>
          {certificates.length === 0 ? (
            <p className="muted">No certificates issued yet.</p>
          ) : (
            <table className="simple-table">
              <thead>
                <tr>
                  <th>Serial Number</th>
                  <th>Lecture ID</th>
                  <th>User ID</th>
                  <th>Issued At</th>
                </tr>
              </thead>
              <tbody>
                {certificates.map((certificate) => (
                  <tr key={`${certificate.serialNumber}-${certificate.issuedAt}`}>
                    <td>{certificate.serialNumber}</td>
                    <td>{certificate.lectureId}</td>
                    <td>{certificate.userId}</td>
                    <td>{certificate.issuedAt}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          )}
        </Card>
      )}
    </section>
  );
}
