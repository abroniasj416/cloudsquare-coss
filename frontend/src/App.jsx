import { useEffect, useState } from "react";
import { BrowserRouter, Route, Routes } from "react-router-dom";

import Layout from "./Layout";
import { getAuthConfigError, getAuthState, initAuth, ensureFreshToken } from "./auth";
import Home from "./pages/Home";
import AdminPage from "./pages/AdminPage";
import LectureListPage from "./pages/LectureListPage";
import LectureDetailPage from "./pages/LectureDetailPage";
import VideoPlayerPage from "./pages/VideoPlayerPage";
import AlertBox from "./components/ui/AlertBox";

export default function App() {
  const configError = getAuthConfigError();
  const [auth, setAuth] = useState({
    authenticated: false,
    username: "anonymous",
    isStudent: false,
    isAdmin: false
  });
  const [authError, setAuthError] = useState("");

  async function refreshAuth() {
    await initAuth();
    setAuth(getAuthState());
  }

  useEffect(() => {
    if (configError) {
      setAuthError(configError);
      return;
    }

    let timerId;

    async function bootstrap() {
      try {
        await refreshAuth();
      } catch (error) {
        setAuthError(error instanceof Error ? error.message : String(error));
        return;
      }

      timerId = window.setInterval(async () => {
        try {
          await ensureFreshToken();
          setAuth(getAuthState());
        } catch {
          setAuth(getAuthState());
        }
      }, 15000);
    }

    bootstrap();

    return () => {
      if (timerId) {
        window.clearInterval(timerId);
      }
    };
  }, [configError]);

  if (configError || authError) {
    return (
      <div className="app-shell">
        <main className="content-wrap page-content">
          <AlertBox type="error">{configError || authError}</AlertBox>
        </main>
      </div>
    );
  }

  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Layout auth={auth} refreshAuth={refreshAuth} />}>
          <Route index element={<Home auth={auth} />} />
          <Route path="lectures" element={<LectureListPage auth={auth} />} />
          <Route path="lectures/:lectureId" element={<LectureDetailPage auth={auth} />} />
          <Route path="lectures/:lectureId/watch" element={<VideoPlayerPage auth={auth} />} />
          <Route path="admin" element={<AdminPage auth={auth} />} />
        </Route>
      </Routes>
    </BrowserRouter>
  );
}