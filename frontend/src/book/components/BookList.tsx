'use client'; // (not needed in plain React, you can remove it)

import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import type { User } from "../../user/UserModel";
import { fetchUserById } from "../../user/userService";

export default function BookList() {
  const { userId } = useParams();
  const [user, setUser] = useState<User | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const load = async () => {
      try {
        const data = await fetchUserById(Number(userId));
        setUser(data);
      } catch (err: any) {
        setError(err.message);
      } finally {
        setLoading(false);
      }
    };
    load();
  }, [userId]);

  if (loading) return <p>Loading books...</p>;
  if (error) return <p className="text-red-500">{error}</p>;
  if (!user) return <p>User not found.</p>;

  return (
    <div className="p-6">
      <h1 className="text-xl font-bold mb-4">
        Books by {user.username}
      </h1>

      {user.books.length === 0 ? (
        <p>No books.</p>
      ) : (
        <ul className="space-y-3">
          {user.books.map((book) => (
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
