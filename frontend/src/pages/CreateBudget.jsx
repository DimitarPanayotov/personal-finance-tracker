import { useState, useEffect } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';

const CreateBudget = () => {
  const [amount, setAmount] = useState('');
  const [period, setPeriod] = useState('MONTHLY');
  const [startDate, setStartDate] = useState('');
  const [endDate, setEndDate] = useState('');
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

    setStartDate(new Date().toISOString().split('T')[0]);
  }, [navigate]);

  const handleSubmit = async (e) => {
    e.preventDefault();
    const token = localStorage.getItem('my_token');

    const budgetData = {
        categoryId: categoryId,
        amount: parseFloat(amount),
        period: period,
        startDate: startDate,
        endDate: endDate || null
    };

    try {
        await axios.post('http://localhost:8080/api/budgets', budgetData, {
            headers: { 'Authorization': `Bearer ${token}` }
        });
        navigate('/budgets');
    } catch (err) {
        console.error(err);
        setError(err.response?.data?.message || 'Error Creating Budget.');
    }
  };

  return (
    <div className="card">
      <h2>New Budget</h2>
      {error && <p style={{color:'red'}}>{error}</p>}
      
      {categories.length === 0 ? (
        <p>A category should be created first!</p>
      ) : (
        <form onSubmit={handleSubmit} style={{ display: 'flex', flexDirection: 'column', gap: '1rem', maxWidth: '400px', margin: '0 auto' }}>            
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
                <label>Limit (EUR):</label>
                <input 
                    type="number" 
                    step="0.01" 
                    value={amount} 
                    onChange={e => setAmount(e.target.value)} 
                    required 
                    placeholder="E.g. 500"
                    style={{ width: '100%', padding: '8px' }}
                />
            </div>
            <div style={{ textAlign: 'left' }}>
                <label>Period:</label>
                <select 
                    value={period} 
                    onChange={e => setPeriod(e.target.value)}
                    style={{ width: '100%', padding: '8px' }}
                >
                    <option value="WEEKLY">Weekly</option>
                    <option value="MONTHLY">Monthly</option>
                    <option value="QUARTERLY">Three months</option>
                    <option value="YEARLY">Yearly</option>
                    <option value="CUSTOM">Custom</option>
                </select>
            </div>
            <div style={{ textAlign: 'left' }}>
                <label>Start date:</label>
                <input 
                    type="date" 
                    value={startDate} 
                    onChange={e => setStartDate(e.target.value)} 
                    required 
                    style={{ width: '100%', padding: '8px' }}
                />
            </div>
            <div style={{ textAlign: 'left' }}>
                <label>End date {period !== 'CUSTOM' && '(Optional)'}:</label>
                <input 
                    type="date" 
                    value={endDate} 
                    onChange={e => setEndDate(e.target.value)} 
                    required={period === 'CUSTOM'} 
                    style={{ width: '100%', padding: '8px' }}
                />
            </div>

            <button type="submit" style={{ marginTop: '10px', backgroundColor: '#4CAF50' }}>Create Budget</button>
        </form>
      )}
    </div>
  );
};

export default CreateBudget;