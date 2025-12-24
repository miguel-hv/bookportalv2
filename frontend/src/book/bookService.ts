import api from "../lib/fetchWithAuth";
import type { AddBookRequest, Book, EditBookRequest } from "./models/BookModel";

export const bookService = {

    async fetchAllBooks(): Promise<Book[]>  {
        try {
            const res = await api.get(`/books`);
            return res.data;
        } catch (error) {
            console.error("Error fetching users:", error);
            throw error;
        }
    },

    async addBook(userId: number, data: AddBookRequest): Promise<Book> {
        try {
            const res = await api.post<Book>(`/user/${userId}/books`, data);
            return res.data;
        } catch (error: any) {
            throw new Error(error.response?.data?.message || "Failed to register user");
        }
    },

    async fetchUserBooks(userId: number): Promise<Book[]>  {
        try {
            const res = await api.get(`/user/${userId}/books`);
            return res.data;
        } catch (error) {
            console.error("Error fetching users:", error);
            throw error;
        }
    },

    async deleteBook(bookId: number): Promise<null> {
        try {
            const response = await api.delete<null>(`/books/${bookId}`);
            console.log(response.data)
            return response.data;
        } catch (error) {
            console.error("Error deleting book:", error);
            throw error;
        }
    },

    async editBook(bookId: number, book: EditBookRequest): Promise<Book> {

        try {
            const response = await api.patch<Book>(`/books/${bookId}`, book);
            console.log(response)
            return response.data;
        } catch (error) {
            console.error("Error editing book:", error);
            throw error;
        }
    }
}