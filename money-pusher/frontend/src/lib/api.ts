import type { ApiError, AuthTokens, Balance, LeaderboardItem, Transfer, User } from '../types';

const baseUrl = (import.meta.env.VITE_API_BASE_URL as string | undefined)?.replace(/\/$/, '') || 'http://localhost:8080';
const AUTH_KEY = 'asphalt-clicker-auth';
let tokens: AuthTokens | null = JSON.parse(localStorage.getItem(AUTH_KEY) || 'null');
let refreshFlight: Promise<AuthTokens> | null = null;
let onSessionExpired: (() => void) | null = null;

export function setSessionExpiredHandler(handler: () => void) { onSessionExpired = handler; }
export function getTokens() { return tokens; }
export function saveTokens(next: AuthTokens) { tokens = next; localStorage.setItem(AUTH_KEY, JSON.stringify(next)); }
export function clearTokens() { tokens = null; localStorage.removeItem(AUTH_KEY); }

const wait = (ms: number) => new Promise((resolve) => setTimeout(resolve, ms));
function messageFor(status: number, category?: string) {
  if (status === 429) return 'Сервис занят. Повтори через несколько секунд.';
  if (status === 401) return 'Сессия истекла. Войди снова.';
  if (status === 400 && category === 'Balance not found') return 'Баланс ещё создаётся.';
  if (status === 409) return 'Этот email уже зарегистрирован.';
  if (status >= 500) return 'Сервис временно недоступен.';
  return 'Не удалось выполнить операцию.';
}

async function parseError(response: Response): Promise<ApiError> {
  let payload: { error?: string } = {};
  try { payload = await response.json(); } catch { /* empty gateway body */ }
  const error = new Error(messageFor(response.status, payload.error)) as ApiError;
  error.status = response.status; error.category = payload.error;
  return error;
}

async function refresh(): Promise<AuthTokens> {
  if (refreshFlight) return refreshFlight;
  const refreshToken = tokens?.refresh_token;
  if (!refreshToken) throw new Error('No refresh token');
  refreshFlight = fetch(`${baseUrl}/api/v1/auth/refresh-token`, {
    method: 'POST', headers: { Authorization: `Bearer ${refreshToken}` },
  }).then(async (response) => {
    if (!response.ok) throw await parseError(response);
    const next = await response.json() as AuthTokens;
    saveTokens(next);
    return next;
  }).finally(() => { refreshFlight = null; });
  return refreshFlight;
}

async function request<T>(path: string, init: RequestInit = {}, canRefresh = true, attempt = 0): Promise<T> {
  const headers = new Headers(init.headers);
  headers.set('Content-Type', 'application/json');
  if (tokens?.access_token) headers.set('Authorization', `Bearer ${tokens.access_token}`);
  const response = await fetch(`${baseUrl}${path}`, { ...init, headers });
  if (response.status === 401 && canRefresh && tokens?.refresh_token) {
    try { await refresh(); return request(path, init, false, attempt); }
    catch { clearTokens(); onSessionExpired?.(); throw new Error('Сессия истекла. Войди снова.'); }
  }
  if (response.status === 429 && attempt < 3) { await wait(300 * 2 ** attempt); return request(path, init, canRefresh, attempt + 1); }
  if (!response.ok) throw await parseError(response);
  const text = await response.text();
  return (text ? JSON.parse(text) : null) as T;
}

export const api = {
  login: (body: { email: string; password: string }) => request<AuthTokens>('/api/v1/auth/authenticate', { method: 'POST', body: JSON.stringify(body) }, false),
  register: (body: { nickname: string; email: string; password: string }) => request<AuthTokens>('/api/v1/auth/register', { method: 'POST', body: JSON.stringify(body) }, false),
  user: (email: string) => request<User>(`/api/v1/users/${encodeURIComponent(email)}`, {}, false),
  balance: (email: string) => request<Balance>(`/api/v1/clicker/balance/${encodeURIComponent(email)}`),
  click: () => request<void>('/api/v1/clicker', { method: 'PATCH', body: JSON.stringify({ balance_increase: 1 }) }),
  leaderboard: () => request<{ items: LeaderboardItem[] }>('/api/v1/leaderboard', {}, false),
  transfers: () => request<{ transfers: Transfer[] }>('/api/v1/transfers'),
  createTransfer: (body: { target_user_email: string; transfer_amount: number; additional_message: string }) =>
    request<Transfer>('/api/v1/transfers', { method: 'POST', body: JSON.stringify(body) }),
  logout: () => request<void>('/api/v1/logout', { method: 'POST', body: JSON.stringify({ access_token: tokens?.access_token || '', refresh_token: tokens?.refresh_token || '' }) }),
};

export async function loadBalanceWithRetry(email: string) {
  for (let attempt = 0; attempt < 4; attempt += 1) {
    try { return (await api.balance(email)).user_balance; }
    catch (error) {
      if ((error as ApiError).status !== 400 || attempt === 3) throw error;
      await wait(Math.min(300 * 2 ** attempt, 1000));
    }
  }
  throw new Error('Баланс недоступен');
}
