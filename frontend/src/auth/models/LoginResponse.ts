import type { User } from "../../user/UserModel";

export type LoginResponse = {
  accessToken: string;
  user: User
};
