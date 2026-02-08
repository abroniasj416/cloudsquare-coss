import { NavLink, Outlet } from "react-router-dom";

import { login, logout, register } from "./auth";

export default function Layout({ auth, refreshAuth }) {
  return (
    <div className="container">
      <header className="header">
        <h1>COSS Demo</h1>
        <div className="auth-box">
          <div>User: {auth.username}</div>
          <div>Authenticated: {auth.authenticated ? "Yes" : "No"}</div>
          <div>Role: {auth.isAdmin ? "ADMIN" : auth.isStudent ? "STUDENT" : "NONE"}</div>
        </div>
        <div className="button-row">
          <button onClick={() => login()}>Login</button>
          <button onClick={() => register()}>Register</button>
          <button
            onClick={async () => {
              await logout();
              await refreshAuth();
            }}
          >
            Logout
          </button>
        </div>
      </header>

      <nav className="nav">
        <NavLink to="/">Home</NavLink>
        <NavLink to="/student">Student</NavLink>
        <NavLink to="/admin">Admin</NavLink>
      </nav>

      <main>
        <Outlet context={{ auth, refreshAuth }} />
      </main>
    </div>
  );
}
