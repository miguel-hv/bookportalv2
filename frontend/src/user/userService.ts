import api from "../lib/fetchWithAuth";
import type { User } from "./UserModel";

export async function fetchUsers(): Promise<User[]> {
  try {
    const response = await api.get<User[]>("/user/user-list");
    console.log(response.data)
    return response.data;
  } catch (error) {
    console.error("Error fetching users:", error);
    throw error;
  }
}

export async function fetchUserById(id: number): Promise<User>  {
  try {
    const res = await api.get(`/user/${id}`);
    return res.data;
  } catch (error) {
    console.error("Error fetching users:", error);
    throw error;
  }
}


export async function deleteUser(userId: number): Promise<null> {
  try {
    const response = await api.delete<null>(`/user/${userId}`);
    console.log(response.data)
    return response.data;
  } catch (error) {
    console.error("Error deleting user:", error);
    throw error;
  }
}
