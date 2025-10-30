import { useNavigate } from "react-router-dom";
import type { RegisterUserRequest } from "../user/UserModel";
import RegisterForm from "../auth/components/RegisterForm";
import { authService } from "../auth/authService";

export default function RegisterPage() {
    const navigate = useNavigate();
  const handleRegister = async (data: RegisterUserRequest) => {
    await authService.registerUser(data);
    navigate('/login');
  };

  return <RegisterForm onSubmit={handleRegister} />;
}
