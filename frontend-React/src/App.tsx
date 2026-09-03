import React from 'react';
import { Navigate, Routes, Route } from 'react-router-dom';
import { AppShell } from '@/components/layout/AppShell';
import {
  Landing,
  CreateGroup,
  InviteShare,
  JoinGroup,
  GroupDashboard,
  GroupHistory,
  ContributionsPage,
  HomePage,
  NotFoundPage,
  AuthPage,
} from '@/pages';
import { useAuthStore } from '@/store';

export const App: React.FC = () => {
  const isAuthenticated = useAuthStore((state) => state.isAuthenticated);
  if (!isAuthenticated) return <Routes><Route path="*" element={<AuthPage />} /></Routes>;
  return (
    <AppShell>
      <Routes>
        {/* Phase 18 Canonical Routes */}
        <Route path="/" element={<Navigate to="/home" replace />} />
        <Route path="/home" element={<Landing />} />
        <Route path="/create" element={<CreateGroup />} />
        <Route path="/invite/:groupId" element={<InviteShare />} />
        <Route path="/join" element={<JoinGroup />} />
        <Route path="/group/:groupId" element={<GroupDashboard />} />
        <Route path="/group/:groupId/history" element={<GroupHistory />} />

        {/* Supporting Mini App Routes */}
        <Route path="/contributions" element={<ContributionsPage />} />
        <Route path="/history" element={<GroupHistory />} />
        <Route path="/groups" element={<GroupDashboard />} />
        <Route path="/groups/:id" element={<GroupDashboard />} />
        <Route path="/groups/:id/share" element={<InviteShare />} />
        <Route path="/groups/:id/history" element={<GroupHistory />} />
        <Route path="/status" element={<HomePage />} />
        <Route path="*" element={<NotFoundPage />} />
      </Routes>
    </AppShell>
  );
};

export default App;
