import { Navigate } from "react-router-dom";
import { authService } from "./authService";
import { useEffect, useState, type JSX } from "react";

export default function AuthGuard({ children }: { children: JSX.Element }) {
    const [status, setStatus] = useState<"checking" | "authenticated" | "unauthenticated">("checking");

  useEffect(() => {
    const token = authService.getAccessToken();
    if (token) {
      setStatus("authenticated");
      return;
    }

    authService.refreshToken()
      .then(() => {
        if (authService.getAccessToken()) {
          setStatus("authenticated");
        } else {
          setStatus("unauthenticated");
        }
      })
      .catch(() => setStatus("unauthenticated"));
  }, []);

 if (status === "checking") {
    return null;
  }

  
  if (status === "authenticated") {
    return children;
  }
  
  return <Navigate to="/login" replace />;
}
