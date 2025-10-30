import { useState } from 'react';
import InputField from './InputField';
import type { LoginUserRequest } from '../../user/UserModel';

type LoginFormProps = {
  onSubmit: (data: LoginUserRequest) => Promise<void>;
};

export default function LoginForm({ onSubmit }: LoginFormProps) {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);
    setLoading(true);

    try {
      await onSubmit({ username, password });
    } catch (err: any) {
      setError(err.message || 'Something went wrong');
    } finally {
      setLoading(false);
    }
  };

  return (
    <form onSubmit={handleSubmit} className="space-y-4 max-w-sm mx-auto mt-10 p-6 border rounded-2xl shadow-md bg-white">
      <h1 className="text-xl font-semibold mb-4 text-center">Login</h1>

      <InputField label="Name" value={username} onChange={setUsername} required />
      <InputField label="Password" type="password" value={password} onChange={setPassword} required />

      {error && <p className="text-red-500 text-sm">{error}</p>}

      <button
        type="submit"
        disabled={loading}
        className="w-full bg-blue-600 text-white rounded-lg py-2 hover:bg-blue-700 disabled:opacity-50"
      >
        {loading ? 'Please wait…' : 'Sign In'}
      </button>
    </form>
  );
}
