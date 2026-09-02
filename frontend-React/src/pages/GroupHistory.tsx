import React from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { useCycleHistory, useGroup } from '@/hooks';
import { useUiStore } from '@/store';
import {
  Card,
  Button,
  HexBadge,
  HexAvatar,
  LoadingSpinner,
  ErrorState,
  EmptyState,
} from '@/components';
import {
  ArrowLeft,
  History,
  Calendar,
  RefreshCw,
  CheckCircle2,
  Clock,
  ArrowRight,
} from 'lucide-react';
import type { CycleHistory } from '@/types';

export const GroupHistory: React.FC = () => {
  const { groupId, id } = useParams<{ groupId?: string; id?: string }>();
  const navigate = useNavigate();
  const { activeGroupId } = useUiStore();

  const currentGroupId = groupId || id || activeGroupId || '';

  // 1. Fetch Group details for context
  const {
    data: group,
    isLoading: isGroupLoading,
  } = useGroup(currentGroupId);

  // 2. Fetch authentic cycle history via GET /groups/{id}/history
  const {
    data: history,
    isLoading: isHistoryLoading,
    isError,
    error,
    refetch,
  } = useCycleHistory(currentGroupId);

  const isLoading = isGroupLoading || isHistoryLoading;

  // Handle case where user navigated without an active group
  if (!currentGroupId) {
    return (
      <div className="py-12 space-y-4 animate-fade-in">
        <EmptyState
          title="No Stokvel Selected"
          description="Please select or join a Stokvel to view its rotation cycle history."
          actionLabel="View Dashboard"
          onAction={() => navigate('/groups')}
        />
        <div className="text-center">
          <Button variant="secondary" onClick={() => navigate('/')} label="Return Home" />
        </div>
      </div>
    );
  }

  return (
    <div className="w-full space-y-5 animate-fade-in pb-8">
      {/* Top Navigation Header */}
      <div className="flex items-center justify-between">
        <Button
          variant="ghost"
          size="sm"
          onClick={() => navigate(currentGroupId ? `/group/${currentGroupId}` : '/')}
          leftIcon={<ArrowLeft className="w-4 h-4" />}
          label="Dashboard"
        />
        <HexBadge variant="gold" size="sm" icon={<History className="w-3 h-3 text-mtn-gold" />}>
          Cycle History
        </HexBadge>
      </div>

      {/* Group Context Banner */}
      {group && (
        <Card variant="elevated" glow="gold" className="p-5 md:p-6 relative overflow-hidden">
          <div className="flex items-center justify-between gap-3">
            <div>
              <span className="text-[10px] text-mtn-cream-secondary uppercase font-mono tracking-wider">
                Stokvel Audit Record
              </span>
              <h1 className="text-xl md:text-2xl font-black text-mtn-cream tracking-tight">
                {group.name}
              </h1>
              <p className="text-xs text-mtn-cream-secondary mt-0.5">
                {group.frequency} rotation cycles &bull; R{group.contributionAmount} per member
              </p>
            </div>

            <button
              onClick={() => refetch()}
              disabled={isLoading}
              className="p-2.5 rounded-xl bg-mtn-base border border-mtn-border text-mtn-cream-secondary hover:text-mtn-gold transition-colors disabled:opacity-50 shrink-0"
              title="Refresh History from Server"
            >
              <RefreshCw className={`w-4 h-4 ${isLoading ? 'animate-spin' : ''}`} />
            </button>
          </div>
        </Card>
      )}

      {/* STATE 1: Loading State */}
      {isLoading ? (
        <div className="py-16 text-center">
          <LoadingSpinner label="Retrieving cycle history from backend..." size="lg" />
        </div>
      ) : isError ? (
        /* STATE 2: Error State */
        <div className="py-8 space-y-4">
          <ErrorState
            title="Failed to Load Cycle History"
            error={error}
            onRetry={() => refetch()}
          />
          <div className="text-center">
            <Button
              variant="secondary"
              onClick={() => navigate(currentGroupId ? `/group/${currentGroupId}` : '/')}
              label="Back to Dashboard"
            />
          </div>
        </div>
      ) : !history || history.length === 0 ? (
        /* STATE 3: Empty State (ZERO fake data) */
        <div className="py-8">
          <EmptyState
            title="No cycle history available."
            description="This Stokvel is currently in its initial cycle. As rotation cycles conclude and disbursements are confirmed by the backend, records will appear here."
            actionLabel="Return to Dashboard"
            onAction={() => navigate(`/groups/${currentGroupId}`)}
          />
        </div>
      ) : (
        /* STATE 4: Populated State (Authentic Backend Records) */
        <div className="space-y-3">
          <div className="flex items-center justify-between px-1">
            <h2 className="text-xs font-bold text-mtn-cream-secondary uppercase tracking-wider">
              Rotation Cycles ({history.length})
            </h2>
            <span className="text-[10px] text-mtn-cream-muted font-mono">
              Live REST Records
            </span>
          </div>

          <div className="grid gap-3">
            {history.map((cycle: CycleHistory) => {
              const isOpen = cycle.status === 'open';
              const formattedDate = cycle.dueDate
                ? new Date(cycle.dueDate).toLocaleDateString(undefined, {
                    year: 'numeric',
                    month: 'short',
                    day: 'numeric',
                  })
                : 'Pending Date';

              return (
                <Card
                  key={cycle.cycleNumber}
                  variant="default"
                  className={`p-4 md:p-5 border transition-all ${
                    isOpen
                      ? 'border-mtn-green/40 bg-mtn-surface shadow-sm'
                      : 'border-mtn-border bg-mtn-base/80'
                  }`}
                >
                  <div className="flex items-start justify-between gap-3 mb-3 border-b border-mtn-border pb-3">
                    <div className="flex items-center gap-2.5">
                      <div className="w-8 h-8 rounded-lg bg-mtn-base border border-mtn-border flex items-center justify-center font-mono font-bold text-xs text-mtn-gold">
                        #{cycle.cycleNumber}
                      </div>
                      <div>
                        <h3 className="text-sm font-bold text-mtn-cream">
                          Cycle {cycle.cycleNumber}
                        </h3>
                        <p className="text-[11px] text-mtn-cream-secondary flex items-center gap-1 mt-0.5">
                          <Calendar className="w-3 h-3 text-mtn-gold" />
                          Due: {formattedDate}
                        </p>
                      </div>
                    </div>

                    <HexBadge
                      variant={isOpen ? 'green' : 'neutral'}
                      size="sm"
                      icon={
                        isOpen ? (
                          <Clock className="w-3 h-3 text-mtn-green" />
                        ) : (
                          <CheckCircle2 className="w-3 h-3 text-mtn-cream-secondary" />
                        )
                      }
                    >
                      {isOpen ? 'Active Cycle' : 'Completed'}
                    </HexBadge>
                  </div>

                  {/* Recipient & Amount Details */}
                  <div className="flex items-center justify-between gap-3 bg-mtn-surface/50 rounded-xl p-3 border border-mtn-border/50">
                    <div className="flex items-center gap-2.5 min-w-0">
                      <HexAvatar
                        name={cycle.payoutRecipient || 'Member'}
                        size="sm"
                        status={isOpen ? 'success' : 'none'}
                      />
                      <div className="min-w-0">
                        <span className="text-[10px] text-mtn-cream-secondary uppercase font-medium block">
                          Payout Recipient
                        </span>
                        <p className="text-xs font-bold text-mtn-cream truncate">
                          {cycle.payoutRecipient || 'Unassigned'}
                        </p>
                      </div>
                    </div>

                    <div className="text-right shrink-0">
                      <span className="text-[10px] text-mtn-cream-secondary uppercase font-medium block">
                        Total Contributed
                      </span>
                      <span className="text-sm font-black text-mtn-gold font-mono">
                        R{cycle.totalContributed}
                      </span>
                    </div>
                  </div>
                </Card>
              );
            })}
          </div>
        </div>
      )}

      {/* Footer Navigation CTA */}
      <div className="pt-2 text-center">
        <Button
          variant="secondary"
          size="lg"
          fullWidth
          onClick={() => navigate(`/group/${currentGroupId}`)}
          rightIcon={<ArrowRight className="w-4 h-4" />}
          label="Back to Group Dashboard"
        />
      </div>
    </div>
  );
};

export default GroupHistory;
