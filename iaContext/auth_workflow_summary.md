# 🔐 Auth Workflow Summary (React + Spring Boot)

## 🧩 Task Description

Goal: Implement secure **JWT authentication with refresh tokens** in a React (SPA) frontend and Spring Boot backend.  
Requirements:
- Short-lived **access tokens** for API access.  
- Long-lived **refresh tokens** stored in **HTTP-only cookies**.  
- Automatic token refreshing and route protection.  
- Proper handling of async refresh before redirects.

---

## ⚙️ Backend Workflow (Spring Boot)

### **1️⃣ Login Flow**
- User authenticates via `/api/auth/login`.
- Backend:
  - Validates credentials.
  - Generates **access token** (JWT).
  - Creates a **refresh token entity** (DB).
  - Sends refresh token in `Set-Cookie` header:

    ```java
    ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", refreshToken.getToken())
        .httpOnly(true)
        .secure(cookieSecure)
        .sameSite(cookieSameSite)
        .path("/api/auth/refresh")
        .maxAge(7 * 24 * 60 * 60)
        .build();
    response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());
    ```

  - Returns access token in JSON body.

---

### **2️⃣ Refresh Flow**
- Client calls `/api/auth/refresh` automatically on 401.
- Backend:
  - Reads `refreshToken` cookie.
  - Validates + verifies expiry.
  - Issues **new access token** and **new refresh token**.
  - Returns new access token in JSON, sets cookie again.

---

### **3️⃣ SecurityConfig**
- CORS setup:

  ```java
  config.setAllowCredentials(true);
  config.setAllowedOrigins(List.of(frontendUrl));
  config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
  config.setAllowedHeaders(List.of("*"));
  ```

- Stateless session policy.
- `/api/auth/**` endpoints are **public**.

---

### **4️⃣ JwtAuthenticationFilter**
- Extracts and validates JWT from `Authorization` header.
- Bypasses `/api/auth/**` routes so refresh/login work even with expired tokens:

  ```java
  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) {
      return request.getServletPath().startsWith("/api/auth");
  }
  ```

---

## ⚛️ Frontend Workflow (React + Axios)

### **1️⃣ Axios Configuration**

```ts
const api = axios.create({
  baseURL: import.meta.env.VITE_API_URL,
  withCredentials: true, // crucial for cookies
});

api.interceptors.request.use((config) => {
  const token = authService.getAccessToken();
  if (token) config.headers.Authorization = `Bearer ${token}`;
  return config;
});

api.interceptors.response.use(
  (res) => res,
  async (error) => {
    const originalRequest = error.config;
    if (
      error.response?.status === 401 &&
      !originalRequest._retry &&
      !originalRequest.url.includes("/auth/refresh") &&
      !originalRequest.url.includes("/auth/login")
    ) {
      originalRequest._retry = true;
      await authService.refreshToken();
      const newToken = authService.getAccessToken();
      if (newToken)
        originalRequest.headers.Authorization = `Bearer ${newToken}`;
      return api.request(originalRequest);
    }
    return Promise.reject(error);
  }
);
```

---

### **2️⃣ authService**

Handles:
- Token storage (access token in `localStorage`).
- Login, logout, refresh.

```ts
async refreshToken() {
  const res = await api.post<{ accessToken: string }>("/auth/refresh");
  if (res.data.accessToken) this.setAccessToken(res.data.accessToken);
}
```

---

### **3️⃣ AuthGuard**

Protects routes and refreshes token if needed before rendering.

```tsx
export default function AuthGuard({ children }) {
  const [status, setStatus] = useState<"checking" | "authenticated" | "unauthenticated">("checking");

  useEffect(() => {
    const token = authService.getAccessToken();
    if (token) {
      setStatus("authenticated");
    } else {
      authService.refreshToken()
        .then(() => setStatus("authenticated"))
        .catch(() => setStatus("unauthenticated"));
    }
  }, []);

  if (status === "checking") return null; // wait
  return status === "authenticated" ? children : <Navigate to="/login" replace />;
}
```

---

## ⚠️ Challenges Faced

| Issue | Cause | Fix |
|-------|--------|-----|
| Cookie not stored | Missing `withCredentials` or CORS misconfig | Enable `withCredentials` and `allowCredentials(true)` |
| Cookie disappeared on reload | Wrong `SameSite` or `Path` | Use `SameSite=Lax`, correct `path` |
| Refresh endpoint 401 | JWT filter blocking `/auth/refresh` | Skip `/api/auth/**` in filter |
| Guard redirecting too soon | Async refresh | Add `"checking"` state to wait before redirect |

---

## 💡 Lessons Learned

- Store **refresh tokens** in **HTTP-only cookies**, not in JS-accessible storage.  
- Always use `withCredentials: true` in frontend + `Access-Control-Allow-Credentials: true` in backend.  
- Exclude `/auth/**` from JWT filtering.  
- AuthGuard must wait for refresh before redirecting.  
- Proper `SameSite` and `path` values are essential for cookie persistence.  
- Industry-standard pattern:
  - **Access token:** short-lived, stored client-side.
  - **Refresh token:** long-lived, stored securely in cookie.
