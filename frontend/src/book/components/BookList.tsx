import type { Book } from "../models/BookModel";

type Props = {
  userName?: string;
  books: Book[];
  onDelete?: (id: number) => void;
  onEdit?: (book: Book) => void;
};

export default function BookList({ userName, books, onDelete, onEdit }: Props) {
  return (
    <div className="p-6">
     {userName && (
        <h1 className="text-xl font-bold mb-4">
          Books by {userName}
        </h1>
      )}

      {books.length === 0 ? (
        <p>No books.</p>
      ) : (
        <ul className="space-y-3">
          {books.map((book) => (
            <li key={book.id} className="p-4 border rounded shadow-sm">
              <h2 className="font-semibold">{book.title}</h2>
              <p className="text-sm text-gray-600">Author: {book.author}</p>
              {book.review && (
                <p className="italic text-gray-500 mt-1">{book.review}</p>
              )}
              {
                onDelete &&
                <button
                  onClick={() => onDelete(book.id)}
                  className="px-3 py-1 bg-red-500 text-white rounded hover:bg-red-600"
                >
                  Delete
                </button>
              }
              {
                onEdit &&
                <button
                  onClick={() => onEdit(book)}
                  className="px-3 py-1 bg-blue-500 text-white rounded hover:bg-blue-600"
                >
                  Edit
                </button>
              }

            </li>
          ))}
        </ul>
      )}
    </div>
  );
}
