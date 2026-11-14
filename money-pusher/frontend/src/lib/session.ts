import type { AuthTokens, User } from '../types';
const USER_KEY = 'asphalt-clicker-user';
export const getUser = (): User | null => JSON.parse(localStorage.getItem(USER_KEY) || 'null');
export const saveUser = (user: User) => localStorage.setItem(USER_KEY, JSON.stringify(user));
export const clearUser = () => localStorage.removeItem(USER_KEY);
export const saveAuthUser = (tokens: AuthTokens, user: User) => { localStorage.setItem('asphalt-clicker-auth', JSON.stringify(tokens)); saveUser(user); };
