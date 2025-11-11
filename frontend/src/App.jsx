import {useState} from 'react';
// useState is a "Hook". Its how you store state(like from data) in a compoment
import axios from 'axios'
import './App.css'

//Functional component - a render method
function App() {
//'useState' returns the value and a function to update it
//its like a private field and its setter
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [token, setToken] = useState(null);
  const [error, setError] = useState(null);

//this function will handle the form submission
  const handleLogin = async (e) => {
    //prevents the browser from doing a full-page refresh
    e.preventDefault();
    setError(null); //clear previous errors

    try {
      //Make the POST request to the Spring Boot backend
      const response = await axios.post('http://localhost:8080/api/auth/login', {
        usernameOrEmail: username,
        password: password
      });

      //Log the response and save the token
      console.log('Login successful:', response.data);
      setToken(response.data.token);
    } catch (err) {
      console.error('Login failed:', err);
      setError('Login failed. Please check you creditentials.');
    }
  };


  //This is the UI. It "reacts" to the state changes.
  // If 'token' is set, it shows the success message.
  if (token) {
    return (
      <div>
        <h1>Login Successful!</h1>
        <p>Your Token: {token}</p>
      </div>
    );
  }

  //If no token, show the login form
  return (
    <div className="App">
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
}

export default App;
