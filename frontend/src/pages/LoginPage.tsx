// src/pages/Login.tsx
import { Link, useNavigate } from "react-router-dom";
import LoginForm from "../auth/components/LoginForm";
import { authService } from "../auth/authService";

export default function LoginPage() {
  const navigate = useNavigate();

  const handleLogin = async (data: { username: string; password: string }) => {
    await authService.loginUser(data);
    navigate("/users");
  };

  return (
    <>
      <LoginForm onSubmit={handleLogin} />
       <div className="text-center text-sm text-gray-600">
          Don’t have an account?{' '}
          <Link
            to="/register"
            className="text-blue-600 hover:text-blue-800 font-medium transition-colors"
          >
            Register here
          </Link>
        </div>
    </>
  );
}
