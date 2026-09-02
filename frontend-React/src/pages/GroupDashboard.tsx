import React, { useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { useGroup, useContributions, useCycleHistory } from '@/hooks';
import { useUiStore } from '@/store';
import {
  Card,
  Button,
  HexBadge,
  HexAvatar,
  LoadingSpinner,
  ErrorState,
  EmptyState,
  DemoTriggerPanel,
  LedgerTable,
  PayoutRotationBanner,
} from '@/components';
import { maskMsisdn } from '@/utils';
import {
  Users,
  Calendar,
  DollarSign,
  Share2,
  Copy,
  Check,
  RefreshCw,
  PlusCircle,
  ArrowRight,
  TrendingUp,
  Receipt,
  ShieldCheck,
  Gift,
  FlaskConical,
} from 'lucide-react';
import type { CycleHistory } from '@/types';

export const GroupDashboard: React.FC = () => {
  const { groupId, id } = useParams<{ groupId?: string; id?: string }>();
  const navigate = useNavigate();
  const { activeGroupId, setActiveGroupId } = useUiStore();

  const currentGroupId = groupId || id || activeGroupId || '';
  const [copiedCode, setCopiedCode] = useState(false);
  const [activeTab, setActiveTab] = useState<'ledger' | 'rotation' | 'members' | 'info'>('ledger');
  const [showDemoPanel, setShowDemoPanel] = useState(false);

  // Sync activeGroupId in UI store if route ID changed
  React.useEffect(() => {
    if (id && id !== activeGroupId) {
      setActiveGroupId(id);
    }
  }, [id, activeGroupId, setActiveGroupId]);

  // Real backend queries via TanStack Query
  const {
    data: group,
    isLoading: isGroupLoading,
    isError: isGroupError,
    error: groupError,
    refetch: refetchGroup,
  } = useGroup(currentGroupId);

  const {
    data: contributions,
    isLoading: isContribLoading,
    refetch: refetchContrib,
  } = useContributions(currentGroupId);

  // Real backend cycle history & payout rotation records
  const {
    data: cycleHistory,
    isLoading: isHistoryLoading,
    isError: isHistoryError,
    error: historyError,
    refetch: refetchHistory,
  } = useCycleHistory(currentGroupId);

  const handleCopyCode = (code: string) => {
    navigator.clipboard.writeText(code);
    setCopiedCode(true);
    setTimeout(() => setCopiedCode(false), 2500);
  };

  // State: No Group Selected or in Route
  if (!currentGroupId) {
    return (
      <div className="py-12 space-y-4">
        <EmptyState
          title="No Stokvel Selected"
          description="Please select a Stokvel group or join one to view its live ledger."
          actionLabel="Join a Stokvel"
          onAction={() => navigate('/join')}
        />
        <div className="text-center">
          <Button
            variant="outline"
            size="sm"
            onClick={() => navigate('/create')}
            leftIcon={<PlusCircle className="w-4 h-4" />}
            label="Create New Stokvel"
          />
        </div>
      </div>
    );
  }

  // State: Loading Group
  if (isGroupLoading) {
    return (
      <div className="py-16 text-center">
        <LoadingSpinner label="Fetching group details & ledger from backend..." size="lg" />
      </div>
    );
  }

  // State: Error Loading Group
  if (isGroupError || !group) {
    return (
      <div className="py-12 space-y-4">
        <ErrorState
          title="Unable to Load Stokvel"
          error={groupError || 'The requested Stokvel could not be found on the server.'}
          onRetry={() => refetchGroup()}
        />
        <div className="text-center">
          <Button variant="secondary" onClick={() => navigate('/')} label="Return to Home" />
        </div>
      </div>
    );
  }

  // Calculate target rotation pool amount based strictly on members and contribution
  const totalPool = (group.members?.length || 0) * group.contributionAmount;

  // Derive next payout recipient strictly from backend domain cycle records
  const activeCycle: CycleHistory | undefined =
    cycleHistory?.find((c) => c.status === 'open') ||
    (cycleHistory && cycleHistory.length > 0 ? cycleHistory[cycleHistory.length - 1] : undefined);

  const nextPayoutRecipient = activeCycle?.payoutRecipient;

  return (
    <div className="w-full space-y-5 animate-fade-in pb-8">
      {/* Group Summary Banner */}
      <Card variant="elevated" glow="gold" className="p-5 md:p-6 relative overflow-hidden">
        <div className="flex items-start justify-between gap-3 mb-3">
          <div>
            <div className="flex items-center gap-2 mb-1">
              <h1 className="text-xl md:text-2xl font-black text-mtn-cream tracking-tight">
                {group.name}
              </h1>
              <HexBadge variant="green" size="sm">
                Active
              </HexBadge>
            </div>
            <p className="text-xs text-mtn-cream-secondary">
              Started {new Date(group.startDate || group.createdAt).toLocaleDateString()} &bull;{' '}
              <span className="capitalize font-bold text-mtn-cream">{group.frequency}</span> Rotation
            </p>
          </div>

          <div className="flex items-center gap-2">
            <button
              onClick={() => setShowDemoPanel(!showDemoPanel)}
              className={`min-h-[44px] px-3 rounded-xl border text-xs font-bold transition-all flex items-center justify-center gap-1.5 active:scale-95 touch-manipulation ${
                showDemoPanel
                  ? 'bg-mtn-gold text-mtn-base border-mtn-gold shadow-sm'
                  : 'bg-mtn-base border-mtn-border text-mtn-cream-secondary hover:text-mtn-gold active:bg-mtn-surface'
              }`}
              title="Toggle Demo Controls"
              aria-label="Toggle Demo Controls"
            >
              <FlaskConical className="w-4 h-4" />
              <span className="hidden sm:inline">Demo</span>
            </button>

            <button
              onClick={() => navigate(`/invite/${group.id}`, { state: { group } })}
              className="min-h-[44px] min-w-[44px] rounded-xl bg-mtn-base border border-mtn-border text-mtn-gold hover:bg-mtn-surface active:scale-95 transition-all flex items-center justify-center shrink-0 touch-manipulation"
              title="Share Invite Code"
              aria-label="Share Invite Code"
            >
              <Share2 className="w-4 h-4" />
            </button>
          </div>
        </div>

        {/* Quick Stats Grid */}
        <div className="grid grid-cols-3 gap-2.5 pt-3 border-t border-mtn-border">
          <div className="p-2.5 bg-mtn-base rounded-xl border border-mtn-border text-center">
            <DollarSign className="w-4 h-4 text-mtn-gold mx-auto mb-0.5" />
            <p className="text-[10px] text-mtn-cream-secondary font-medium">Per Member</p>
            <p className="text-xs font-black text-mtn-cream">R{group.contributionAmount}</p>
          </div>

          <div className="p-2.5 bg-mtn-base rounded-xl border border-mtn-border text-center">
            <TrendingUp className="w-4 h-4 text-mtn-green mx-auto mb-0.5" />
            <p className="text-[10px] text-mtn-cream-secondary font-medium">Cycle Pool</p>
            <p className="text-xs font-black text-mtn-gold">R{totalPool}</p>
          </div>

          <div className="p-2.5 bg-mtn-base rounded-xl border border-mtn-border text-center">
            <Users className="w-4 h-4 text-mtn-blue mx-auto mb-0.5" />
            <p className="text-[10px] text-mtn-cream-secondary font-medium">Roster</p>
            <p className="text-xs font-black text-mtn-cream">{group.members?.length || 0}</p>
          </div>
        </div>

        {/* Invite Code Tag */}
        <div className="mt-3 pt-3 border-t border-mtn-border/50 flex items-center justify-between text-xs">
          <span className="text-mtn-cream-secondary font-mono text-[11px]">
            Code: <strong className="text-mtn-gold font-bold">{group.inviteCode}</strong>
          </span>
          <button
            onClick={() => handleCopyCode(group.inviteCode)}
            className="inline-flex items-center gap-1.5 py-1 px-2.5 rounded-lg bg-mtn-base border border-mtn-border/60 text-[11px] font-bold text-mtn-cream hover:text-mtn-gold active:bg-mtn-surface transition-all touch-manipulation min-h-[36px]"
          >
            {copiedCode ? (
              <>
                <Check className="w-3.5 h-3.5 text-mtn-green" /> Copied
              </>
            ) : (
              <>
                <Copy className="w-3.5 h-3.5 text-mtn-cream-secondary" /> Copy Code
              </>
            )}
          </button>
        </div>
      </Card>

      {/* Demo Trigger Panel */}
      {showDemoPanel && (
        <DemoTriggerPanel
          groupId={currentGroupId}
          cycleId={activeCycle?.cycleNumber?.toString() || currentGroupId}
          members={group.members}
          className="animate-fade-in"
        />
      )}

      {/* Payout Rotation Spotlight Banner (Backend-Driven) */}
      <PayoutRotationBanner
        recipientName={nextPayoutRecipient}
        cycleNumber={activeCycle?.cycleNumber}
        dueDate={activeCycle?.dueDate}
        amount={activeCycle?.totalContributed || totalPool}
        isLoading={isHistoryLoading}
        error={isHistoryError ? historyError : null}
      />

      {/* Navigation Tabs (Mobile-Friendly 44px Height) */}
      <div className="flex bg-mtn-surface border border-mtn-border rounded-xl p-1 gap-1">
        <button
          onClick={() => setActiveTab('ledger')}
          className={`flex-1 py-2.5 min-h-[44px] rounded-lg text-xs font-bold transition-all flex items-center justify-center gap-1.5 touch-manipulation active:scale-[0.98] ${
            activeTab === 'ledger'
              ? 'bg-mtn-gold text-mtn-base shadow-sm'
              : 'text-mtn-cream-secondary hover:text-mtn-cream active:bg-mtn-card'
          }`}
        >
          <Receipt className="w-3.5 h-3.5" />
          <span>Ledger</span>
        </button>

        <button
          onClick={() => setActiveTab('rotation')}
          className={`flex-1 py-2.5 min-h-[44px] rounded-lg text-xs font-bold transition-all flex items-center justify-center gap-1.5 touch-manipulation active:scale-[0.98] ${
            activeTab === 'rotation'
              ? 'bg-mtn-gold text-mtn-base shadow-sm'
              : 'text-mtn-cream-secondary hover:text-mtn-cream active:bg-mtn-card'
          }`}
        >
          <Gift className="w-3.5 h-3.5" />
          <span>History</span>
        </button>

        <button
          onClick={() => setActiveTab('members')}
          className={`flex-1 py-2.5 min-h-[44px] rounded-lg text-xs font-bold transition-all flex items-center justify-center gap-1.5 touch-manipulation active:scale-[0.98] ${
            activeTab === 'members'
              ? 'bg-mtn-gold text-mtn-base shadow-sm'
              : 'text-mtn-cream-secondary hover:text-mtn-cream active:bg-mtn-card'
          }`}
        >
          <Users className="w-3.5 h-3.5" />
          <span>Members</span>
        </button>

        <button
          onClick={() => setActiveTab('info')}
          className={`flex-1 py-2.5 min-h-[44px] rounded-lg text-xs font-bold transition-all flex items-center justify-center gap-1.5 touch-manipulation active:scale-[0.98] ${
            activeTab === 'info'
              ? 'bg-mtn-gold text-mtn-base shadow-sm'
              : 'text-mtn-cream-secondary hover:text-mtn-cream active:bg-mtn-card'
          }`}
        >
          <Calendar className="w-3.5 h-3.5" />
          <span>Schedule</span>
        </button>
      </div>

      {/* TAB 1: Real Contribution Ledger Table */}
      {activeTab === 'ledger' && (
        <LedgerTable
          contributions={contributions}
          isLoading={isContribLoading}
          onRefresh={() => refetchContrib()}
          onContribute={() => navigate('/contributions')}
        />
      )}

      {/* TAB 2: Rotation & Cycle History */}
      {activeTab === 'rotation' && (
        <Card variant="default" className="p-4 md:p-5 space-y-4">
          <div className="flex items-center justify-between">
            <div>
              <h2 className="text-sm font-bold text-mtn-cream flex items-center gap-2">
                <Gift className="w-4 h-4 text-mtn-green" />
                Cycle Rotation History
              </h2>
              <p className="text-[11px] text-mtn-cream-secondary">
                Official disbursement records from backend
              </p>
            </div>
            <button
              onClick={() => refetchHistory()}
              disabled={isHistoryLoading}
              className="p-1.5 rounded-lg bg-mtn-base border border-mtn-border text-mtn-cream-secondary hover:text-mtn-cream transition-colors disabled:opacity-50"
              title="Refresh Cycles"
            >
              <RefreshCw className={`w-3.5 h-3.5 ${isHistoryLoading ? 'animate-spin' : ''}`} />
            </button>
          </div>

          {isHistoryLoading ? (
            <div className="py-8">
              <LoadingSpinner label="Fetching cycle history..." size="md" />
            </div>
          ) : isHistoryError ? (
            <ErrorState
              title="Failed to Load History"
              error={historyError}
              onRetry={() => refetchHistory()}
            />
          ) : !cycleHistory || cycleHistory.length === 0 ? (
            <EmptyState
              title="No Past Cycles"
              description="This Stokvel is currently in its initial cycle. Completed cycles will appear here."
            />
          ) : (
            <div className="divide-y divide-mtn-border/60">
              {cycleHistory.map((cycle) => (
                <div key={cycle.cycleNumber} className="py-3.5 flex items-center justify-between">
                  <div>
                    <div className="flex items-center gap-2">
                      <span className="text-xs font-bold text-mtn-cream">Cycle #{cycle.cycleNumber}</span>
                      <HexBadge variant={cycle.status === 'open' ? 'green' : 'neutral'} size="sm">
                        {cycle.status === 'open' ? 'Current Cycle' : 'Disbursed'}
                      </HexBadge>
                    </div>
                    <p className="text-[11px] text-mtn-cream-secondary mt-0.5">
                      Recipient: <strong className="text-mtn-cream">{cycle.payoutRecipient}</strong> &bull; Due:{' '}
                      {new Date(cycle.dueDate).toLocaleDateString()}
                    </p>
                  </div>
                  <div className="text-right">
                    <span className="text-xs font-bold text-mtn-gold font-mono">
                      R{cycle.totalContributed}
                    </span>
                  </div>
                </div>
              ))}
            </div>
          )}
        </Card>
      )}

      {/* TAB 3: Members Roster */}
      {activeTab === 'members' && (
        <Card variant="default" className="p-4 md:p-5 space-y-4">
          <div className="flex items-center justify-between">
            <div>
              <h2 className="text-sm font-bold text-mtn-cream flex items-center gap-2">
                <Users className="w-4 h-4 text-mtn-green" />
                Registered Members
              </h2>
              <p className="text-[11px] text-mtn-cream-secondary">
                Turn order for cycle disbursements
              </p>
            </div>
            <Button
              variant="outline"
              size="sm"
              onClick={() => handleCopyCode(group.inviteCode)}
              leftIcon={<PlusCircle className="w-3.5 h-3.5" />}
              label="Invite More"
            />
          </div>

          <div className="divide-y divide-mtn-border/60">
            {group.members && group.members.length > 0 ? (
              group.members.map((member, index) => (
                <div key={member.id} className="py-3 flex items-center justify-between">
                  <div className="flex items-center gap-3">
                    <HexAvatar name={member.displayName} size="sm" />
                    <div>
                      <p className="text-xs font-bold text-mtn-cream">{member.displayName}</p>
                      <p className="text-[10px] text-mtn-cream-secondary font-mono">{maskMsisdn(member.msisdn)}</p>
                    </div>
                  </div>
                  <div className="text-right">
                    <span className="text-[10px] font-bold text-mtn-gold bg-mtn-gold-muted border border-mtn-gold/40 px-2 py-0.5 rounded">
                      Rotation #{member.joinOrder || index + 1}
                    </span>
                  </div>
                </div>
              ))
            ) : (
              <EmptyState
                title="No Members Joined"
                description="Share your invite code to invite members into this Stokvel."
                actionLabel="Copy Invite Code"
                onAction={() => handleCopyCode(group.inviteCode)}
              />
            )}
          </div>
        </Card>
      )}

      {/* TAB 4: Schedule & Info */}
      {activeTab === 'info' && (
        <Card variant="default" className="p-4 md:p-5 space-y-4">
          <div className="flex items-center gap-2 font-bold text-sm text-mtn-cream">
            <Calendar className="w-4 h-4 text-mtn-blue" />
            <h3>Stokvel Schedule & Rules</h3>
          </div>

          <div className="bg-mtn-base rounded-xl p-4 border border-mtn-border space-y-3 text-xs text-mtn-cream-secondary">
            <div className="flex justify-between border-b border-mtn-border/40 pb-2">
              <span>Contribution Frequency</span>
              <strong className="text-mtn-cream capitalize">{group.frequency}</strong>
            </div>
            <div className="flex justify-between border-b border-mtn-border/40 pb-2">
              <span>Contribution per Member</span>
              <strong className="text-mtn-gold font-mono">R{group.contributionAmount}</strong>
            </div>
            <div className="flex justify-between border-b border-mtn-border/40 pb-2">
              <span>Cycle Start Date</span>
              <strong className="text-mtn-cream font-mono">
                {new Date(group.startDate || group.createdAt).toLocaleDateString()}
              </strong>
            </div>
            <div className="flex justify-between">
              <span>Total Payout Pot</span>
              <strong className="text-mtn-green font-mono">R{totalPool}</strong>
            </div>
          </div>

          <div className="p-3 bg-mtn-surface border border-mtn-border rounded-xl flex items-center gap-3">
            <ShieldCheck className="w-5 h-5 text-mtn-green shrink-0" />
            <p className="text-[11px] text-mtn-cream-secondary">
              Disbursements occur automatically at the end of each {group.frequency} cycle via MTN MoMo.
            </p>
          </div>
        </Card>
      )}

      {/* Action Footer CTA */}
      <div className="pt-2">
        <Button
          variant="primary"
          size="lg"
          fullWidth
          onClick={() => navigate('/contributions')}
          rightIcon={<ArrowRight className="w-4 h-4" />}
          label="Make MoMo Contribution"
        />
      </div>
    </div>
  );
};

export default GroupDashboard;
