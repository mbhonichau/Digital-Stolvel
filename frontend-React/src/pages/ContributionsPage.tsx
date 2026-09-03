import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useGroup, useGroupCycles, useTriggerContribution } from '@/hooks';
import { useUiStore } from '@/store';
import {
  Card,
  Button,
  HexBadge,
  LoadingSpinner,
  ErrorState,
  EmptyState,
} from '@/components';
import { maskMsisdn } from '@/utils';
import {
  ArrowLeft,
  PiggyBank,
  CheckCircle2,
  ShieldCheck,
  ArrowRight,
} from 'lucide-react';
import type { ContributionStatus } from '@/types';

export const ContributionsPage: React.FC = () => {
  const navigate = useNavigate();
  const { activeGroupId } = useUiStore();
  const triggerContributionMutation = useTriggerContribution();

  // Load group details for active group
  const {
    data: group,
    isLoading: isGroupLoading,
    isError: isGroupError,
    error: groupError,
    refetch: refetchGroup,
  } = useGroup(activeGroupId || undefined);
  const { data: cycles, isLoading: isCyclesLoading, isError: isCyclesError, error: cyclesError, refetch: refetchCycles } = useGroupCycles(activeGroupId || undefined);

  const [selectedMemberId, setSelectedMemberId] = useState<string>('');
  const [successStatus, setSuccessStatus] = useState<ContributionStatus | null>(null);

  // Set default selected member when group loads
  React.useEffect(() => {
    if (group?.members && group.members.length > 0 && !selectedMemberId) {
      setSelectedMemberId(group.members[0].id);
    }
  }, [group, selectedMemberId]);

  // Handle: No active group selected
  if (!activeGroupId) {
    return (
      <div className="py-12 space-y-4 animate-fade-in">
        <EmptyState
          title="No Active Stokvel Selected"
          description="Please join or select a Stokvel group to make a contribution payment."
          actionLabel="Select Stokvel"
          onAction={() => navigate('/groups')}
        />
        <div className="text-center">
          <Button variant="secondary" onClick={() => navigate('/')} label="Return Home" />
        </div>
      </div>
    );
  }

  // Handle: Loading Group State
  if (isGroupLoading || isCyclesLoading) {
    return (
      <div className="py-16 text-center">
        <LoadingSpinner label="Fetching active Stokvel details..." size="lg" />
      </div>
    );
  }

  // Handle: Error Loading Group State
  if (isGroupError || isCyclesError || !group) {
    return (
      <div className="py-12 space-y-4">
        <ErrorState
          title="Unable to Load Group Information"
          error={groupError || cyclesError || "We couldn't load the group information. Please try again."}
          onRetry={() => { refetchGroup(); refetchCycles(); }}
        />
        <div className="text-center">
          <Button variant="secondary" onClick={() => navigate('/')} label="Return Home" />
        </div>
      </div>
    );
  }

  // Handle: Empty Members in Group
  if (!group.members || group.members.length === 0) {
    return (
      <div className="py-8 space-y-4">
        <EmptyState
          title="No Members in Group"
          description="You need to invite members before contributions can be collected."
          actionLabel="Invite Members"
          onAction={() => navigate(`/invite/${group.id}`, { state: { group } })}
        />
        <div className="text-center">
          <Button
            variant="secondary"
            onClick={() => navigate(`/group/${group.id}`)}
            label="Back to Dashboard"
          />
        </div>
      </div>
    );
  }

  const selectedMember = group.members.find((m) => m.id === selectedMemberId) || group.members[0];
  const activeCycle = cycles?.find((cycle) => cycle.status === 'active');

  if (!activeCycle) {
    return <EmptyState title="No Active Cycle" description="A cycle must be created before members can contribute." />;
  }

  const handlePayContribution = (e?: React.FormEvent) => {
    e?.preventDefault();
    setSuccessStatus(null);

    triggerContributionMutation.mutate(
      {
        cycleId: activeCycle.id,
        memberId: selectedMember.id,
      },
      {
        onSuccess: (data) => {
          setSuccessStatus(data);
        },
      }
    );
  };

  return (
    <div className="w-full space-y-5 animate-fade-in pb-8">
      {/* Header */}
      <div className="flex items-center justify-between">
        <Button
          variant="ghost"
          size="sm"
          onClick={() => navigate(`/group/${group.id}`)}
          leftIcon={<ArrowLeft className="w-4 h-4" />}
          label="Dashboard"
        />
        <HexBadge variant="gold" size="sm" icon={<PiggyBank className="w-3 h-3 text-mtn-gold" />}>
          MoMo Payment
        </HexBadge>
      </div>

      {/* Main Contribution Card */}
      <Card variant="elevated" glow="gold" className="p-6 md:p-8">
        <div className="flex items-center gap-3 mb-3 text-mtn-gold">
          <div className="w-10 h-10 rounded-xl bg-mtn-gold/10 border border-mtn-gold/30 flex items-center justify-center text-mtn-gold">
            <PiggyBank className="w-6 h-6" />
          </div>
          <div>
            <h1 className="text-xl font-black text-mtn-cream">Make Contribution</h1>
            <p className="text-xs text-mtn-cream-secondary">{group.name}</p>
          </div>
        </div>

        {/* API Error State Banner */}
        {triggerContributionMutation.isError && (
          <div className="mb-4">
            <ErrorState
              title="Contribution Request Failed"
              error={triggerContributionMutation.error || 'Payment request could not be completed. Please try again.'}
              onRetry={() => handlePayContribution()}
            />
          </div>
        )}

        {/* Success Confirmation State */}
        {successStatus ? (
          <div className="p-5 bg-mtn-base rounded-2xl border border-mtn-green/50 text-center space-y-4 my-2">
            <div className="w-12 h-12 rounded-full bg-mtn-green-muted text-mtn-green flex items-center justify-center mx-auto shadow-sm">
              <CheckCircle2 className="w-7 h-7" />
            </div>

            <div>
              <h2 className="text-base font-black text-mtn-cream">Payment Initiated</h2>
              <p className="text-xs text-mtn-cream-secondary mt-1">
                A MoMo push prompt has been dispatched to {successStatus.displayName}.
              </p>
            </div>

            <div className="bg-mtn-surface rounded-xl p-3 border border-mtn-border text-xs space-y-1.5 text-left font-mono">
              <div className="flex justify-between text-mtn-cream-secondary">
                <span>Amount:</span>
                <strong className="text-mtn-gold font-bold">R{successStatus.amount}</strong>
              </div>
              <div className="flex justify-between text-mtn-cream-secondary">
                <span>Status:</span>
                <strong className="text-mtn-green capitalize">{successStatus.status}</strong>
              </div>
              {successStatus.momoReference && (
                <div className="flex justify-between text-mtn-cream-secondary">
                  <span>Reference:</span>
                  <span className="text-mtn-cream truncate">{successStatus.momoReference}</span>
                </div>
              )}
            </div>

            <div className="flex gap-2">
              <Button
                variant="primary"
                size="md"
                fullWidth
                onClick={() => navigate(`/group/${group.id}`)}
                rightIcon={<ArrowRight className="w-4 h-4" />}
                label="View in Ledger"
              />
              <Button
                variant="secondary"
                size="md"
                onClick={() => setSuccessStatus(null)}
                label="New Contribution"
              />
            </div>
          </div>
        ) : (
          /* Payment Form */
          <form onSubmit={handlePayContribution} className="mt-4 space-y-4">
            {/* Member Selector */}
            <div className="space-y-1.5">
              <label className="text-xs font-bold text-mtn-cream tracking-wide">
                Select Contributing Member
              </label>
              <select
                value={selectedMemberId}
                onChange={(e) => setSelectedMemberId(e.target.value)}
                disabled={triggerContributionMutation.isPending}
                className="w-full bg-mtn-base text-mtn-cream text-xs rounded-xl border border-mtn-border p-3 outline-none focus:border-mtn-gold focus:ring-2 focus:ring-mtn-gold/30"
              >
                {group.members.map((member) => (
                  <option key={member.id} value={member.id}>
                    {member.displayName} ({maskMsisdn(member.msisdn)})
                  </option>
                ))}
              </select>
            </div>

            {/* Amount Summary */}
            <div className="bg-mtn-base rounded-xl p-4 border border-mtn-border flex items-center justify-between">
              <div>
                <span className="text-[10px] text-mtn-cream-secondary uppercase font-medium block">
                  Fixed Cycle Amount
                </span>
                <span className="text-xs text-mtn-cream">
                  {group.frequency} contribution
                </span>
              </div>
              <div className="text-right">
                <span className="text-xl font-black text-mtn-gold font-mono">
                  R{group.contributionAmount}
                </span>
              </div>
            </div>

            {/* Terms Notice */}
            <div className="p-3 bg-mtn-surface border border-mtn-border rounded-xl flex items-center gap-3">
              <ShieldCheck className="w-5 h-5 text-mtn-green shrink-0" />
              <p className="text-[11px] text-mtn-cream-secondary">
                Payment request is processed securely through real MTN MoMo API contracts.
              </p>
            </div>

            {/* Action */}
            <div className="pt-2">
              <Button
                type="submit"
                variant="primary"
                size="lg"
                fullWidth
                loading={triggerContributionMutation.isPending}
                disabled={triggerContributionMutation.isPending}
                label={
                  triggerContributionMutation.isPending
                    ? 'Processing MoMo Request...'
                    : `Pay R${group.contributionAmount} via MoMo`
                }
              />
            </div>
          </form>
        )}
      </Card>
    </div>
  );
};

export default ContributionsPage;
