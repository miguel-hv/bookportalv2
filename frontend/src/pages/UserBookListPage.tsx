// pages/BookListPage.tsx
import { useEffect, useState } from "react";
import { bookService } from "../book/bookService";
import { authService } from "../auth/authService";
import type { Book } from "../book/models/BookModel";
import BookList from "../book/components/BookList";

export default function UserBookListPage() {
  const user = authService.getUser();
  const [bookList, setBookList] = useState<Book[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const load = async () => {
      try {
        const data = await bookService.fetchAllBooks();
        setBookList(data);
      } catch (err: any) {
        setError(err.message);
      } finally {
        setLoading(false);
      }
    };
    load();
  }, [user?.id]);

  if (!user) return <p>Please log in to view your books.</p>;
  if (loading) return <p>Loading books...</p>;
  if (error) return <p className="text-red-500">{error}</p>;

  return <BookList userName={user.username} books={bookList} />;
}
