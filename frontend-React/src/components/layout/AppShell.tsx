import React from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { HexBackground } from './HexBackground';
import { HexBadge } from '../ui/HexBadge';
import { Button } from '../ui/Button';
import { useAuthStore, useUiStore } from '@/store';
import apiClient from '@/api/client';
import { Home, Users, PiggyBank, History, LogOut, Sparkles } from 'lucide-react';

export interface AppShellProps {
  children: React.ReactNode;
}

/**
 * Mobile-first application shell designed for 375px+ screens with Mzansi Futurism aesthetic.
 * Meets minimum 44px+ touch targets and supports iOS safe area insets.
 */
export const AppShell: React.FC<AppShellProps> = ({ children }) => {
  const location = useLocation();
  const navigate = useNavigate();
  const { activeGroupId } = useUiStore();
  const clearAuth = useAuthStore((state) => state.clearAuth);

  const logout = async () => {
    try {
      await apiClient.post('/auth/logout');
    } finally {
      clearAuth();
      navigate('/', { replace: true });
    }
  };

  const navItems = [
    { label: 'Home', path: '/', icon: Home },
    { label: 'Group', path: activeGroupId ? `/group/${activeGroupId}` : '/groups', icon: Users },
    { label: 'Contribute', path: '/contributions', icon: PiggyBank },
    { label: 'History', path: activeGroupId ? `/group/${activeGroupId}/history` : '/history', icon: History },
  ];

  return (
    <div className="min-h-screen bg-white text-mtn-cream flex flex-col relative selection:bg-mtn-gold selection:text-mtn-base overflow-x-hidden">
      {/* Decorative pattern stays outside the application surface. */}
      <HexBackground />

      {/* Main Container - Mobile First (375px optimized, max-w-lg centered on desktop) */}
      <div className="w-full max-w-md md:max-w-lg mx-auto min-h-screen flex flex-col relative z-10 bg-mtn-base border-x border-mtn-border/30 shadow-2xl">
        {/* Sticky Top Header */}
        <header className="sticky top-0 z-40 bg-mtn-surface/90 backdrop-blur-md border-b border-mtn-border px-4 h-14 flex items-center justify-between">
          <Link to="/" className="flex items-center gap-2.5 group touch-target">
            <div className="w-8 h-8 rounded-lg bg-mtn-gold flex items-center justify-center font-black text-mtn-base text-xs tracking-tighter shadow-sm shrink-0">
              MoMo
            </div>
            <div className="flex flex-col">
              <span className="font-extrabold text-sm tracking-tight text-mtn-cream group-hover:text-mtn-gold transition-colors leading-tight">
                Stokvel
              </span>
              <span className="text-[10px] text-mtn-cream-secondary font-mono leading-none">
                Mini App
              </span>
            </div>
          </Link>

          <div className="flex items-center gap-2">
            <HexBadge variant="green" size="sm" icon={<Sparkles className="w-3 h-3 text-mtn-green" />}>
              Live REST
            </HexBadge>
            <Button
              variant="ghost"
              size="sm"
              onClick={logout}
              leftIcon={<LogOut className="w-4 h-4" />}
              label="Log out"
            />
          </div>
        </header>

        {/* Dynamic Page Content */}
        <main className="flex-1 flex flex-col p-4 pb-24 md:pb-8 overflow-y-auto">
          {children}
        </main>

        {/* Mobile Bottom Navigation Bar (48px+ touch targets & safe area padding) */}
        <nav
          className="fixed md:sticky bottom-0 left-0 right-0 z-40 w-full max-w-md md:max-w-lg mx-auto bg-mtn-surface/95 backdrop-blur-md border-t border-mtn-border px-2 py-2 flex items-center justify-around"
          style={{ paddingBottom: 'max(0.5rem, env(safe-area-inset-bottom, 0px))' }}
        >
          {navItems.map((item) => {
            const Icon = item.icon;
            const isActive =
              location.pathname === item.path ||
              (item.path.startsWith('/group/') && location.pathname.startsWith('/group/'));

            return (
              <Link
                key={item.label}
                to={item.path}
                className={`flex flex-col items-center justify-center py-1.5 px-3 rounded-xl min-h-[48px] min-w-[64px] transition-all duration-150 active:scale-95 touch-manipulation ${
                  isActive
                    ? 'text-mtn-gold font-bold'
                    : 'text-mtn-cream-secondary hover:text-mtn-cream active:text-mtn-cream active:bg-mtn-card/60'
                }`}
              >
                <Icon className={`w-5 h-5 ${isActive ? 'text-mtn-gold' : 'text-mtn-cream-secondary'}`} />
                <span className="text-[10px] tracking-tight mt-0.5">{item.label}</span>
                {isActive && (
                  <span className="w-1.5 h-1.5 rounded-full bg-mtn-gold mt-0.5" />
                )}
              </Link>
            );
          })}
        </nav>
      </div>
    </div>
  );
};

export default AppShell;
