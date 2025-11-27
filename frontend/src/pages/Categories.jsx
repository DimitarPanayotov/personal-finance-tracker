import { useEffect, useState } from 'react';
import axios from 'axios';
import { useNavigate, Link } from 'react-router-dom';

const Categories = () => {
  const [categories, setCategories] = useState([]);
  const [error, setError] = useState(null);
  const navigate = useNavigate();

  useEffect(() => {
    const token = localStorage.getItem('my_token');

    if (!token) {
      navigate('/login');
      return;
    }

    axios.get('http://localhost:8080/api/categories', {
      headers: {
        'Authorization': `Bearer ${token}` 
      }
    })
    .then(response => {
      setCategories(response.data);
    })
    .catch(err => {
      console.error(err);
      setError('Cannot load categories');
      if (err.response && err.response.status === 401) {
          navigate('/login');
      }
    });
  }, [navigate]);


  const handleDelete = async (id) => {
    const token = localStorage.getItem('my_token');
    
    try {
        await axios.delete(`http://localhost:8080/api/categories/${id}`, {
            headers: { 'Authorization': `Bearer ${token}` }
        });
        setCategories(categories.filter(cat => cat.id !== id));
    } catch (err) {
        alert('Error while deleting');
    }
  }

  return (
    <div>      
      <h1>My Categories</h1>
      
      <Link to="/categories/new">
        <button>+ New Category</button>
      </Link>
      
      {error && <p style={{color:'red'}}>{error}</p>}

      <div style={{ display: 'grid', gap: '10px', marginTop: '20px' }}>
        {categories.map(cat => (
          <div key={cat.id} style={{ border: '1px solid #ccc', padding: '10px', borderRadius: '8px' }}>
            <div style={{ display: 'flex', alignItems: 'center', gap: '10px', marginBottom: '10px' }}>
              <div style={{ 
                width: '20px', 
                height: '20px', 
                borderRadius: '50%', 
                backgroundColor: cat.color, 
                border: '1px solid #ddd' 
              }}></div>
                <h3 style={{ margin: 0 }}>{cat.name}</h3>
            </div>

            <p style={{ margin: '5px 0' }}>Type: {cat.type}</p>
            <button onClick={() => handleDelete(cat.id)} style={{backgroundColor: 'red', color: 'white', marginTop: '10px'}}>
              Delete
            </button>
          </div>
        ))}
      </div>
    </div>
  );
};

export default Categories;