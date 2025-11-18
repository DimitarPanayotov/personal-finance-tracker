import { useState } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom'; 

const Login = () => {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState(null);
  const navigate = useNavigate(); // Navigation hook

  const handleLogin = async (e) => {
    e.preventDefault();
    setError(null);

    try {
      const response = await axios.post('http://localhost:8080/api/auth/login', {
        usernameOrEmail: username,
        password: password
      });

      console.log('Login successful:', response.data);
      
      // Will add AuthContex later to save the token
      localStorage.setItem('token', response.data.token);
      
      // Navigating to the dashboard
      navigate('/dashboard');
      
    } catch (err) {
      console.error('Login failed:', err);
      setError('Login failed. Please check your credentials.');
    }
  };

  return (
    <div className="card"> {/* App.css style */}
      <form onSubmit={handleLogin}>
        <h1>Login</h1>
        <div>
          <label>Username:</label>
          <input
            type="text"
            value={username}
            onChange={(e) => setUsername(e.target.value)}
          />
        </div>
        <div>
          <label>Password:</label>
          <input 
            type="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
          />
        </div>
        <button type="submit">Login</button>
        {error && <p style={{color: 'red'}}>{error}</p>}
      </form>
    </div>
  );
};

export default Login;