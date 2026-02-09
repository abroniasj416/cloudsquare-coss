import { NavLink, Outlet, useLocation } from "react-router-dom";

import { login, logout, register } from "./auth";
import Badge from "./components/ui/Badge";

function roleLabel(auth) {
  if (auth.isAdmin) {
    return "ADMIN";
  }
  if (auth.isStudent) {
    return "STUDENT";
  }
  return "NONE";
}

export default function Layout({ auth, refreshAuth }) {
  const location = useLocation();
  const isHome = location.pathname === "/";

  return (
    <div className="app-shell">
      <header className="app-header">
        <div className="content-wrap header-inner">
          <div className="brand-block">
            <div className="brand-mark">C</div>
            <div>
              <div className="brand-name">COSS LMS Video</div>
              <div className="brand-sub">Convergence and Open Sharing System</div>
            </div>
          </div>

          <nav className="main-nav">
            <NavLink to="/">Home</NavLink>
            <NavLink to="/lectures">Lectures</NavLink>
            <NavLink to="/admin">Admin</NavLink>
          </nav>

          <div className="header-right">
            <div className="auth-meta">
              <span>{auth.username}</span>
              <Badge variant={auth.authenticated ? "success" : "neutral"}>
                {auth.authenticated ? "Authenticated" : "Guest"}
              </Badge>
              <Badge variant={auth.isAdmin ? "primary" : auth.isStudent ? "info" : "neutral"}>
                {roleLabel(auth)}
              </Badge>
            </div>
            <div className="button-row compact">
              <button className="btn ghost" onClick={() => login()}>
                Login
              </button>
              <button className="btn ghost" onClick={() => register()}>
                Register
              </button>
              <button
                className="btn ghost"
                onClick={async () => {
                  await logout();
                  await refreshAuth();
                }}
              >
                Logout
              </button>
            </div>
          </div>
        </div>
      </header>

      {isHome && (
        <section className="hero">
          <div className="content-wrap hero-inner">
            <p className="hero-kicker">Digital Learning Experience</p>
            <h1>Convergence and Open Sharing System</h1>
            <p className="hero-subtitle">
              Secure video delivery for enrolled learners with admin upload workflows and controlled playback.
            </p>
          </div>
        </section>
      )}

      <main className="content-wrap page-content">
        <Outlet context={{ auth, refreshAuth }} />
      </main>
    </div>
  );
}