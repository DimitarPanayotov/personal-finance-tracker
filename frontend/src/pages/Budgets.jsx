import { useEffect, useState } from 'react';
import axios from 'axios';
import { Link, useNavigate } from 'react-router-dom';
import Navbar from '../components/navbar';

const Budgets = () => {
  const [budgets, setBudgets] = useState([]);
  const [error, setError] = useState(null);
  const navigate = useNavigate();

  const fetchBudgets = async () => {
    const token = localStorage.getItem('my_token');
    if (!token) {
        navigate('/login');
        return;
    }

    try {
        const response = await axios.get('http://localhost:8080/api/budgets/usage', {
            headers: { 'Authorization': `Bearer ${token}` }
        });
        setBudgets(response.data);
    } catch (err) {
        console.error(err);
        if (err.response && err.response.status === 401) navigate('/login');
        else setError('Error loading budgets.');
    }
  };

  useEffect(() => {
    fetchBudgets();
  }, [navigate]);

  const handleDelete = async (id) => {
    if (!window.confirm("Are you sure?")) return;
    const token = localStorage.getItem('my_token');
    try {
        await axios.delete(`http://localhost:8080/api/budgets/${id}`, {
            headers: { 'Authorization': `Bearer ${token}` }
        });
        fetchBudgets();
    } catch (err) { alert("Error deleting budget"); }
  };

  const toggleActive = async (id, isActive) => {
    const token = localStorage.getItem('my_token');
    const action = isActive ? 'deactivate' : 'activate'; 
    try {
        await axios.post(`http://localhost:8080/api/budgets/${id}/${action}`, {}, {
            headers: { 'Authorization': `Bearer ${token}` }
        });
        fetchBudgets();
    } catch (err) { alert(`Error ${isActive ? 'deactivating' : 'activating'}.`); }
  };

  return (
    <div>
      <Navbar />
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <h1>Budgets</h1>
        <Link to="/budgets/new">
            <button style={{ backgroundColor: '#4CAF50', color: 'white', padding: '10px', border: 'none', borderRadius: '5px', cursor: 'pointer' }}>
                + New Budget
            </button>
        </Link>
      </div>

      {error && <p style={{color:'red'}}>{error}</p>}

      <div style={{ display: 'grid', gap: '20px', marginTop: '20px', gridTemplateColumns: 'repeat(auto-fill, minmax(300px, 1fr))' }}>
        {budgets.map(b => {
            let progressColor = '#4CAF50';
            if (b.percentUsed >= 80) progressColor = '#FFC107';
            if (b.percentUsed >= 100) progressColor = '#ff4444';
            
            const width = Math.min(b.percentUsed, 100);

            return (
                <div key={b.id} style={{ 
                    border: '1px solid #ccc', 
                    borderRadius: '10px', 
                    padding: '20px',
                    backgroundColor: b.isActive ? 'white' : '#f9f9f9',
                    opacity: b.isActive ? 1 : 0.7 
                }}>
                    <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '10px' }}>
                        <h3>{b.categoryName}</h3>
                        <span style={{ fontSize: '0.9em', color: '#666', textTransform: 'uppercase' }}>{b.period}</span>
                    </div>
                    
                    <p style={{ margin: '5px 0' }}>
                        Spent: <strong>{b.spent} EUR</strong> / {b.amount} EUR
                    </p>
                    
                    {/* Progress bar */}
                    <div style={{ width: '100%', height: '20px', backgroundColor: '#e0e0e0', borderRadius: '10px', overflow: 'hidden', marginBottom: '15px' }}>
                        <div style={{ 
                            width: `${width}%`, 
                            height: '100%', 
                            backgroundColor: progressColor,
                            transition: 'width 0.5s ease-in-out'
                        }}></div>
                    </div>

                    <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                        <button 
                            onClick={() => toggleActive(b.id, b.isActive)}
                            style={{ 
                                padding: '5px 10px', 
                                backgroundColor: b.isActive ? '#FFC107' : '#4CAF50', 
                                color: 'black', border: 'none', borderRadius: '5px', cursor: 'pointer' 
                            }}
                        >
                            {b.isActive ? 'Deactivate' : 'Activate'}
                        </button>

                        <button 
                            onClick={() => handleDelete(b.id)}
                            style={{ padding: '5px 10px', backgroundColor: '#ff4444', color: 'white', border: 'none', borderRadius: '5px', cursor: 'pointer' }}
                        >
                            Delete
                        </button>
                    </div>
                </div>
            );
        })}
      </div>
      
      {budgets.length === 0 && <p>No Budgets Found</p>}
    </div>
  );
};

export default Budgets;