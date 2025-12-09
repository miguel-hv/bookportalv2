import type { Book } from "../book/models/BookModel";

export type UserRole = "ROLE_USER" | "ROLE_ADMIN";
export type UserRoleRegister = "USER" | "ADMIN";

export type User = {
  id: number;
  username: string;
  role: UserRole[];
  books: Book[];
};

export type RegisterUserRequest = {
  username: string;
  password: string;
  role: UserRoleRegister;
};

export type LoginUserRequest = {
  username: string;
  password: string;
};