import { createContext, useContext, useState, useCallback } from 'react';

const AuthContext = createContext(null);

const KEYS = {
  TOKEN: 'tw_token',
  USER_NAME: 'tw_user_name',
  USER_EMAIL: 'tw_user_email',
  USER_ROLE: 'tw_user_role',
  USER_ID: 'tw_user_id',
  ONBOARDING_SEEN: 'tw_onboarding_seen',
};

function getStoredUser() {
  const token = localStorage.getItem(KEYS.TOKEN);
  if (!token) return null;
  return {
    token,
    name: localStorage.getItem(KEYS.USER_NAME) || '',
    email: localStorage.getItem(KEYS.USER_EMAIL) || '',
    role: localStorage.getItem(KEYS.USER_ROLE) || 'user',
    id: parseInt(localStorage.getItem(KEYS.USER_ID)) || 0,
  };
}

export function AuthProvider({ children }) {
  const [user, setUser] = useState(getStoredUser);

  const login = useCallback((data) => {
    if (data.token) localStorage.setItem(KEYS.TOKEN, data.token);
    if (data.user) {
      localStorage.setItem(KEYS.USER_NAME, data.user.name || '');
      localStorage.setItem(KEYS.USER_EMAIL, data.user.email || '');
      localStorage.setItem(KEYS.USER_ROLE, data.user.role || 'user');
      localStorage.setItem(KEYS.USER_ID, data.user.id || 0);
    }
    setUser({
      token: data.token || localStorage.getItem(KEYS.TOKEN),
      name: data.user?.name || '',
      email: data.user?.email || '',
      role: data.user?.role || 'user',
      id: data.user?.id || 0,
    });
  }, []);

  const logout = useCallback(() => {
    Object.entries(KEYS).forEach(([name, key]) => {
      if (name !== 'ONBOARDING_SEEN') localStorage.removeItem(key);
    });
    setUser(null);
  }, []);

  const hasSeenOnboarding = useCallback(() => {
    return localStorage.getItem(KEYS.ONBOARDING_SEEN) === 'true';
  }, []);

  const setOnboardingSeen = useCallback(() => {
    localStorage.setItem(KEYS.ONBOARDING_SEEN, 'true');
  }, []);

  const isLoggedIn = !!user?.token;
  const token = user?.token || null;

  const value = {
    user,
    token,
    isLoggedIn,
    login,
    logout,
    hasSeenOnboarding,
    setOnboardingSeen,
    getUserName: () => user?.name || '',
    getUserEmail: () => user?.email || '',
    getUserRole: () => user?.role || 'user',
    getUserId: () => user?.id || 0,
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error('useAuth must be used within AuthProvider');
  return ctx;
}
