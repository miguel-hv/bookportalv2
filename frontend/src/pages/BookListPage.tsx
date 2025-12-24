import { useEffect, useState } from "react";
import { bookService } from "../book/bookService";
import type { Book } from "../book/models/BookModel";
import BookList from "../book/components/BookList";
import EditBookForm from "../book/components/EditBookForm";

export default function BookListPage() {
  const [bookList, setBookList] = useState<Book[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [editingBook, setEditingBook] = useState<Book | null>(null);
  const [isEditOpen, setIsEditOpen] = useState(false);

  const handleDelete = async (id: number) => {
    if (!confirm("Are you sure you want to delete this book?")) return;

    try {
      await bookService.deleteBook(id);

      setBookList((prev) => prev.filter((b) => b.id !== id));
    } catch (err: any) {
      alert(err.message);
    }
  };

  const handleEdit = (book: Book) => {
    setEditingBook(book);
    setIsEditOpen(true);
  };

  const handleEditSubmit = async (updatedData: Partial<Book>) => {
    if (!editingBook) return;

    try {
      const updatedBook = await bookService.editBook(
        editingBook.id,
        updatedData
      );

      setBookList((list) =>
        list.map((b) => (b.id === updatedBook.id ? updatedBook : b))
      );

      setIsEditOpen(false);
      setEditingBook(null);
    } catch (err: any) {
      alert(err.message);
    }
  };

  const handleEditCancel = () => {
    setIsEditOpen(false);
    setEditingBook(null);
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

  return (
  <>
    <BookList
      books={bookList}
      onDelete={handleDelete}
      onEdit={handleEdit}
    />

    {isEditOpen && editingBook && (
      <div className="fixed inset-0 bg-black/50 flex items-center justify-center">
        <EditBookForm
          book={editingBook}
          onSubmit={handleEditSubmit}
          onCancel={handleEditCancel}
        />
      </div>
    )}
  </>
);

}
