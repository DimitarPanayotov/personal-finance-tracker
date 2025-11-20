import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import Login from './pages/Login';
import Dashboard from './pages/Dashboard';
import Categories from './pages/Categories';
import CreateCategory from './pages/CreateCategory';
import Transactions from './pages/Transactions';
import CreateTransaction from './pages/CreateTransaction';
import './App.css';

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Navigate to="/login" />} />
        
        <Route path="/login" element={<Login />} />
        <Route path="/dashboard" element={<Dashboard />} />

        <Route path="/categories" element={<Categories />} />
        <Route path="/categories/new" element={<CreateCategory />} />

        <Route path="/transactions" element={<Transactions />} />
        <Route path="/transactions/new" element={<CreateTransaction />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;
