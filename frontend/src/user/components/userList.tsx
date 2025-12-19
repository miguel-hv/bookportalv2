import { useEffect, useState } from 'react';
import type { User } from '../UserModel';
import { deleteUser, fetchUsers } from '../userService';
import { Link } from 'react-router-dom';

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

  const handleDelete = async (id: number) => {
    if (!confirm("Are you sure you want to delete this user?")) return;

    try {
      await deleteUser(id);

      // Remove deleted user from the list
      setUsers((prev) => prev.filter((u) => u.id !== id));
    } catch (err: any) {
      alert(err.message);
    }
  };

  if (loading) return <p>Loading users...</p>;
  if (error) return <p className="text-red-500">Error: {error}</p>;

  return (
    <div className="p-6">
      <h1 className="text-2xl font-bold mb-4">User List</h1>
      <ul className="space-y-2">
        {users.map((user) => (
          <li key={user.id} className="p-3 border rounded-lg shadow-sm flex justify-between">
            <div className='flex gap-4 items-center'>
              <p className="font-medium">{user.username}</p>
              <p className="text-sm text-gray-500">{user.role.join(", ")}</p>
            </div>
            <Link
              to={`/users/${user.id}/books`}
              state={{ user }}
              className="px-3 py-1 bg-blue-500 text-white rounded hover:bg-blue-600"
            >
              View Books
            </Link>
            <button
              onClick={() => handleDelete(user.id)}
              className="px-3 py-1 bg-red-500 text-white rounded hover:bg-red-600"
            >
              Delete
            </button>
          </li>
        ))}
      </ul>
    </div>
  );
}