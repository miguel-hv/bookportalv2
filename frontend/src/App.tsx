import { Routes, Route } from "react-router-dom";
import './App.css'
import RegisterPage from "./pages/RegisterPage";
import LoginPage from "./pages/LoginPage";

function App() {
  return (
      <Routes>
        <Route path="/login" element={<LoginPage />} />
        <Route path="/register" element={<RegisterPage />} />
        {/* <Route
          path="/users"
          element={
            <AuthGuard>
              <UserList />
            </AuthGuard>
          }
        /> */}
      </Routes>
  );
}

export default App
