import { useEffect, useState } from "react";
import { bookService } from "../book/bookService";
import type { Book } from "../book/models/BookModel";
import BookList from "../book/components/BookList";

export default function BookListPage() {
  const [bookList, setBookList] = useState<Book[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const handleDelete = async (id: number) => {
    if (!confirm("Are you sure you want to delete this user?")) return;

    try {
      await bookService.deleteBook(id);

      setBookList((prev) => prev.filter((b) => b.id !== id));
    } catch (err: any) {
      alert(err.message);
    }
  };

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
  }, []);

  if (loading) return <p>Loading books...</p>;
  if (error) return <p className="text-red-500">{error}</p>;

  return <BookList books={bookList} onDelete={handleDelete} />;
}
