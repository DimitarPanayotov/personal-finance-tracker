import { Navigate, Outlet } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import Navbar from './navbar';

const ProtectedRoute = () => {
  const { user } = useAuth();

  if (!user) {
    return <Navigate to="/login" replace />;
  }

  return (
    <>
      <Navbar /> {/* <- The menu is on top */}
      <div style={{ padding: '0 2rem' }}>
        <Outlet /> {/* <- The current page is loaded here (Dashboard, Categories...) */}
      </div>
    </>
  );
};

export default ProtectedRoute;