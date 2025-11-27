import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import Login from './pages/Login';
import Dashboard from './pages/Dashboard';
import Categories from './pages/Categories';
import CreateCategory from './pages/CreateCategory';
import Transactions from './pages/Transactions';
import CreateTransaction from './pages/CreateTransaction';
import Budgets from './pages/Budgets';
import CreateBudget from './pages/CreateBudget';
import Profile from './pages/Profile';
import Register from './pages/Register';
import Layout from './components/Layout';
import './App.css';

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Navigate to="/login" />} />
        <Route path="/register" element={<Register />} />
        <Route path="/login" element={<Login />} />

        <Route element={<Layout />}>
          <Route path="/dashboard" element={<Dashboard />} />

          <Route path="/categories" element={<Categories />} />
          <Route path="/categories/new" element={<CreateCategory />} />

          <Route path="/transactions" element={<Transactions />} />
          <Route path="/transactions/new" element={<CreateTransaction />} />

          <Route path="/budgets" element={<Budgets />} />
          <Route path="/budgets/new" element={<CreateBudget />} />

          <Route path="/profile" element={<Profile />} />
        </Route>
      </Routes>
    </BrowserRouter>
  );
}

export default App;
