import React, { useState } from 'react';
import { Navigate, useNavigate } from 'react-router-dom';
import { Button, Card, Input } from '@/components';
import { HexBackground } from '@/components/layout/HexBackground';
import apiClient from '@/api/client';
import { useAuthStore } from '@/store';
import type { UserProfile } from '@/types';

type AuthResponse = { token: string; user: UserProfile };

export const AuthPage: React.FC = () => {
  const navigate = useNavigate();
  const { isAuthenticated, setAuth } = useAuthStore();
  const [mode, setMode] = useState<'login' | 'register'>('login');
  const [name, setName] = useState('');
  const [msisdn, setMsisdn] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [isSubmitting, setIsSubmitting] = useState(false);

  if (isAuthenticated) return <Navigate to="/home" replace />;

  const submit = async (event: React.FormEvent) => {
    event.preventDefault();
    setError('');
    setIsSubmitting(true);
    try {
      const response = await apiClient.post<AuthResponse>(`/auth/${mode}`, {
        msisdn: msisdn.trim(), password, ...(mode === 'register' ? { name: name.trim() } : {}),
      });
      setAuth(response.data.token, response.data.user);
      navigate('/home', { replace: true });
    } catch (requestError) {
      setError(requestError instanceof Error ? requestError.message : 'Unable to continue.');
    } finally { setIsSubmitting(false); }
  };

  return <div className="min-h-screen bg-white flex items-center justify-center p-5 relative">
    <HexBackground />
    <Card variant="elevated" glow="gold" className="w-full max-w-md p-6 md:p-8 relative z-10">
      <div className="mb-7 text-center">
        <div className="mx-auto mb-4 w-52 rounded-xl bg-white p-2 shadow-sm">
          <img src="/stokvel-logo.png" alt="Stokvel" className="w-full h-auto" />
        </div>
        <h1 className="text-2xl font-black text-mtn-cream">{mode === 'login' ? 'Welcome back' : 'Create your account'}</h1>
        <p className="mt-1 text-sm text-mtn-cream-secondary">Digital Stokvel</p>
      </div>
      <form onSubmit={submit} className="space-y-4">
        {mode === 'register' && <Input label="Full name" value={name} onChange={(e) => setName(e.target.value)} required disabled={isSubmitting} />}
        <Input label="MTN MoMo phone number" type="tel" value={msisdn} onChange={(e) => setMsisdn(e.target.value)} required disabled={isSubmitting} />
        <Input label="Password" type="password" value={password} onChange={(e) => setPassword(e.target.value)} required disabled={isSubmitting} />
        {error && <p className="text-sm text-mtn-red">{error}</p>}
        <Button type="submit" variant="primary" size="lg" fullWidth loading={isSubmitting} disabled={isSubmitting || !msisdn.trim() || !password || (mode === 'register' && !name.trim())} label={mode === 'login' ? 'Log in' : 'Register'} />
      </form>
      <button type="button" className="mt-5 w-full text-sm text-mtn-gold hover:underline" onClick={() => { setMode(mode === 'login' ? 'register' : 'login'); setError(''); }}>
        {mode === 'login' ? 'New here? Create an account' : 'Already have an account? Log in'}
      </button>
    </Card>
  </div>;
};

export default AuthPage;
