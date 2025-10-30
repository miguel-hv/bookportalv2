import type { LoginUserRequest, RegisterUserRequest, User } from "../user/UserModel";
import api from "../lib/fetchWithAuth";

let accessToken: string | null = null;

export const authService = {
  getAccessToken(): string | null {
    return accessToken ?? localStorage.getItem("accessToken");
  },

  setAccessToken(token: string) {
    accessToken = token;
    localStorage.setItem("accessToken", token);
  },

  clearAccessToken() {
    accessToken = null;
    localStorage.removeItem("accessToken");
  },

  async registerUser(data: RegisterUserRequest): Promise<User> {
    try {
      const res = await api.post<User>("/register", data);
      return res.data;
    } catch (error: any) {
      throw new Error(error.response?.data?.message || "Failed to register user");
    }
  },

  async loginUser(data: LoginUserRequest): Promise<User> {
    try {
      const res = await api.post<User>("/login", data);
      if (res.data.accessToken) this.setAccessToken(res.data.accessToken);
      return res.data;
    } catch (error: any) {
      throw new Error(error.response?.data?.message || "Invalid credentials");
    }
  },

  async logoutUser(): Promise<void> {
    try {
      await api.post("/logout");
    } catch (error: any) {
      console.error("Logout failed", error);
    } finally {
      this.clearAccessToken();
    }
  },

  async refreshToken(): Promise<void> {
    try {
      const res = await api.post<{ accessToken: string }>("/refresh");
      if (res.data.accessToken) this.setAccessToken(res.data.accessToken);
    } catch (error) {
      this.clearAccessToken();
      throw error;
    }
  },
};
