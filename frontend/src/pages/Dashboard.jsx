import { useEffect, useState } from "react";
import axios from "axios";
import { useNavigate, Link } from "react-router-dom";

const Dashboard = () => {
  const [stats, setStats] = useState(null);
  const [recentTransactions, setRecentTransactions] = useState([]);
  const [loading, setLoading] = useState(null);
  const [error, setError] = useState(null);

  const navigate = useNavigate();

  useEffect(() => {
    const token = localStorage.getItem('my_token');
    if (!token) {
      navigate('/login');
      return;
    }

    const fetchData = async () => {
      try {
        // Promise.all -> the 2 requests are parallel
        const [statsRes, transactionsRes] = await Promise.all([
          axios.get('http://localhost:8080/api/users/me/statistics', {
            headers: { 'Authorization': `Bearer ${token}` }
          }),
          axios.get('http://localhost:8080/api/transactions/recent?limit=5', {
            headers: { 'Authorization': `Bearer ${token}` }
          })
        ]);

        setStats(statsRes.data);
        setRecentTransactions(transactionsRes.data);
        setLoading(false);
    } catch (err) {
      console.error('Error loading dashboard data:', err);
      if (err.response && err.response.status === 401) {
        navigate('/login');
      } else {
        setError('Error loading the data.');
        setLoading(false);
      }
    }
  };

  if (loading) return <div style={{padding: '20px'}}>Loading...</div>;
  if (error) return <div style={{padding: '20px', color: 'red'}}>{error}</div>;

  fetchData();
  }, [navigate])

  return (
    <div>      
      <h1>Dashboard</h1>
      
      {/* --- Statistics --- */}
      <div style={{ display: 'flex', gap: '20px', flexWrap: 'wrap', marginBottom: '30px' }}>
        <div style={{ flex: 1, minWidth: '200px', padding: '20px', backgroundColor: '#d4edda', borderRadius: '10px', border: '1px solid #c3e6cb', color: '#155724' }}>
          <h3>Total Income</h3>
          <p style={{ fontSize: '1.5rem', fontWeight: 'bold' }}>
             +{stats?.totalIncome?.toFixed(2)} EUR
          </p>
        </div>
        <div style={{ flex: 1, minWidth: '200px', padding: '20px', backgroundColor: '#f8d7da', borderRadius: '10px', border: '1px solid #f5c6cb', color: '#721c24' }}>
          <h3>Total Expenses</h3>
          <p style={{ fontSize: '1.5rem', fontWeight: 'bold' }}>
             -{stats?.totalExpenses?.toFixed(2)} EUR
          </p>
        </div>
        <div style={{ flex: 1, minWidth: '200px', padding: '20px', backgroundColor: '#cce5ff', borderRadius: '10px', border: '1px solid #b8daff', color: '#004085' }}>
          <h3>Balance</h3>
          <p style={{ fontSize: '1.5rem', fontWeight: 'bold' }}>
             {stats?.netBalance?.toFixed(2)} EUR
          </p>
        </div>
      </div>
      <div style={{ marginTop: '40px' }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '10px' }}>
            <h2>Recent Transactions</h2>
            <Link to="/transactions">See all: </Link>
        </div>

        {recentTransactions.length === 0 ? (
            <p>No recent transactions</p>
        ) : (
            <table style={{ width: '100%', borderCollapse: 'collapse' }}>
                <thead>
                    <tr style={{  backgroundColor: '#f1f1f1' }}>
                        <th style={{ padding: '10px', textAlign: 'left' }}>Date</th>
                        <th style={{ padding: '10px', textAlign: 'left' }}>Description</th>
                        <th style={{ padding: '10px', textAlign: 'left' }}>Amount</th>
                    </tr>
                </thead>
                <tbody>
                    {recentTransactions.map(tx => (
                        <tr key={tx.id} style={{ borderBottom: '1px solid #eee' }}>
                            <td style={{ padding: '10px' }}>{tx.transactionDate}</td>
                            <td style={{ padding: '10px' }}>
                                <strong>{tx.categoryName}</strong> <br/>
                                <span style={{ fontSize: '0.9em', color: '#666' }}>{tx.description}</span>
                            </td>
                            <td style={{ padding: '10px', fontWeight: 'bold', color: tx.amount > 0 ? 'green' : 'red' }}>
                                {tx.amount.toFixed(2)} EUR
                            </td>
                        </tr>
                    ))}
                </tbody>
            </table>
        )}
      </div>
    </div>
  );
};

export default Dashboard;