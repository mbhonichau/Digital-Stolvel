import React, { useState } from 'react';
import { useHealthCheck } from '@/hooks';
import { Card, Button, Input, HexAvatar, HexBadge } from '@/components';
import { ShieldCheck, Server, Layers, CheckCircle2, XCircle, Sparkles, Activity } from 'lucide-react';

export const HomePage: React.FC = () => {
  const apiBaseUrl = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api';
  const [sampleInput, setSampleInput] = useState('');

  // Strict Data Flow: Page -> TanStack Query Hook -> API Service -> Axios -> Spring Boot
  const { data: healthData, isLoading, isError, error, refetch } = useHealthCheck(false);

  return (
    <main className="w-full space-y-5">
      {/* Header Banner — Mzansi Futurism Hero */}
      <Card variant="elevated" glow="gold" className="p-6 md:p-8 relative overflow-hidden border-mtn-border">
        <div className="absolute -top-10 -right-10 w-48 h-48 bg-mtn-gold-muted rounded-full blur-3xl pointer-events-none" />
        <div className="flex items-center gap-3 mb-3">
          <HexBadge variant="gold" size="sm" label="Phase 7" />
          <span className="text-xs text-mtn-cream-secondary font-mono flex items-center gap-1">
            <Sparkles className="w-3.5 h-3.5 text-mtn-gold" />
            Reusable Generic UI Layer
          </span>
        </div>
        <h1 className="text-2xl md:text-4xl font-extrabold text-mtn-cream tracking-tight">
          MTN MoMo Stokvel Mini App
        </h1>
        <p className="text-sm text-mtn-cream-secondary mt-2 max-w-xl leading-relaxed">
          High-performance fintech mini app styled with the Mzansi Futurism design system, decoupled generic UI components, and zero mock data.
        </p>
      </Card>

      {/* Architecture & Compliance Checklist */}
      <div className="grid md:grid-cols-2 gap-4">
        {/* Core Rules & Zero-Mock Enforcement */}
        <Card variant="default" className="p-5">
          <div className="flex items-center gap-2 mb-3 font-semibold text-mtn-green">
            <ShieldCheck className="w-5 h-5 text-mtn-green" />
            <h2 className="text-base text-mtn-cream">Zero-Mock Compliance</h2>
          </div>
          <ul className="space-y-2.5 text-xs text-mtn-cream-secondary">
            <li className="flex items-center gap-2">
              <CheckCircle2 className="w-4 h-4 text-mtn-green shrink-0" />
              <span>Zero mock API responses or fixture simulations</span>
            </li>
            <li className="flex items-center gap-2">
              <CheckCircle2 className="w-4 h-4 text-mtn-green shrink-0" />
              <span>No mock users, groups, or fake entities</span>
            </li>
            <li className="flex items-center gap-2">
              <CheckCircle2 className="w-4 h-4 text-mtn-green shrink-0" />
              <span>Strict separation: generic UI components know nothing of backend</span>
            </li>
            <li className="flex items-center gap-2">
              <CheckCircle2 className="w-4 h-4 text-mtn-green shrink-0" />
              <span>Mzansi Futurism design tokens (no raw hex styling)</span>
            </li>
          </ul>
        </Card>

        {/* Backend Configuration Info */}
        <Card variant="default" className="p-5 flex flex-col justify-between">
          <div>
            <div className="flex items-center gap-2 mb-3 font-semibold text-mtn-blue">
              <Server className="w-5 h-5 text-mtn-blue" />
              <h2 className="text-base text-mtn-cream">Backend REST Target</h2>
            </div>
            <div className="bg-mtn-base border border-mtn-border rounded-lg p-3 font-mono text-xs text-mtn-cream break-all">
              <span className="text-mtn-cream-muted select-none">VITE_API_BASE_URL=</span>
              <span className="text-mtn-gold font-semibold">{apiBaseUrl}</span>
            </div>
          </div>

          <div className="mt-4 pt-3 border-t border-mtn-border flex items-center justify-between">
            <Button
              onClick={() => refetch()}
              loading={isLoading}
              variant="primary"
              size="sm"
              leftIcon={<Activity className="w-3.5 h-3.5" />}
              label="Test API Connection"
            />

            {healthData && (
              <span className="inline-flex items-center gap-1 text-xs font-medium text-mtn-green">
                <CheckCircle2 className="w-3.5 h-3.5" /> Connected ({healthData.status})
              </span>
            )}
            {isError && (
              <span className="inline-flex items-center gap-1 text-xs font-medium text-mtn-red" title={(error as Error)?.message}>
                <XCircle className="w-3.5 h-3.5" /> Offline / Unreachable
              </span>
            )}
          </div>
        </Card>
      </div>

      {/* Generic UI Components Showcase */}
      <Card variant="default" className="p-5 space-y-4">
        <div className="flex items-center justify-between border-b border-mtn-border pb-3">
          <div className="flex items-center gap-2 font-semibold text-mtn-cream">
            <Layers className="w-5 h-5 text-mtn-gold" />
            <h2 className="text-base">Reusable Generic UI Component Showcase</h2>
          </div>
          <HexBadge variant="neutral" size="sm" label="Generic Props Only" />
        </div>

        <div className="grid md:grid-cols-3 gap-4 pt-1">
          {/* HexAvatars */}
          <div className="bg-mtn-base p-4 rounded-xl border border-mtn-border space-y-3">
            <h3 className="text-xs font-bold text-mtn-cream-secondary uppercase tracking-wider">HexAvatar</h3>
            <div className="flex items-center gap-3">
              <HexAvatar name="MoMo" size="md" status="online" />
              <HexAvatar name="Zustand" size="md" status="success" />
              <HexAvatar name="React" size="sm" status="none" />
            </div>
            <p className="text-[11px] text-mtn-cream-muted">Pure geometric initials / image avatar</p>
          </div>

          {/* HexBadges */}
          <div className="bg-mtn-base p-4 rounded-xl border border-mtn-border space-y-3">
            <h3 className="text-xs font-bold text-mtn-cream-secondary uppercase tracking-wider">HexBadge</h3>
            <div className="flex flex-wrap gap-1.5">
              <HexBadge variant="gold" label="Gold" />
              <HexBadge variant="green" label="Success" />
              <HexBadge variant="red" label="Failure" />
              <HexBadge variant="blue" label="Tertiary" />
            </div>
            <p className="text-[11px] text-mtn-cream-muted">Status indicators with Mzansi accents</p>
          </div>

          {/* Generic Buttons */}
          <div className="bg-mtn-base p-4 rounded-xl border border-mtn-border space-y-3">
            <h3 className="text-xs font-bold text-mtn-cream-secondary uppercase tracking-wider">Buttons</h3>
            <div className="flex flex-wrap gap-2">
              <Button size="sm" variant="primary" label="Primary" />
              <Button size="sm" variant="secondary" label="Secondary" />
              <Button size="sm" variant="outline" label="Outline" />
            </div>
            <p className="text-[11px] text-mtn-cream-muted">Variants with loading and icon props</p>
          </div>
        </div>

        {/* Generic Input Component */}
        <div className="pt-2">
          <Input
            label="Generic Input Demo"
            placeholder="Type anything to test input component..."
            value={sampleInput}
            onChange={(e) => setSampleInput(e.target.value)}
            helperText="Generic controlled input accepting props without backend coupling."
          />
        </div>
      </Card>
    </main>
  );
};

export default HomePage;
