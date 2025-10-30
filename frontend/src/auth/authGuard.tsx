import { Navigate } from "react-router-dom";
import { authService } from "./authService";
import type { JSX } from "react";

export default function AuthGuard({ children }: { children: JSX.Element }) {
  const token = authService.getAccessToken();
  return token ? children : <Navigate to="/login" />;
}
