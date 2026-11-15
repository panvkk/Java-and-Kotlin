import type { AuthTokens, User } from '../types';
const USER_KEY = 'asphalt-clicker-user';
localStorage.removeItem(USER_KEY);
export const getUser = (): User | null => JSON.parse(sessionStorage.getItem(USER_KEY) || 'null');
export const saveUser = (user: User) => sessionStorage.setItem(USER_KEY, JSON.stringify(user));
export const clearUser = () => sessionStorage.removeItem(USER_KEY);
export const saveAuthUser = (_tokens: AuthTokens, user: User) => saveUser(user);
