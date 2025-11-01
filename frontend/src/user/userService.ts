import api from "../lib/fetchWithAuth";
import type { User } from "./UserModel";

export async function fetchUsers(): Promise<User[]> {
  try {
    const response = await api.get<User[]>("/user/user-list");
    return response.data;
  } catch (error) {
    console.error("Error fetching users:", error);
    throw error;
  }
}
