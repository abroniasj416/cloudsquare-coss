import { ensureFreshToken } from "./auth";

const apiBaseUrl = import.meta.env.VITE_API_BASE_URL;

async function request(path, options = {}) {
  const token = await ensureFreshToken();

  const headers = {
    ...(options.body ? { "Content-Type": "application/json" } : {}),
    ...(options.headers || {}),
    ...(token ? { Authorization: `Bearer ${token}` } : {})
  };

  const response = await fetch(`${apiBaseUrl}${path}`, {
    ...options,
    headers
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

export function getLectures() {
  return request("/api/lectures");
}

export function getLecture(lectureId) {
  return request(`/api/lectures/${lectureId}`);
}

export function enrollLecture(lectureId) {
  return request(`/api/lectures/${lectureId}/enroll`, { method: "POST" });
}

export function getPlayback(lectureId) {
  return request(`/api/lectures/${lectureId}/playback`);
}

export function createAdminLecture(payload) {
  return request("/api/admin/lectures", {
    method: "POST",
    body: JSON.stringify(payload)
  });
}

export function initVideoUpload(lectureId) {
  return request(`/api/admin/lectures/${lectureId}/video/upload-init`, { method: "POST" });
}

export function completeVideoUpload(lectureId, payload) {
  return request(`/api/admin/lectures/${lectureId}/video/upload-complete`, {
    method: "POST",
    body: JSON.stringify(payload)
  });
}

export async function putObjectByPresignedUrl(presignedPutUrl, file) {
  const response = await fetch(presignedPutUrl, {
    method: "PUT",
    headers: {
      "Content-Type": file.type || "video/mp4"
    },
    body: file
  });

  if (!response.ok) {
    const text = await response.text();
    throw new Error(`PUT upload failed: ${response.status} ${response.statusText} ${text}`.trim());
  }
}
