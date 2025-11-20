import { useState, useEffect } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';

const CreateTransaction = () => {
  const [amount, setAmount] = useState('');
  const [description, setDescription] = useState('');
  const [transactionDate, setTransactionDate] = useState('');
  const [categoryId, setCategoryId] = useState('');
  
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
        headers: { 'Authorization': `Bearer ${token}` }
    })
    .then(res => {
        setCategories(res.data);
        if (res.data.length > 0) {
            setCategoryId(res.data[0].id);
        }
    })
    .catch(err => console.error("Error loading categories:", err));

    setTransactionDate(new Date().toISOString().split('T')[0]);
  }, [navigate]);

  const handleSubmit = async (e) => {
    e.preventDefault();
    const token = localStorage.getItem('my_token');

    const transactionData = {
        categoryId: categoryId,
        amount: parseFloat(amount), 
        description: description,
        transactionDate: transactionDate
    };

    try {
        await axios.post('http://localhost:8080/api/transactions', transactionData, {
            headers: { 'Authorization': `Bearer ${token}` }
        });
        navigate('/transactions');
    } catch (err) {
        console.error(err);
        setError(err.response?.data?.message || 'Error creating.');
    }
  };

  return (
    <div className="card">
      <h2>New Transaction</h2>
      {error && <p style={{color:'red'}}>{error}</p>}
      
      {categories.length === 0 ? (
        <p>At least one category required!</p>
      ) : (
        <form onSubmit={handleSubmit} style={{ display: 'flex', flexDirection: 'column', gap: '1rem', maxWidth: '400px', margin: '0 auto' }}>
            
            <div style={{ textAlign: 'left' }}>
                <label>Amount (bgn.):</label>
                <input 
                    type="number" 
                    step="0.01" 
                    value={amount} 
                    onChange={e => setAmount(e.target.value)} 
                    required 
                    style={{ width: '100%', padding: '8px' }}
                />
            </div>

            <div style={{ textAlign: 'left' }}>
                <label>Category:</label>
                <select 
                    value={categoryId} 
                    onChange={e => setCategoryId(e.target.value)}
                    style={{ width: '100%', padding: '8px' }}
                >
                    {categories.map(cat => (
                        <option key={cat.id} value={cat.id}>
                            {cat.name} ({cat.type})
                        </option>
                    ))}
                </select>
            </div>

            <div style={{ textAlign: 'left' }}>
                <label>Date:</label>
                <input 
                    type="date" 
                    value={transactionDate} 
                    onChange={e => setTransactionDate(e.target.value)} 
                    required 
                    style={{ width: '100%', padding: '8px' }}
                />
            </div>

            <div style={{ textAlign: 'left' }}>
                <label>Description:</label>
                <input 
                    type="text" 
                    value={description} 
                    onChange={e => setDescription(e.target.value)} 
                    placeholder="e.g. Lunch" 
                    style={{ width: '100%', padding: '8px' }}
                />
            </div>

            <button type="submit" style={{ marginTop: '10px' }}>Submit</button>
        </form>
      )}
    </div>
  );
};

export default CreateTransaction;