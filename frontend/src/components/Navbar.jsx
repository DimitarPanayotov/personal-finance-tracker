import { Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

const Navbar = () => {
  const { logout, user } = useAuth();

  return (
    <nav style={{ 
      padding: '1rem', 
      borderBottom: '1px solid #ccc', 
      marginBottom: '2rem',
      display: 'flex',
      justifyContent: 'space-between',
      alignItems: 'center'
    }}>
      <div style={{ display: 'flex', gap: '20px' }}>
        <Link to="/dashboard" style={{ fontWeight: 'bold' }}>Dashboard</Link>
        <Link to="/categories">Categories</Link>
        {/* По-късно ще добавим Transactions и Budgets */}
      </div>
      
      <div style={{ display: 'flex', alignItems: 'center', gap: '15px' }}>
        <span>Hello!</span>
        <button onClick={logout} style={{ padding: '5px 10px', fontSize: '0.9rem' }}>
          Logout
        </button>
      </div>
    </nav>
  );
};

export default Navbar;