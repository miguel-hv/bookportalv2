import { useEffect, useState } from "react";
import type { Book } from "../models/BookModel";
import { bookService } from "../bookService";

type Props = {
  userName?: string;
  books: Book[];
};

export default function BookList({ userName, books }: Props) {
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
            </li>
          ))}
        </ul>
      )}
    </div>
  );
}
