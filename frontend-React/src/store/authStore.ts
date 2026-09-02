import { create } from 'zustand';
import type { UserProfile } from '@/types';

interface AuthState {
  token: string | null;
  user: UserProfile | null;
  isAuthenticated: boolean;
  setAuth: (token: string, user: UserProfile) => void;
  clearAuth: () => void;
  setUser: (user: UserProfile) => void;
}

export const useAuthStore = create<AuthState>((set) => ({
  token: localStorage.getItem('auth_token'),
  user: null,
  isAuthenticated: Boolean(localStorage.getItem('auth_token')),

  setAuth: (token: string, user: UserProfile) => {
    localStorage.setItem('auth_token', token);
    set({
      token,
      user,
      isAuthenticated: true,
    });
  },

  clearAuth: () => {
    localStorage.removeItem('auth_token');
    set({
      token: null,
      user: null,
      isAuthenticated: false,
    });
  },

  setUser: (user: UserProfile) => {
    set({ user });
  },
}));
