import { useState } from 'react';
import InputField from './InputField';
import type { RegisterUserRequest, UserRole } from '../../user/UserModel';

type RegisterFormProps = {
  onSubmit: (data: RegisterUserRequest) => Promise<void>;
};

export default function RegisterForm({ onSubmit }: RegisterFormProps) {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [role, setRole] = useState<UserRole>('USER');
  const [verifyPassword, setVerifyPassword] = useState('');
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);

    if (password !== verifyPassword) {
      setError('Passwords do not match');
      return;
    }

    setLoading(true);
    try {
      await onSubmit({ username, password, role });
    } catch (err: any) {
      setError(err.message || 'Something went wrong');
    } finally {
      setLoading(false);
    }
  };

  return (
    <form onSubmit={handleSubmit} className="space-y-4 max-w-sm mx-auto mt-10 p-6 border rounded-2xl shadow-md bg-white">
      <h1 className="text-xl font-semibold mb-4 text-center">Register</h1>

      <InputField label="Name" value={username} onChange={setUsername} required />
      <div>
        <label className="block text-sm font-medium mb-1">Role</label>
        <select
          value={role}
          onChange={(e) => setRole(e.target.value as UserRole)}
          className="w-full border rounded-lg px-3 py-2"
        >
          <option value="USER">User</option>
          <option value="ADMIN">Admin</option>
        </select>
      </div>
      <InputField label="Password" type="password" value={password} onChange={setPassword} required />
      <InputField label="Verify Password" type="password" value={verifyPassword} onChange={setVerifyPassword} required />

      {error && <p className="text-red-500 text-sm">{error}</p>}

      <button
        type="submit"
        disabled={loading}
        className="w-full bg-blue-600 text-white rounded-lg py-2 hover:bg-blue-700 disabled:opacity-50"
      >
        {loading ? 'Please wait…' : 'Sign Up'}
      </button>
    </form>
  );
}
