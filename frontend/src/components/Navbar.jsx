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
        <Link to="/categories" style={{ textDecoration: 'none', fontWeight: 'bold' }}>Categories</Link>
        <Link to="/transactions" style={{ textDecoration: 'none', fontWeight: 'bold', color: '#333' }}>Transactions</Link>
      </div>
      
      <button 
        onClick={handleLogout} 
        style={{ 
          padding: '5px 15px', 
          backgroundColor: '#dc3545', // Червен цвят
          color: 'white',
          border: 'none',
          borderRadius: '4px',
          cursor: 'pointer'
        }}
      >
        Logout
      </button>
    </nav>
  );
};

export default Navbar;