import { Link, NavLink } from 'react-router-dom';
import { clearTokens, api } from '../lib/api';
import { clearUser } from '../lib/session';
import { Icon } from './Icon';
import type { ReactNode } from 'react';
import { useEffect, useState } from 'react';

function ThemeToggle() {
  const [dark, setDark] = useState(() => document.documentElement.dataset.theme === 'dark');
  const toggle = () => {
    const next = !dark;
    setDark(next);
    document.documentElement.dataset.theme = next ? 'dark' : 'light';
    localStorage.setItem('asphalt-clicker-theme', next ? 'dark' : 'light');
  };
  return <button className="theme-toggle" type="button" aria-pressed={dark} onClick={toggle}><Icon name={dark ? 'sun' : 'moon'} /><span>{dark ? 'Светлая' : 'Тёмная'}</span></button>;
}

export function Layout({ children, user, onSignedOut }: { children: ReactNode; user: { nickname: string; email: string }; onSignedOut: () => void }) {
  const [menuOpen, setMenuOpen] = useState(false);
  const logout = async () => { try { await api.logout(); } catch { /* local session still must end */ } clearTokens(); clearUser(); onSignedOut(); };
  useEffect(() => {
    if (!menuOpen) return;
    const closeOnEscape = (event: KeyboardEvent) => { if (event.key === 'Escape') setMenuOpen(false); };
    document.addEventListener('keydown', closeOnEscape);
    return () => document.removeEventListener('keydown', closeOnEscape);
  }, [menuOpen]);
  return <div className="app-shell">
    <a className="skip-link" href="#content">К содержимому</a>
    <header className="topbar"><Link className="brand" to="/" onClick={() => setMenuOpen(false)}><span className="brand-mark">A</span><span>asphalt<span className="brand-muted">.by</span></span></Link>
      <nav id="main-navigation" className={`main-nav ${menuOpen ? 'is-open' : ''}`} aria-label="Основная навигация">{[['/', 'Кликер'], ['/leaderboard', 'Лидерборд'], ['/transfers', 'Переводы'], ['/profile', 'Профиль']].map(([to, label]) => <NavLink key={to} to={to} end={to === '/'} onClick={() => setMenuOpen(false)}>{label}</NavLink>)}</nav>
      <div className="topbar-actions"><ThemeToggle /><span className="user-chip"><span className="avatar">{user.nickname.slice(0, 1).toUpperCase()}</span><span>{user.nickname}</span></span><button className="icon-btn" type="button" onClick={logout} aria-label="Выйти"><Icon name="logout" /></button></div>
      <button className="icon-btn mobile-menu" type="button" aria-label={menuOpen ? 'Закрыть меню' : 'Открыть меню'} aria-expanded={menuOpen} aria-controls="main-navigation" onClick={() => setMenuOpen((value) => !value)}><Icon name="menu" /></button>
    </header>
    <main id="content">{children}</main>
    <footer className="footer"><div><strong>asphalt<span className="brand-muted">.by</span></strong><span> Цифровой артефакт дорожной культуры.</span></div><span>Сессия защищена gateway</span></footer>
  </div>;
}

export function PublicLayout({ children }: { children: ReactNode }) {
  return <div className="auth-shell"><div className="auth-texture"><div className="auth-topline"><Link className="brand" to="/login"><span className="brand-mark">A</span><span>asphalt<span className="brand-muted">.by</span></span></Link><ThemeToggle /></div><div className="auth-hero"><p className="kicker">банк асфальтовых монет</p><h1>Серьёзные<br /><span>деньги.</span><br />Ненастоящие.</h1><p>Кликай, копи и переводи монеты в самом честном банке интернета.</p></div><span className="road-line">12 / 2026 / digital roadworks</span></div><section className="auth-card">{children}</section></div>;
}
