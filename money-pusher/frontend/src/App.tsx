import { BrowserRouter, Navigate, Route, Routes, useLocation, useNavigate } from 'react-router-dom';
import { useEffect, useState } from 'react';
import { Layout, PublicLayout } from './components/Layout';
import { clearTokens, getTokens, setSessionExpiredHandler } from './lib/api';
import { getUser } from './lib/session';
import { Login, Register } from './pages/Auth';
import { Clicker } from './pages/Clicker';
import { Leaderboard } from './pages/Leaderboard';
import { Transfers } from './pages/Transfers';
import { Profile, UserProfile } from './pages/Profile';
import './styles.css';

function Protected({ onSignedOut }: { onSignedOut: () => void }) {
  const location = useLocation(); const [user, setUser] = useState(getUser());
  useEffect(() => { document.title = `Asphalt Clicker · ${location.pathname === '/' ? 'Кликер' : location.pathname.slice(1)}`; }, [location.pathname]);
  if (!getTokens() || !user) { clearTokens(); return <Navigate to="/login" replace state={{ from: location.pathname }} />; }
  return <Layout user={user} onSignedOut={onSignedOut}>{<Routes><Route path="/" element={<Clicker />} /><Route path="/leaderboard" element={<Leaderboard />} /><Route path="/transfers" element={<Transfers />} /><Route path="/profile" element={<Profile user={user} onUserChange={setUser} />} /><Route path="/profile/:email" element={<UserProfile />} /><Route path="*" element={<Navigate to="/" replace />} /></Routes>}</Layout>;
}

function Public() {
  return <PublicLayout><Routes><Route path="/login" element={<Login />} /><Route path="/register" element={<Register />} /><Route path="*" element={<Navigate to="/login" replace />} /></Routes></PublicLayout>;
}

export default function App() {
  const [authenticated, setAuthenticated] = useState(() => Boolean(getTokens() && getUser()));
  useEffect(() => { setSessionExpiredHandler(() => setAuthenticated(false)); }, []);
  return <BrowserRouter>{authenticated ? <Protected onSignedOut={() => setAuthenticated(false)} /> : <Public />}</BrowserRouter>;
}
