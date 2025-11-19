import { useEffect, useState } from 'react';
import api from '../api'; 

const Categories = () => {
  const [categories, setCategories] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const fetchCategories = async () => {
    try {
      const response = await api.get('/categories');
      setCategories(response.data);
      setLoading(false);
    } catch (err) {
      console.error('Error fetching categories:', err);
      setError('Failed to load categories.');
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchCategories();
  }, []);

  if (loading) return <p>Loading categories...</p>;
  if (error) return <p style={{ color: 'red' }}>{error}</p>;

  return (
    <div>
      <h1>Categories</h1>
      
      {categories.length === 0 ? (
        <p>No categories found. Create one!</p>
      ) : (
        <div style={{ display: 'grid', gap: '1rem', gridTemplateColumns: 'repeat(auto-fill, minmax(250px, 1fr))' }}>
          {categories.map((cat) => (
            <div key={cat.id} className="card" style={{ borderLeft: `5px solid ${cat.color}`, textAlign: 'left' }}>
              <h3>{cat.name}</h3>
              <p>Type: <strong>{cat.type}</strong></p>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

export default Categories;