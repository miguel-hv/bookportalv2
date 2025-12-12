import { useState } from "react";
import InputField from "../../auth/components/InputField"; // adjust path if needed
import type { AddBookRequest } from "../models/BookModel";

type CreateBookFormProps = {
  onSubmit: (data: AddBookRequest) => Promise<void>;
};

export default function CreateBookForm({ onSubmit }: CreateBookFormProps) {
  const [title, setTitle] = useState("");
  const [author, setAuthor] = useState("");
  const [review, setReview] = useState("");
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);
    setLoading(true);

    try {
      await onSubmit({ title, author, review });
      // optionally clear fields on success
      setTitle("");
      setAuthor("");
      setReview("");
    } catch (err: any) {
      setError(err.message || "Something went wrong");
    } finally {
      setLoading(false);
    }
  };

  return (
    <form
      onSubmit={handleSubmit}
      className="space-y-4 max-w-md mx-auto mt-10 p-6 border rounded-2xl shadow-md bg-white"
    >
      <h1 className="text-xl font-semibold mb-4 text-center">Add New Book</h1>

      <InputField
        label="Title"
        value={title}
        onChange={setTitle}
        required
      />

      <InputField
        label="Author"
        value={author}
        onChange={setAuthor}
        required
      />

      {/* Review text area */}
      <div className="space-y-1">
        <label className="block text-sm font-medium text-gray-700">
          Review
        </label>
        <textarea
          value={review}
          onChange={(e) => setReview(e.target.value)}
          required
          rows={4}
          className="w-full border rounded-lg px-3 py-2 focus:ring focus:ring-blue-300"
        />
      </div>

      {error && <p className="text-red-500 text-sm">{error}</p>}

      <button
        type="submit"
        disabled={loading}
        className="w-full bg-blue-600 text-white rounded-lg py-2 hover:bg-blue-700 disabled:opacity-50"
      >
        {loading ? "Saving…" : "Create Book"}
      </button>
    </form>
  );
}
