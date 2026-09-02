import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useCreateGroup } from '@/hooks';
import { useUiStore } from '@/store';
import { Card, Button, Input, HexBadge, ErrorState } from '@/components';
import { ArrowLeft, PlusCircle, Calendar, DollarSign, Users, Layers, Sparkles } from 'lucide-react';
import type { CreateGroupRequest } from '@/types';

export const CreateGroup: React.FC = () => {
  const navigate = useNavigate();
  const { setActiveGroupId } = useUiStore();
  const createGroupMutation = useCreateGroup();

  // Form State
  const [name, setName] = useState('');
  const [contributionAmount, setContributionAmount] = useState('');
  const [frequency, setFrequency] = useState<'weekly' | 'monthly'>('monthly');
  const [startDate, setStartDate] = useState(
    new Date().toISOString().split('T')[0]
  );

  // Field Validation Errors
  const [errors, setErrors] = useState<{
    name?: string;
    contributionAmount?: string;
    frequency?: string;
    startDate?: string;
  }>({});

  const validateForm = (): boolean => {
    const newErrors: typeof errors = {};

    if (!name.trim()) {
      newErrors.name = 'Group name is required';
    } else if (name.trim().length < 3) {
      newErrors.name = 'Group name must be at least 3 characters';
    } else if (name.trim().length > 50) {
      newErrors.name = 'Group name must be less than 50 characters';
    }

    const amountNum = parseFloat(contributionAmount);
    if (!contributionAmount || isNaN(amountNum)) {
      newErrors.contributionAmount = 'Valid contribution amount is required';
    } else if (amountNum <= 0) {
      newErrors.contributionAmount = 'Contribution amount must be greater than R0';
    }

    if (frequency !== 'weekly' && frequency !== 'monthly') {
      newErrors.frequency = 'Frequency must be weekly or monthly';
    }

    if (!startDate) {
      newErrors.startDate = 'Start date is required';
    } else {
      const selected = new Date(startDate);
      const today = new Date();
      today.setHours(0, 0, 0, 0);
      if (selected < today) {
        newErrors.startDate = 'Start date cannot be in the past';
      }
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = (e?: React.FormEvent) => {
    e?.preventDefault();

    if (!validateForm()) {
      return;
    }

    const payload: CreateGroupRequest = {
      name: name.trim(),
      contributionAmount: parseFloat(contributionAmount),
      frequency,
      startDate,
    };

    createGroupMutation.mutate(payload, {
      onSuccess: (createdGroup) => {
        // Update UI state with real group ID from backend response
        setActiveGroupId(createdGroup.id);

        // Navigate to InviteShare with the real backend response
        navigate(`/invite/${createdGroup.id}`, {
          state: { group: createdGroup },
        });
      },
      // Note: On error, form state is preserved and error is rendered below
    });
  };

  return (
    <div className="w-full space-y-5 animate-fade-in">
      {/* Header Bar */}
      <div className="flex items-center justify-between">
        <Button
          variant="ghost"
          size="sm"
          onClick={() => navigate('/')}
          leftIcon={<ArrowLeft className="w-4 h-4" />}
          label="Back"
        />
        <HexBadge variant="gold" size="sm" icon={<Sparkles className="w-3 h-3 text-mtn-gold" />}>
          New Stokvel
        </HexBadge>
      </div>

      {/* Main Form Card */}
      <Card variant="elevated" glow="gold" className="p-6 md:p-8">
        <div className="flex items-center gap-3 mb-2 text-mtn-gold">
          <div className="w-10 h-10 rounded-xl bg-mtn-gold/10 border border-mtn-gold/30 flex items-center justify-center">
            <PlusCircle className="w-6 h-6 text-mtn-gold" />
          </div>
          <div>
            <h1 className="text-xl font-black text-mtn-cream">Create a Stokvel</h1>
            <p className="text-xs text-mtn-cream-secondary">
              Configure pooled contribution rules and rotation schedule.
            </p>
          </div>
        </div>

        {/* API Error Feedback Banner */}
        {createGroupMutation.isError && (
          <div className="mt-4">
            <ErrorState
              title="Group Creation Failed"
              error={createGroupMutation.error}
              onRetry={() => handleSubmit()}
            />
          </div>
        )}

        <form onSubmit={handleSubmit} className="mt-6 space-y-5">
          {/* Group Name */}
          <Input
            label="Group Name"
            placeholder="e.g., Family Savings 2026"
            value={name}
            onChange={(e) => {
              setName(e.target.value);
              if (errors.name) setErrors((prev) => ({ ...prev, name: undefined }));
            }}
            error={errors.name}
            leftIcon={<Users className="w-4 h-4" />}
            disabled={createGroupMutation.isPending}
            autoFocus
          />

          {/* Contribution Amount */}
          <Input
            label="Contribution Amount (ZAR)"
            type="number"
            min="1"
            step="any"
            placeholder="e.g., 500"
            value={contributionAmount}
            onChange={(e) => {
              setContributionAmount(e.target.value);
              if (errors.contributionAmount) {
                setErrors((prev) => ({ ...prev, contributionAmount: undefined }));
              }
            }}
            error={errors.contributionAmount}
            leftIcon={<DollarSign className="w-4 h-4" />}
            helperText="Amount each member contributes per rotation cycle."
            disabled={createGroupMutation.isPending}
          />

          {/* Frequency Selector */}
          <div className="flex flex-col space-y-2">
            <label className="text-xs font-bold text-mtn-cream tracking-wide flex items-center gap-1.5">
              <Layers className="w-3.5 h-3.5 text-mtn-gold" />
              Contribution Frequency
            </label>
            <div className="grid grid-cols-2 gap-3">
              <button
                type="button"
                onClick={() => setFrequency('weekly')}
                disabled={createGroupMutation.isPending}
                className={`py-3 px-4 rounded-xl border text-xs font-bold transition-all flex flex-col items-center justify-center gap-1 ${
                  frequency === 'weekly'
                    ? 'bg-mtn-gold text-mtn-base border-mtn-gold shadow-sm scale-[1.02]'
                    : 'bg-mtn-base text-mtn-cream-secondary border-mtn-border hover:bg-mtn-surface hover:text-mtn-cream'
                }`}
              >
                <span className="text-sm">Weekly</span>
                <span className="text-[10px] opacity-80">7-Day Rotation Cycle</span>
              </button>

              <button
                type="button"
                onClick={() => setFrequency('monthly')}
                disabled={createGroupMutation.isPending}
                className={`py-3 px-4 rounded-xl border text-xs font-bold transition-all flex flex-col items-center justify-center gap-1 ${
                  frequency === 'monthly'
                    ? 'bg-mtn-gold text-mtn-base border-mtn-gold shadow-sm scale-[1.02]'
                    : 'bg-mtn-base text-mtn-cream-secondary border-mtn-border hover:bg-mtn-surface hover:text-mtn-cream'
                }`}
              >
                <span className="text-sm">Monthly</span>
                <span className="text-[10px] opacity-80">30-Day Rotation Cycle</span>
              </button>
            </div>
            {errors.frequency && (
              <p className="text-xs text-mtn-red font-medium">{errors.frequency}</p>
            )}
          </div>

          {/* Start Date */}
          <Input
            label="First Cycle Start Date"
            type="date"
            value={startDate}
            min={new Date().toISOString().split('T')[0]}
            onChange={(e) => {
              setStartDate(e.target.value);
              if (errors.startDate) {
                setErrors((prev) => ({ ...prev, startDate: undefined }));
              }
            }}
            error={errors.startDate}
            leftIcon={<Calendar className="w-4 h-4" />}
            helperText="Date when the first contribution collection commences."
            disabled={createGroupMutation.isPending}
          />

          {/* Submit Action */}
          <div className="pt-2">
            <Button
              type="submit"
              variant="primary"
              size="lg"
              fullWidth
              loading={createGroupMutation.isPending}
              disabled={createGroupMutation.isPending}
              label={createGroupMutation.isPending ? 'Creating Group on Backend...' : 'Create Stokvel'}
            />
          </div>
        </form>
      </Card>
    </div>
  );
};

export default CreateGroup;
