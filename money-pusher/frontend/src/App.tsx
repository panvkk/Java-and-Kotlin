import { BrowserRouter, Navigate, Route, Routes, useLocation, useNavigate } from 'react-router-dom';
import { useEffect, useMemo, useState } from 'react';
import { Layout, PublicLayout } from './components/Layout';
import { getTokens, setSessionExpiredHandler } from './lib/api';
import { getUser } from './lib/session';
import { Login, Register } from './pages/Auth';
import { Clicker } from './pages/Clicker';
import { Leaderboard } from './pages/Leaderboard';
import { Transfers } from './pages/Transfers';
import { Profile } from './pages/Profile';
import './styles.css';

function Protected() {
  const navigate = useNavigate(); const location = useLocation(); const [user, setUser] = useState(getUser());
  useEffect(() => { setSessionExpiredHandler(() => navigate('/login', { replace: true })); }, [navigate]);
  useEffect(() => { document.title = `Asphalt Clicker · ${location.pathname === '/' ? 'Кликер' : location.pathname.slice(1)}`; }, [location.pathname]);
  if (!getTokens() || !user) return <Navigate to="/login" replace state={{ from: location.pathname }} />;
  return <Layout user={user}>{<Routes><Route path="/" element={<Clicker />} /><Route path="/leaderboard" element={<Leaderboard />} /><Route path="/transfers" element={<Transfers />} /><Route path="/profile" element={<Profile user={user} onUserChange={setUser} />} /><Route path="*" element={<Navigate to="/" replace />} /></Routes>}</Layout>;
}

function Public() {
  return <PublicLayout><Routes><Route path="/login" element={<Login />} /><Route path="/register" element={<Register />} /><Route path="*" element={<Navigate to="/login" replace />} /></Routes></PublicLayout>;
}

export default function App() {
  const authenticated = useMemo(() => Boolean(getTokens() && getUser()), []);
  return <BrowserRouter>{authenticated ? <Protected /> : <Public />}</BrowserRouter>;
}
