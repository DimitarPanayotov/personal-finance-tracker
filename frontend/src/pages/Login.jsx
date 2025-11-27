import { useState } from 'react';
import axios from 'axios'; 
import { useNavigate, Link } from 'react-router-dom';

const Login = () => {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState(null);
  
  const navigate = useNavigate();

  const handleLogin = async (e) => {
    e.preventDefault();
    setError(null);

    try {
      const response = await axios.post('http://localhost:8080/api/auth/login', {
        usernameOrEmail: username,
        password: password
      });

      const token = response.data.token;

      localStorage.setItem('my_token', token);

      navigate('/dashboard');
      
    } catch (err) {
      console.error(err);
      setError('Error! Check creditentials.');
    }
  };

  return (
    <div className="auth-container">
      <div className="card" style={{ textAlign: 'center' }}>
        <h2>Login</h2>
        
        <form onSubmit={handleLogin}>
          <div style={{ 
            display: 'flex', 
            justifyContent: 'center', 
            alignItems: 'center', 
            marginBottom: '1rem' 
          }}>
            <label style={{ width: '80px', textAlign: 'right', marginRight: '10px', fontWeight: 'bold' }}>
              Username:
            </label>
            <input 
              type="text" 
              value={username} 
              onChange={(e) => setUsername(e.target.value)} 
              style={{ width: '200px', padding: '5px' }}
            />
          </div>

          <div style={{ 
            display: 'flex', 
            justifyContent: 'center', 
            alignItems: 'center', 
            marginBottom: '1rem' 
          }}>
            <label style={{ width: '80px', textAlign: 'right', marginRight: '10px', fontWeight: 'bold' }}>
              Password:
            </label>
            <input 
              type="password" 
              value={password} 
              onChange={(e) => setPassword(e.target.value)} 
              style={{ width: '200px', padding: '5px' }}
            />
          </div>

          <button type="submit" style={{ cursor: 'pointer', padding: '8px 20px' }}>
            Login
          </button>
          
          {error && <p style={{color: 'red', marginTop: '10px'}}>{error}</p>}
        </form>

        <p style={{ marginTop: '1rem', fontSize: '0.9rem' }}>
          You don't have an account? <Link to="/register">Sign in</Link>
        </p>
      </div>
    </div>
  );
};

export default Login;