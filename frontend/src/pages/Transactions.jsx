import { useEffect, useState } from 'react';
import axios from 'axios';
import { useNavigate, Link } from 'react-router-dom';
import Navbar from '../components/navbar';

const Transactions = () => {
  const [transactions, setTransactions] = useState([]);
  const [page, setPage] = useState(0); 
  const [totalPages, setTotalPages] = useState(0); 
  const [error, setError] = useState(null);
  const navigate = useNavigate();

  const fetchTransactions = async (pageNum) => {
    const token = localStorage.getItem('my_token');
    if (!token) {
      navigate('/login');
      return;
    }

    try {
      const response = await axios.get(`http://localhost:8080/api/transactions?page=${pageNum}&size=10&sortBy=transactionDate&sortDirection=DESC`, {
        headers: { 'Authorization': `Bearer ${token}` }
      });
            setTransactions(response.data.content);
      setTotalPages(response.data.totalPages);
    } catch (err) {
      console.error(err);
      if (err.response && err.response.status === 401) {
          navigate('/login');
      } else {
          setError('Error loading transactions.');
      }
    }
  };

  useEffect(() => {
    fetchTransactions(page);
  }, [page, navigate]);

  const handleDelete = async (id) => {
    if(!window.confirm("Are you sure you want to delete this transaction?")) return;

    const token = localStorage.getItem('my_token');
    try {
        await axios.delete(`http://localhost:8080/api/transactions/${id}`, {
            headers: { 'Authorization': `Bearer ${token}` }
        });
        fetchTransactions(page);
    } catch (err) {
        alert('Error deleting transaction.');
    }
  };

  return (
    <div>
      <Navbar />
      
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <h1>Transactions</h1>
        <Link to="/transactions/new">
            <button style={{ backgroundColor: '#4CAF50', color: 'white', padding: '10px', border: 'none', borderRadius: '5px', cursor: 'pointer' }}>
                + Add new transaction
            </button>
        </Link>
      </div>

      {error && <p style={{color:'red'}}>{error}</p>}

      {/* Table with transactions */}
      <table style={{ width: '100%', borderCollapse: 'collapse', marginTop: '20px' }}>
        <thead>
          <tr style={{ backgroundColor: '#f2f2f2', textAlign: 'left' }}>
            <th style={{ padding: '10px', borderBottom: '1px solid #ddd' }}>Date</th>
            <th style={{ padding: '10px', borderBottom: '1px solid #ddd' }}>Category</th>
            <th style={{ padding: '10px', borderBottom: '1px solid #ddd' }}>Description</th>
            <th style={{ padding: '10px', borderBottom: '1px solid #ddd' }}>Amount</th>
            <th style={{ padding: '10px', borderBottom: '1px solid #ddd' }}>Actions</th>
          </tr>
        </thead>
        <tbody>
          {transactions.length === 0 ? (
            <tr><td colSpan="5" style={{padding: '20px', textAlign: 'center'}}>No transactions found.</td></tr>
          ) : (
            transactions.map(tx => (
              <tr key={tx.id}>
                <td style={{ padding: '10px', borderBottom: '1px solid #ddd' }}>{tx.transactionDate}</td>
                <td style={{ padding: '10px', borderBottom: '1px solid #ddd' }}>{tx.categoryName}</td>
                <td style={{ padding: '10px', borderBottom: '1px solid #ddd' }}>{tx.description}</td>
                <td style={{ padding: '10px', borderBottom: '1px solid #ddd', fontWeight: 'bold', color: tx.amount > 0 ? '#4CAF50' : '#FF5733' }}>
                    {tx.amount} bgn.
                </td>
                <td style={{ padding: '10px', borderBottom: '1px solid #ddd' }}>
                  <button onClick={() => handleDelete(tx.id)} style={{ backgroundColor: '#ff4444', color: 'white', border: 'none', padding: '5px 10px', borderRadius: '4px', cursor: 'pointer' }}>
                    Delete
                  </button>
                </td>
              </tr>
            ))
            )}
        </tbody>
      </table>
      {/* Pagination */}
      <div style={{ marginTop: '20px', display: 'flex', justifyContent: 'center', gap: '10px' }}>
        <button 
            disabled={page === 0} 
            onClick={() => setPage(p => p - 1)}
            style={{ padding: '5px 15px', cursor: page === 0 ? 'not-allowed' : 'pointer' }}
        >
            &lt; Previous
        </button>
        <span>Page {page + 1} of {totalPages || 1}</span>
        <button 
            disabled={page >= totalPages - 1} 
            onClick={() => setPage(p => p + 1)}
            style={{ padding: '5px 15px', cursor: page >= totalPages - 1 ? 'not-allowed' : 'pointer' }}
        >
            Next &gt;
        </button>
      </div>
    </div>
  );
};

export default Transactions;