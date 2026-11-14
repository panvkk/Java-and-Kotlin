import { Link, NavLink, useNavigate } from 'react-router-dom';
import { clearTokens, api } from '../lib/api';
import { clearUser } from '../lib/session';
import { Icon } from './Icon';
import type { ReactNode } from 'react';

export function Layout({ children, user }: { children: ReactNode; user: { nickname: string; email: string } }) {
  const navigate = useNavigate();
  const logout = async () => { try { await api.logout(); } catch { /* local session still must end */ } clearTokens(); clearUser(); navigate('/login', { replace: true }); };
  return <div className="app-shell">
    <a className="skip-link" href="#content">К содержимому</a>
    <header className="topbar"><Link className="brand" to="/"><span className="brand-mark">A</span><span>asphalt<span className="brand-muted">.by</span></span></Link>
      <nav className="main-nav" aria-label="Основная навигация">{[['/', 'Кликер'], ['/leaderboard', 'Лидерборд'], ['/transfers', 'Переводы'], ['/profile', 'Профиль']].map(([to, label]) => <NavLink key={to} to={to} end={to === '/'}>{label}</NavLink>)}</nav>
      <div className="topbar-actions"><span className="user-chip"><span className="avatar">{user.nickname.slice(0, 1).toUpperCase()}</span><span>{user.nickname}</span></span><button className="icon-btn" type="button" onClick={logout} aria-label="Выйти"><Icon name="logout" /></button></div>
      <button className="icon-btn mobile-menu" type="button" aria-label="Открыть меню"><Icon name="menu" /></button>
    </header>
    <main id="content">{children}</main>
    <footer className="footer"><div><strong>asphalt<span className="brand-muted">.by</span></strong><span> Цифровой артефакт дорожной культуры.</span></div><span>Сессия защищена gateway</span></footer>
  </div>;
}

export function PublicLayout({ children }: { children: ReactNode }) {
  return <div className="auth-shell"><div className="auth-texture"><Link className="brand" to="/login"><span className="brand-mark">A</span><span>asphalt<span className="brand-muted">.by</span></span></Link><div className="auth-hero"><p className="kicker">банк асфальтовых монет</p><h1>Серьёзные<br /><span>деньги.</span><br />Ненастоящие.</h1><p>Кликай, копи и переводи монеты в самом честном банке интернета.</p></div><span className="road-line">12 / 2026 · digital roadworks</span></div><section className="auth-card">{children}</section></div>;
}
