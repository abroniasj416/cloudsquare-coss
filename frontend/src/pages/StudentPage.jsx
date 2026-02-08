import { useEffect, useState } from "react";

import { createAdminCourse, getStudentCourses } from "../api";

export default function StudentPage({ auth }) {
  const [courses, setCourses] = useState([]);
  const [error, setError] = useState("");
  const [admin403Result, setAdmin403Result] = useState("");

  async function loadCourses() {
    try {
      const data = await getStudentCourses();
      setCourses(data);
      setError("");
    } catch (e) {
      setError(e.message);
    }
  }

  useEffect(() => {
    if (auth.authenticated) {
      loadCourses();
    }
  }, [auth.authenticated]);

  async function verifyAdminForbidden() {
    setAdmin403Result("Checking access...");

    try {
      await createAdminCourse({
        courseCode: "COSS-FORBIDDEN-001",
        title: "Should Fail",
        instructorName: "Demo",
        capacity: 10
      });
      setAdmin403Result("Unexpected result: admin API call succeeded.");
    } catch (e) {
      if (String(e.message).startsWith("403")) {
        setAdmin403Result("Expected result: access denied (403) for student role.");
      } else {
        setAdmin403Result(`Request failed: ${e.message}`);
      }
    }
  }

  return (
    <section>
      <h2>Student</h2>
      <p>Load `/api/student/courses` and verify `/api/admin/courses` returns 403.</p>
      <div className="button-row">
        <button disabled={!auth.authenticated} onClick={loadCourses}>
          Refresh Courses
        </button>
        <button disabled={!auth.authenticated} onClick={verifyAdminForbidden}>
          Verify Admin 403
        </button>
      </div>

      {!auth.authenticated && <p>Not authenticated.</p>}
      {error && <pre className="error">Request failed: {error}</pre>}
      {admin403Result && <pre>{admin403Result}</pre>}
      <pre>{JSON.stringify(courses, null, 2)}</pre>
    </section>
  );
}
