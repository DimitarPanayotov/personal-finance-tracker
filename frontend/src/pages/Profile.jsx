import { useState, useEffect } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';
import Navbar from '../components/navbar';

const Profile = () => {
  const [user, setUser] = useState({ username: '', email: '' });
  const [passwords, setPasswords] = useState({ currentPassword: '', newPassword: '' });
  const [message, setMessage] = useState(null);
  const [error, setError] = useState(null);
  const navigate = useNavigate();

  useEffect(() => {
    const token = localStorage.getItem('my_token');
    if (!token) {
        navigate('/login');
        return;
    }

    axios.get('http://localhost:8080/api/users/me', {
        headers: { 'Authorization': `Bearer ${token}` }
    })
    .then(res => {
        setUser(res.data);
    })
    .catch(err => {
        console.error(err);
        if (err.response && err.response.status === 401) navigate('/login');
    });
  }, [navigate]);

  const handleUpdateProfile = async (e) => {
    e.preventDefault();
    setMessage(null); setError(null);
    const token = localStorage.getItem('my_token');

    try {
        const res = await axios.patch('http://localhost:8080/api/users/me', 
            { username: user.username, email: user.email },
            { headers: { 'Authorization': `Bearer ${token}` } }
        );
        setUser(res.data); 
        setMessage("The profile is updated!");
    } catch (err) {
        setError(err.response?.data?.message || "Error updating");
    }
  };

  const handleChangePassword = async (e) => {
    e.preventDefault();
    setMessage(null); setError(null);
    const token = localStorage.getItem('my_token');

    try {
        await axios.patch('http://localhost:8080/api/users/me/change-password', 
            { 
                password: passwords.currentPassword, 
                newPassword: passwords.newPassword 
            },
            { headers: { 'Authorization': `Bearer ${token}` } }
        );
        setMessage("Your password is changed!");
        setPasswords({ currentPassword: '', newPassword: '' });
    } catch (err) {
        setError(err.response?.data?.message || "Error changing the password.");
    }
  };

  const handleDeleteAccount = async () => {
    if (!window.confirm("Warning: This action will delete your account and all your data! Are you sure?")) return;
    
    const token = localStorage.getItem('my_token');
    try {
        await axios.delete('http://localhost:8080/api/users/me', {
            headers: { 'Authorization': `Bearer ${token}` }
        });
        localStorage.removeItem('my_token');
        navigate('/login');
    } catch (err) {
        alert("Error deleting account.");
    }
  };

  return (
    <div>
      <div style={{ maxWidth: '600px', margin: '0 auto' }}>
        <h1>My Profile</h1>

        {message && <p style={{ color: 'green', padding: '10px', backgroundColor: '#d4edda', borderRadius: '5px' }}>{message}</p>}
        {error && <p style={{ color: 'red', padding: '10px', backgroundColor: '#f8d7da', borderRadius: '5px' }}>{error}</p>}

        <div className="card" style={{ marginBottom: '2rem', textAlign: 'left' }}>
            <h3>Personal data</h3>
            <form onSubmit={handleUpdateProfile} style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
                <div>
                    <label>Username:</label>
                    <input 
                        type="text" 
                        value={user.username} 
                        onChange={e => setUser({...user, username: e.target.value})}
                        style={{ width: '100%', padding: '8px' }}
                    />
                </div>
                <div>
                    <label>Email:</label>
                    <input 
                        type="email" 
                        value={user.email} 
                        onChange={e => setUser({...user, email: e.target.value})}
                        style={{ width: '100%', padding: '8px' }}
                    />
                </div>
                <button type="submit" style={{ backgroundColor: '#4CAF50' }}>Save changes</button>
            </form>
        </div>
        <div className="card" style={{ marginBottom: '2rem', textAlign: 'left' }}>
            <h3>Change your password</h3>
            <form onSubmit={handleChangePassword} style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
                <div>
                    <label>Current password:</label>
                    <input 
                        type="password" 
                        value={passwords.currentPassword} 
                        onChange={e => setPasswords({...passwords, currentPassword: e.target.value})}
                        style={{ width: '100%', padding: '8px' }}
                    />
                </div>
                <div>
                    <label>New password:</label>
                    <input 
                        type="password" 
                        value={passwords.newPassword} 
                        onChange={e => setPasswords({...passwords, newPassword: e.target.value})}
                        style={{ width: '100%', padding: '8px' }}
                    />
                </div>
                <button type="submit" style={{ backgroundColor: '#008CBA' }}>Change password</button>
            </form>
        </div>
        <div className="card" style={{ textAlign: 'left', border: '1px solid #fefefeff' }}>
            <h3 style={{ color: '#ff4444' }}>Warning!</h3>
            <p>Personal data is deleted permanently after deleting account!</p>
            <button onClick={handleDeleteAccount} style={{ backgroundColor: '#ff4444', width: '100%' }}>
                Delete account
            </button>
        </div>
      </div>
    </div>
  );
};

export default Profile;