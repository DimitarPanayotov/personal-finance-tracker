import { Link, useNavigate } from 'react-router-dom';

const Navbar = () => {
  const navigate = useNavigate();

  const handleLogout = () => {
    localStorage.removeItem('my_token');
    navigate('/login');
  };

  return (
    <nav style={{ 
      padding: '1rem', 
      borderBottom: '1px solid #ccc', 
      marginBottom: '2rem',
      display: 'flex',
      justifyContent: 'space-between',
      alignItems: 'center',
      backgroundColor: '#f8f9fa'
    }}>
      <div style={{ display: 'flex', gap: '20px' }}>
        {/* Links to the pages */}
        <Link to="/dashboard" style={{ textDecoration: 'none', fontWeight: 'bold', color: '#333' }}>Dashboard</Link>
        <Link to="/categories" style={{ textDecoration: 'none', fontWeight: 'bold', color: '#333' }}>Categories</Link>
        <Link to="/transactions" style={{ textDecoration: 'none', fontWeight: 'bold', color: '#333' }}>Transactions</Link>
        <Link to="/budgets" style={{ textDecoration: 'none', fontWeight: 'bold', color: '#333' }}>Budgets</Link>
      </div>
      
      <div style={{ display: 'flex', gap: '15px', alignItems: 'center' }}>
        <Link to="/profile" style={{ textDecoration: 'none', color: '#555' }}>Profile</Link>
        <button 
          onClick={handleLogout} 
          style={{ 
            margin: '15px',
            padding: '5px 15px', 
            backgroundColor: '#dc3545',
            color: 'white',
            border: 'none',
            borderRadius: '4px',
            cursor: 'pointer'
        }}
      >
        Logout
      </button>
      </div>
    </nav>
  );
};

export default Navbar;