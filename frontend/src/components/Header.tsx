import { useNavigate } from "react-router-dom";
import { authService } from "../auth/authService";

export default function Header() {
  const navigate = useNavigate();

  const onLogout = () => {
    authService.logoutUser();
  }

  return (
    <header className="flex items-center justify-between bg-blue-400 text-white px-6 py-3 shadow-md">
      <ul className="flex gap-4">
        <li className="text-lg font-semibold cursor-pointer"
        onClick={() => navigate("/users")}
      >
        Users List
        </li>
        <li className="text-lg font-semibold cursor-pointer"
        onClick={() => navigate("/users")}
      >
        Books List

        </li>
      </ul>

      <div className="flex items-center gap-4">
        <button
          onClick={onLogout}
          className="bg-white text-blue-600 px-3 py-1 rounded-lg hover:bg-gray-100 transition"
        >
          Logout
        </button>
      </div>
    </header>
  );
}
