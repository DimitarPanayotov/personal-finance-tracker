import { useState } from 'react';
import axios from 'axios';
import { useNavigate, Link } from 'react-router-dom';

const Register = () => {
  const [username, setUsername] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState(null);

  const navigate = useNavigate();

  const handleRegister = async (e) => {
    e.preventDefault();
    setError(null);

    try {
      const response = await axios.post('http://localhost:8080/api/auth/register', {
        username,
        email,
        password
      });

      const token = response.data.token;
      localStorage.setItem('my_token', token);

      navigate('/dashboard');
      
    } catch (err) {
      console.error(err);
      setError(err.response?.data?.message || 'Error signing in.');
    }
  };

  return (
    <div className="auth-container">
      <div className="card" style={{ textAlign: 'center' }}>
        <h2>Sign Up</h2>
        
        {error && <p style={{color: 'red', marginBottom: '1rem'}}>{error}</p>}
        
        <form onSubmit={handleRegister}>
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
              placeholder="Choose username"
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
              Email:
            </label>
            <input
              type="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              placeholder="example@mail.com"
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
              placeholder="At least 6 chars"
              style={{ width: '200px', padding: '5px' }}
            />
          </div>

          <button type="submit" style={{ 
            marginTop: '10px', 
            backgroundColor: '#4CAF50', 
            color: 'white', 
            border: 'none', 
            padding: '8px 20px', 
            cursor: 'pointer', 
            borderRadius: '4px' 
          }}>
            Register
          </button>
        </form>

        <p style={{ marginTop: '1rem', fontSize: '0.9rem' }}>
          Already have an account? <Link to="/login">Log in here</Link>
        </p>
      </div>
    </div>
  );
};

export default Register;