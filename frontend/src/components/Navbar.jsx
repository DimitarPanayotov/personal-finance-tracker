import { Link, useNavigate } from 'react-router-dom';
import './Navbar.css'

const Navbar = () => {
  const navigate = useNavigate();

  const handleLogout = () => {
    localStorage.removeItem('my_token');
    navigate('/login');
  };

  return (
    <nav className='navbar'>
      <div className='nav-links'>
        <Link to="/dashboard" className="nav-link">Dashboard</Link>
        <Link to="/categories" className="nav-link">Categories</Link>
        <Link to="/transactions" className="nav-link">Transactions</Link>
        <Link to="/budgets" className="nav-link">Budgets</Link>
      </div>
      
      <div className='nav-actions'>
        <Link to="/profile" className="nav-link">Profile</Link>
        <button onClick={handleLogout} className="btn-logout">
        Logout
      </button>
      </div>
    </nav>
  );
};

export default Navbar;