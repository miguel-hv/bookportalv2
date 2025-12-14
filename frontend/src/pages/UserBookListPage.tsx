// pages/BookListPage.tsx
import { useEffect, useState } from "react";
import { bookService } from "../book/bookService";
import type { Book } from "../book/models/BookModel";
import BookList from "../book/components/BookList";
import { useLocation, useNavigate } from "react-router-dom";

export default function UserBookListPage() {
  const navigate = useNavigate();
  const  { state } = useLocation();
  const user = state?.user;
  const userId = user?.id;
  const [bookList, setBookList] = useState<Book[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const load = async () => {
      try {
        if (userId) {
          const data = await bookService.fetchUserBooks(userId);
          setBookList(data);
          return;
        }
        alert("You must be logged in to create a book.");
        navigate("/login");
      } catch (err: any) {
        setError(err.message);
      } finally {
        setLoading(false);
      }
    };
    load();
  }, [userId]);

  if (!user) return <p>Please log in to view your books.</p>;
  if (loading) return <p>Loading books...</p>;
  if (error) return <p className="text-red-500">{error}</p>;

  return <BookList userName={user.username} books={bookList} />;
}
