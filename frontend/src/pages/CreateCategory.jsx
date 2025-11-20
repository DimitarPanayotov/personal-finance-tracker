import { useState } from 'react';
import axios from 'axios'; 
import { useNavigate } from 'react-router-dom';

const CreateCategory = () => {
  const [name, setName] = useState('');
  const [type, setType] = useState('EXPENSE');
  const [color, setColor] = useState('#000000');
  const [error, setError] = useState(null);
  
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError(null);

    const token = localStorage.getItem('my_token');

    if (!token) {
        navigate('/login');
        return;
    }

    const categoryData = {
      name: name,
      type: type,
      color: color
    };

    try {
      await axios.post('http://localhost:8080/api/categories', categoryData, {
        headers: {
            'Authorization': `Bearer ${token}`
        }
      });
      
      navigate('/categories');
      
    } catch (err) {
      console.error('Error creating category:', err);
      
      if (err.response && err.response.status === 401) {
          navigate('/login');
          return;
      }

      setError(err.response?.data?.message || 'Unsuccessful creation of category.');
    }
  };

  return (
    <div className="card">
      <h2>New Category</h2>
      {error && <p style={{ color: 'red' }}>{error}</p>}
      
      <form onSubmit={handleSubmit} style={{ display: 'flex', flexDirection: 'column', gap: '1rem', maxWidth: '400px', margin: '0 auto' }}>
        
        <div style={{ textAlign: 'left' }}>
          <label style={{ display: 'block', marginBottom: '5px' }}>Name:</label>
          <input
            type="text"
            value={name}
            onChange={(e) => setName(e.target.value)}
            required
            placeholder="e.g. Groceries"
            style={{ width: '100%', padding: '8px', boxSizing: 'border-box' }}
          />
        </div>

        <div style={{ textAlign: 'left' }}>
          <label style={{ display: 'block', marginBottom: '5px' }}>Тype:</label>
          <select 
            value={type} 
            onChange={(e) => setType(e.target.value)}
            style={{ width: '100%', padding: '8px', boxSizing: 'border-box' }}
          >
            <option value="EXPENSE">Expense</option>
            <option value="INCOME">Income</option>
          </select>
        </div>

        <div style={{ textAlign: 'left' }}>
          <label style={{ display: 'block', marginBottom: '5px' }}>Color:</label>
          <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
            <input
              type="color"
              value={color}
              onChange={(e) => setColor(e.target.value)}
              style={{ height: '40px', width: '60px', padding: 0, border: 'none' }}
            />
            <span>{color}</span>
          </div>
        </div>

        <button type="submit" style={{ marginTop: '10px' }}>Submit</button>
      </form>
    </div>
  );
};

export default CreateCategory;