import { useState } from "react";

import { createAdminCourse } from "../api";

const initialForm = {
  courseCode: "",
  title: "",
  instructorName: "",
  capacity: 30
};

export default function AdminPage({ auth }) {
  const [form, setForm] = useState(initialForm);
  const [result, setResult] = useState("");
  const [error, setError] = useState("");

  async function submit(e) {
    e.preventDefault();

    try {
      const payload = {
        ...form,
        capacity: Number(form.capacity)
      };
      const created = await createAdminCourse(payload);
      setResult(JSON.stringify(created, null, 2));
      setError("");
      setForm(initialForm);
    } catch (err) {
      setError(err.message);
      setResult("");
    }
  }

  return (
    <section>
      <h2>Admin</h2>
      <p>Submit the admin-only course creation form for `/api/admin/courses`.</p>
      {!auth.isAdmin && <p className="warn">Access denied unless the user has ADMIN role.</p>}

      <form onSubmit={submit} className="form">
        <label>
          courseCode
          <input
            value={form.courseCode}
            onChange={(e) => setForm({ ...form, courseCode: e.target.value })}
            required
          />
        </label>
        <label>
          title
          <input
            value={form.title}
            onChange={(e) => setForm({ ...form, title: e.target.value })}
            required
          />
        </label>
        <label>
          instructorName
          <input
            value={form.instructorName}
            onChange={(e) => setForm({ ...form, instructorName: e.target.value })}
            required
          />
        </label>
        <label>
          capacity
          <input
            type="number"
            min="1"
            value={form.capacity}
            onChange={(e) => setForm({ ...form, capacity: e.target.value })}
            required
          />
        </label>
        <button type="submit" disabled={!auth.authenticated}>
          Create Course
        </button>
      </form>

      {!auth.authenticated && <p>Not authenticated.</p>}
      {error && <pre className="error">Request failed: {error}</pre>}
      {result && <pre>{result}</pre>}
    </section>
  );
}
