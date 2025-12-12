import { useNavigate } from "react-router-dom";
import { bookService } from "../book/bookService";
import type { AddBookRequest } from "../book/models/BookModel";
import { authService } from "../auth/authService";
import CreateBookForm from "../book/components/CreateBookForm";

export default function AddBookPage() {
  const navigate = useNavigate();
  const userId = authService.getUser()?.id;

  const handleCreate = async (data: AddBookRequest) => {
    if (userId) {
       await bookService.addBook(userId, data);
      navigate("/books");
      return;
    }

    alert("You must be logged in to create a book.");
    navigate("/login");
   
  };

  return (
    <div className="max-w-lg mx-auto">
      <h1 className="text-2xl font-semibold my-4 text-center">Create New Book</h1>
      <CreateBookForm onSubmit={handleCreate} />
    </div>
  );
}
