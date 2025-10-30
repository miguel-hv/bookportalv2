import axios from "axios";
import { authService } from "../auth/authService";

const api = axios.create({
  baseURL: "http://localhost:4000",
  withCredentials: true,
});

api.interceptors.request.use((config) => {
  const token = authService.getAccessToken();
  if (token) config.headers.Authorization = `Bearer ${token}`;
  return config;
});

api.interceptors.response.use(
  (res) => res,
  async (error) => {
    if (error.response?.status === 401 && !error.config._retry) {
      error.config._retry = true;
      try {
        await authService.refreshToken();
        const newToken = authService.getAccessToken();
        if (newToken) error.config.headers.Authorization = `Bearer ${newToken}`;
        return api.request(error.config);
      } catch {
        authService.logoutUser();
        window.location.href = "/login";
      }
    }
    return Promise.reject(error);
  }
);

export default api;
