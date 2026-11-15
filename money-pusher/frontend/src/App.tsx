import { BrowserRouter, Navigate, Route, Routes, useLocation, useNavigate } from 'react-router-dom';
import { useEffect, useState } from 'react';
import { Layout, PublicLayout } from './components/Layout';
import { clearTokens, getTokenSubject, getTokens, restoreSession, setSessionExpiredHandler, api } from './lib/api';
import { clearUser, getUser, saveUser } from './lib/session';
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
  const [authState, setAuthState] = useState<'checking' | 'authenticated' | 'public'>(() => (
    getTokens() && getUser() ? 'authenticated' : 'checking'
  ));
  useEffect(() => {
    let active = true;
    setSessionExpiredHandler(() => {
      clearUser();
      setAuthState('public');
    });

    if (getTokens() && getUser()) return () => { active = false; };

    restoreSession()
      .then((next) => {
        const email = getTokenSubject(next.access_token);
        if (!email) throw new Error('Access token does not contain a user subject');
        return api.user(email);
      })
      .then((user) => {
        if (!active) return;
        saveUser(user);
        setAuthState('authenticated');
      })
      .catch(() => {
        if (!active) return;
        clearTokens();
        clearUser();
        setAuthState('public');
      });

    return () => { active = false; };
  }, []);

  return <BrowserRouter>{
    authState === 'checking'
      ? <PublicLayout><p className="status status-info" role="status">Восстанавливаем сессию…</p></PublicLayout>
      : authState === 'authenticated'
        ? <Protected onSignedOut={() => setAuthState('public')} />
        : <Public />
  }</BrowserRouter>;
}
