
export type UserRole = 'USER' | 'ADMIN';

export type User = {
  id: number;
  username: string;
  role: UserRole;
   accessToken?: string;
};

export type RegisterUserRequest = {
  username: string;
  password: string;
  role: UserRole;
};

export type LoginUserRequest = {
  username: string;
  password: string;
};