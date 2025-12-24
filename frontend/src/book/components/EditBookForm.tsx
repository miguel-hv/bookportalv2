import { useState } from "react";
import InputField from "../../auth/components/InputField";
import type { Book } from "../models/BookModel";

type EditBookFormProps = {
  book: Book;
  onSubmit: (data: Partial<Book>) => Promise<void>;
  onCancel: () => void;
};

export default function EditBookForm({
  book,
  onSubmit,
  onCancel,
}: EditBookFormProps) {
  const [title, setTitle] = useState(book.title ?? "");
  const [author, setAuthor] = useState(book.author ?? "");
  const [review, setReview] = useState(book.review ?? "");
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);
    setLoading(true);

    try {
      const updatedData: Partial<Book> = {};

      if (title !== book.title) updatedData.title = title;
      if (author !== book.author) updatedData.author = author;
      if (review !== book.review) updatedData.review = review;

      await onSubmit(updatedData);
    } catch (err: any) {
      setError(err.message || "Something went wrong");
    } finally {
      setLoading(false);
    }
  };

  return (
    <form
      onSubmit={handleSubmit}
      className="space-y-4 max-w-md w-full p-6 border rounded-2xl shadow-md bg-white"
    >
      <h1 className="text-xl font-semibold mb-4 text-center">
        Edit Book
      </h1>

      <InputField
        label="Title"
        value={title}
        onChange={setTitle}
      />

      <InputField
        label="Author"
        value={author}
        onChange={setAuthor}
      />

      <div className="space-y-1">
        <label className="block text-sm font-medium text-gray-700">
          Review
        </label>
        <textarea
          value={review}
          onChange={(e) => setReview(e.target.value)}
          rows={4}
          className="w-full border rounded-lg px-3 py-2 focus:ring focus:ring-blue-300"
        />
      </div>

      {error && <p className="text-red-500 text-sm">{error}</p>}

      <div className="flex gap-3">
        <button
          type="submit"
          disabled={loading}
          className="flex-1 bg-blue-600 text-white rounded-lg py-2 hover:bg-blue-700 disabled:opacity-50"
        >
          {loading ? "Saving…" : "Save Changes"}
        </button>

        <button
          type="button"
          onClick={onCancel}
          className="flex-1 bg-gray-200 text-gray-800 rounded-lg py-2 hover:bg-gray-300"
        >
          Cancel
        </button>
      </div>
    </form>
  );
}
