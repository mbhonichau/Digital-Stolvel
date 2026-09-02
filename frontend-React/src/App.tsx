import React from 'react';
import { Routes, Route } from 'react-router-dom';
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
} from '@/pages';

export const App: React.FC = () => {
  return (
    <AppShell>
      <Routes>
        {/* Phase 18 Canonical Routes */}
        <Route path="/" element={<Landing />} />
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
