import { ensureFreshToken } from "./auth";

const apiBaseUrl = import.meta.env.VITE_API_BASE_URL;

async function request(path, options = {}) {
  const token = await ensureFreshToken();

  const response = await fetch(`${apiBaseUrl}${path}`, {
    ...options,
    headers: {
      "Content-Type": "application/json",
      ...(options.headers || {}),
      ...(token ? { Authorization: `Bearer ${token}` } : {})
    }
  });

  if (!response.ok) {
    const text = await response.text();
    throw new Error(`${response.status} ${response.statusText} ${text}`.trim());
  }

  if (response.status === 204) {
    return null;
  }

  return response.json();
}

export function getMyInfo() {
  return request("/api/me");
}

export function getStudentCourses() {
  return request("/api/student/courses");
}

export function getStudentEnrollments() {
  return request("/api/student/enrollments");
}

export function createAdminCourse(payload) {
  return request("/api/admin/courses", {
    method: "POST",
    body: JSON.stringify(payload)
  });
}

export function createAdminCourseExpect403(payload) {
  return fetch(`${apiBaseUrl}/api/admin/courses`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json"
    },
    body: JSON.stringify(payload)
  });
}
