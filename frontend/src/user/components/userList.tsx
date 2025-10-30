'use client';

import { useEffect, useState } from 'react';
import type { User } from '../UserModel';
import { fetchUsers } from '../userService';

export default function UserList () {
    const [users, setUsers] = useState<User[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const loadUsers = async () => {
      try {
        const data = await fetchUsers();
        setUsers(data);
      } catch (err: any) {
        setError(err.message);
      } finally {
        setLoading(false);
      }
    };

    loadUsers();
  }, []);

  if (loading) return <p>Loading users...</p>;
  if (error) return <p className="text-red-500">Error: {error}</p>;

  return (
    <div className="p-6">
      <h1 className="text-2xl font-bold mb-4">User List</h1>
      <ul className="space-y-2">
        {users.map((user) => (
          <li key={user.id} className="p-3 border rounded-lg shadow-sm">
            <p className="font-medium">{user.username}</p>
            <p className="text-sm text-gray-500">{user.role}</p>
          </li>
        ))}
      </ul>
    </div>
  );
}