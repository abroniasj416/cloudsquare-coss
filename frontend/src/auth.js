import Keycloak from "keycloak-js";

const authEnv = {
  VITE_KEYCLOAK_URL: import.meta.env.VITE_KEYCLOAK_URL,
  VITE_KEYCLOAK_REALM: import.meta.env.VITE_KEYCLOAK_REALM,
  VITE_KEYCLOAK_CLIENT_ID: import.meta.env.VITE_KEYCLOAK_CLIENT_ID
};

const missingAuthEnvKeys = Object.entries(authEnv)
  .filter(([, value]) => typeof value !== "string" || value.trim() === "")
  .map(([key]) => key);

const authConfigError =
  missingAuthEnvKeys.length > 0
    ? `Missing frontend env keys: ${missingAuthEnvKeys.join(", ")}`
    : null;

if (authConfigError) {
  console.error("[auth] Environment variable validation failed.", {
    missingKeys: missingAuthEnvKeys
  });
}

const keycloak = authConfigError
  ? null
  : new Keycloak({
      url: authEnv.VITE_KEYCLOAK_URL,
      realm: authEnv.VITE_KEYCLOAK_REALM,
      clientId: authEnv.VITE_KEYCLOAK_CLIENT_ID
    });

let initialized = false;
let initPromise = null;

function assertAuthConfigured() {
  if (authConfigError || !keycloak) {
    throw new Error(authConfigError ?? "Keycloak is not configured.");
  }
}

export function getAuthConfigError() {
  return authConfigError;
}

export async function initAuth() {
  assertAuthConfigured();

  if (initialized) {
    return keycloak;
  }

  if (initPromise) {
    await initPromise;
    return keycloak;
  }

  initPromise = keycloak
    .init({
      onLoad: "check-sso",
      pkceMethod: "S256",
      checkLoginIframe: false,
      silentCheckSsoRedirectUri: `${window.location.origin}/silent-check-sso.html`
    })
    .then(() => {
      initialized = true;
    })
    .finally(() => {
      initPromise = null;
    });

  await initPromise;
  return keycloak;
}

export async function ensureFreshToken() {
  assertAuthConfigured();

  if (!keycloak.authenticated) {
    return null;
  }

  await keycloak.updateToken(30);
  return keycloak.token;
}

export function login() {
  assertAuthConfigured();
  return keycloak.login({
    redirectUri: window.location.href
  });
}

export function logout() {
  assertAuthConfigured();
  return keycloak.logout({
    redirectUri: window.location.origin
  });
}

export function register() {
  assertAuthConfigured();
  const registerUrl = import.meta.env.VITE_KEYCLOAK_REGISTER_URL;
  if (registerUrl) {
    window.location.href = registerUrl;
    return;
  }

  return keycloak.register({
    redirectUri: window.location.origin
  });
}

export function hasRole(role) {
  if (!keycloak) {
    return false;
  }
  return keycloak.hasRealmRole(role) || keycloak.hasResourceRole(role, "coss-api");
}

export function getAuthState() {
  return {
    authenticated: !!keycloak.authenticated,
    tokenParsed: keycloak.tokenParsed ?? null,
    token: keycloak.token ?? null,
    username: keycloak.tokenParsed?.preferred_username ?? keycloak.subject ?? "anonymous",
    isStudent: hasRole("STUDENT"),
    isAdmin: hasRole("ADMIN")
  };
}

export default keycloak;
