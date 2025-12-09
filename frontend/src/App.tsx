import { Routes, Route, Outlet, Navigate } from "react-router-dom";
import './App.css'
import RegisterPage from "./pages/RegisterPage";
import LoginPage from "./pages/LoginPage";
import AuthGuard from "./auth/authGuard";
import UserList from "./user/components/userList";
import Header from "./components/Header";
import BookList from "./book/components/BookList";

function Layout() {
  return (
    <>
      <Header />
      <Outlet />
    </>
  );
}

function App() {
  return (
      <Routes>
        <Route path="/login" element={<LoginPage />} />
        <Route path="/register" element={<RegisterPage />} />
        <Route element={<Layout />}>
          <Route path="/" element={<Navigate to="/users" replace />} />
          <Route path="/users" element={
            <AuthGuard>
              <UserList />
            </AuthGuard>
          } />
          <Route path="/users/:userId/books" element={
            <AuthGuard>
              <BookList />
            </AuthGuard>
          } />
      
        </Route>
      </Routes>
  );
}

export default App
