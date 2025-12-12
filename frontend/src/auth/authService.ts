import type { LoginUserRequest, RegisterUserRequest, User } from "../user/UserModel";
import api from "../lib/fetchWithAuth";
import type { LoginResponse } from "./models/LoginResponse";

let accessToken: string | null = null;

export const authService = {
  getAccessToken(): string | null {
    return accessToken ?? localStorage.getItem("accessToken");
  },

  setAccessToken(token: string) {
    localStorage.setItem("accessToken", token);
  },

  clearAccessToken() {
    accessToken = null;
    localStorage.removeItem("accessToken");
    localStorage.removeItem("userId");
  },

  /** 
   * TEMPORARY: Using localStorage as a user store.
   * TODO: Replace with an actual in-app state store (Zustand/Context/Redux).
   */
  setUser(user: User) {
    localStorage.setItem("user", JSON.stringify(user));
  },

  getUser(): User | null {
    return JSON.parse(localStorage.getItem("user") ?? '');
  },

  async registerUser(data: RegisterUserRequest): Promise<User> {
    try {
      const res = await api.post<User>("/auth/register", data);
      return res.data;
    } catch (error: any) {
      throw new Error(error.response?.data?.message || "Failed to register user");
    }
  },

  async loginUser(data: LoginUserRequest): Promise<LoginResponse> {
    try {
      const res = await api.post<LoginResponse>("/auth/login", data);
      if (res.data.accessToken) this.setAccessToken(res.data.accessToken);
      if (res.data.user) this.setUser(res.data.user);
      return res.data;
    } catch (error: any) {
      throw new Error(error.response?.data?.message || "Invalid credentials");
    }
  },

  async logoutUser(): Promise<void> {
    try {
      await api.post("/auth/logout");
    } catch (error: any) {
      console.error("Logout failed", error);
    } finally {
      this.clearAccessToken();
        window.location.href = "/login";
    }
  },

  async refreshToken(): Promise<void> {
   try {
    const res = await api.post<{ accessToken: string }>("/auth/refresh", undefined, { headers: { Authorization: "" } });
    if (res.data.accessToken) this.setAccessToken(res.data.accessToken);
    } catch (error) {
      this.clearAccessToken();
      throw error;
    }
  },
};
