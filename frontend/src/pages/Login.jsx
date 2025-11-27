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
    <div className="card">
      <h2>Login</h2>
      <form onSubmit={handleLogin}>
        <div>
          <label>Username:</label>
          <input type="text" value={username} onChange={(e) => setUsername(e.target.value)} />
        </div>
        <div>
          <label style={{marginLeft: '10px'}}>Password:</label>
          <input type="password" value={password} onChange={(e) => setPassword(e.target.value)} />
        </div>
        <button type="submit">Login</button>
        {error && <p style={{color: 'red'}}>{error}</p>}
      </form>
      <p style={{ marginTop: '1rem', fontSize: '0.9rem' }}>
      You don't have an account? <Link to="/register">Sign in</Link>
      </p>
    </div>
  );
};

export default Login;