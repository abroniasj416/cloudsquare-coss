import { useEffect, useState } from "react";
import { BrowserRouter, Route, Routes } from "react-router-dom";

import Layout from "./Layout";
import { getAuthConfigError, getAuthState, initAuth, ensureFreshToken } from "./auth";
import Home from "./pages/Home";
import StudentPage from "./pages/StudentPage";
import AdminPage from "./pages/AdminPage";

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

  if (configError) {
    return (
      <div className="container">
        <header className="header">
          <h1>COSS Demo</h1>
        </header>
        <section>
          <h2>Environment variables missing</h2>
          <p>Copy `frontend/.env.example` to `frontend/.env` and restart the dev server.</p>
          <pre className="error">{configError}</pre>
        </section>
      </div>
    );
  }

  if (authError) {
    return (
      <div className="container">
        <header className="header">
          <h1>COSS Demo</h1>
        </header>
        <section>
          <h2>Authentication initialization failed</h2>
          <p>Check browser console and verify Keycloak URL/realm/client configuration.</p>
          <pre className="error">{authError}</pre>
        </section>
      </div>
    );
  }

  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Layout auth={auth} refreshAuth={refreshAuth} />}>
          <Route index element={<Home auth={auth} />} />
          <Route path="student" element={<StudentPage auth={auth} />} />
          <Route path="admin" element={<AdminPage auth={auth} />} />
        </Route>
      </Routes>
    </BrowserRouter>
  );
}
