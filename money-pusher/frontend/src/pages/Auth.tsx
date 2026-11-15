import { FormEvent, useState } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { Button, Field, Status } from '../components/UI';
import { api, loadBalanceWithRetry, saveTokens } from '../lib/api';
import { saveUser } from '../lib/session';
import type { ApiError, AuthTokens } from '../types';

function AuthForm({ mode }: { mode: 'login' | 'register' }) {
  const navigate = useNavigate(); const location = useLocation(); const [loading, setLoading] = useState(false); const [error, setError] = useState(''); const [form, setForm] = useState({ nickname: '', email: '', password: '' });
  const register = mode === 'register';
  const submit = async (event: FormEvent) => { event.preventDefault(); setError(''); if (form.password.length < 8 || !form.email.includes('@') || (register && form.nickname.trim().length < 2)) { setError('Проверь заполнение полей. Пароль — минимум 8 символов.'); return; } setLoading(true);
    try {
      let tokens: AuthTokens;
      try {
        tokens = register ? await api.register(form) : await api.login({ email: form.email, password: form.password });
      } catch (e) {
        if (!register && (e as ApiError).status === 401) {
          setError('Проверь логин или пароль.');
          return;
        }
        throw e;
      }
      saveTokens(tokens); const user = await api.user(form.email); saveUser(user); if (register) await loadBalanceWithRetry(user.email).catch(() => undefined); window.location.replace((location.state as { from?: string } | null)?.from || '/');
    } catch (e) { setError(e instanceof Error ? e.message : 'Операция не выполнена.'); } finally { setLoading(false); }
  };
  return <><div className="auth-card-head"><p className="kicker">личный кабинет</p><h2>{register ? 'Начать с нуля.' : 'С возвращением.'}</h2><p>{register ? 'Создай счёт и забери свой первый coin.' : 'Войди, чтобы продолжить свой забег.'}</p></div><form className="auth-form" onSubmit={submit} noValidate>
    {register && <Field label="Никнейм"><input value={form.nickname} onChange={(e) => setForm({ ...form, nickname: e.target.value })} autoComplete="nickname" required /></Field>}
    <Field label="Email"><input type="email" value={form.email} onChange={(e) => setForm({ ...form, email: e.target.value })} autoComplete="email" required /></Field>
    <Field label="Пароль" hint={register ? 'Минимум 8 символов.' : undefined}><input type="password" value={form.password} onChange={(e) => setForm({ ...form, password: e.target.value })} autoComplete={register ? 'new-password' : 'current-password'} required /></Field>
    {error && <Status tone="error">{error}</Status>}<Button type="submit" loading={loading}>{register ? 'Создать аккаунт' : 'Войти в игру'} <span aria-hidden="true">↗</span></Button>
  </form><p className="auth-switch">{register ? 'Уже есть счёт?' : 'Первый раз на дороге?'} <Link to={register ? '/login' : '/register'}>{register ? 'Войти' : 'Зарегистрироваться'}</Link></p></>;
}
export function Login() { return <AuthForm mode="login" />; }
export function Register() { return <AuthForm mode="register" />; }
