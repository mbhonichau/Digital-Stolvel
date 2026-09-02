import React from 'react';
import { Routes, Route } from 'react-router-dom';
import { AppShell } from '@/components/layout/AppShell';
import {
  LandingPage,
  CreateGroupPage,
  InvitePage,
  JoinGroupPage,
  DashboardPage,
  HistoryPage,
  ContributionsPage,
  HomePage,
  NotFoundPage,
} from '@/pages';

export const App: React.FC = () => {
  return (
    <AppShell>
      <Routes>
        {/* Canonical Target Production Routes */}
        <Route path="/" element={<LandingPage />} />
        <Route path="/create" element={<CreateGroupPage />} />
        <Route path="/invite/:groupId" element={<InvitePage />} />
        <Route path="/join" element={<JoinGroupPage />} />
        <Route path="/group/:groupId" element={<DashboardPage />} />
        <Route path="/group/:groupId/history" element={<HistoryPage />} />

        {/* Supporting Mini App Alias Routes */}
        <Route path="/contributions" element={<ContributionsPage />} />
        <Route path="/history" element={<HistoryPage />} />
        <Route path="/groups" element={<DashboardPage />} />
        <Route path="/groups/:id" element={<DashboardPage />} />
        <Route path="/groups/:id/share" element={<InvitePage />} />
        <Route path="/groups/:id/history" element={<HistoryPage />} />
        <Route path="/status" element={<HomePage />} />
        <Route path="*" element={<NotFoundPage />} />
      </Routes>
    </AppShell>
  );
};

export default App;
