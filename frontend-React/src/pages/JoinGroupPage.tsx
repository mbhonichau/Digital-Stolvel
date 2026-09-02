import React, { useState, useEffect } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import { useJoinGroupByInviteCode } from '@/hooks';
import { useUiStore } from '@/store';
import { Card, Button, Input, HexBadge, ErrorState, HexAvatar } from '@/components';
import {
  ArrowLeft,
  UserPlus,
  Key,
  User,
  Phone,
  ShieldCheck,
  Sparkles,
  Check,
} from 'lucide-react';
import type { JoinGroupRequest } from '@/types';

export const JoinGroupPage: React.FC = () => {
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();
  const { setActiveGroupId } = useUiStore();
  const joinMutation = useJoinGroupByInviteCode();

  // Form State
  const [inviteCode, setInviteCode] = useState('');
  const [displayName, setDisplayName] = useState('');
  const [msisdn, setMsisdn] = useState('');

  // Confirmation state step
  const [isConfirming, setIsConfirming] = useState(false);

  // Field validation errors
  const [errors, setErrors] = useState<{
    inviteCode?: string;
    displayName?: string;
    msisdn?: string;
  }>({});

  // Pre-fill invite code from URL query parameter ?code=...
  useEffect(() => {
    const codeParam = searchParams.get('code');
    if (codeParam) {
      setInviteCode(codeParam.trim().toUpperCase());
    }
  }, [searchParams]);

  const validateForm = (): boolean => {
    const newErrors: typeof errors = {};

    if (!inviteCode.trim()) {
      newErrors.inviteCode = 'Invite code is required';
    } else if (inviteCode.trim().length < 4) {
      newErrors.inviteCode = 'Invite code must be at least 4 characters';
    }

    if (!displayName.trim()) {
      newErrors.displayName = 'Display name is required';
    } else if (displayName.trim().length < 2) {
      newErrors.displayName = 'Display name must be at least 2 characters';
    } else if (displayName.trim().length > 40) {
      newErrors.displayName = 'Display name cannot exceed 40 characters';
    }

    const cleanPhone = msisdn.replace(/\s+/g, '');
    const phoneRegex = /^(\+?27|0)[6-8][0-9]{8}$/;
    if (!cleanPhone) {
      newErrors.msisdn = 'MoMo phone number is required';
    } else if (!phoneRegex.test(cleanPhone) && cleanPhone.length < 10) {
      newErrors.msisdn = 'Enter a valid MTN MoMo phone number (e.g., 0831234567)';
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleProceedToConfirmation = (e: React.FormEvent) => {
    e.preventDefault();
    if (validateForm()) {
      setIsConfirming(true);
    }
  };

  const handleConfirmJoin = () => {
    const payload: JoinGroupRequest = {
      inviteCode: inviteCode.trim().toUpperCase(),
      displayName: displayName.trim(),
      msisdn: msisdn.replace(/\s+/g, ''),
    };

    joinMutation.mutate(payload, {
      onSuccess: (joinedGroup) => {
        // Update client active group ID strictly from backend response
        setActiveGroupId(joinedGroup.id);

        // Navigate directly to the real group dashboard
        navigate(`/group/${joinedGroup.id}`);
      },
      onError: () => {
        // Return to form view on error with inputs preserved
        setIsConfirming(false);
      },
    });
  };

  return (
    <div className="w-full space-y-5 animate-fade-in">
      {/* Header Bar */}
      <div className="flex items-center justify-between">
        <Button
          variant="ghost"
          size="sm"
          onClick={() => (isConfirming ? setIsConfirming(false) : navigate('/'))}
          leftIcon={<ArrowLeft className="w-4 h-4" />}
          label={isConfirming ? 'Edit Details' : 'Back'}
        />
        <HexBadge variant="gold" size="sm" icon={<Sparkles className="w-3 h-3 text-mtn-gold" />}>
          {isConfirming ? 'Review & Confirm' : 'Join Stokvel'}
        </HexBadge>
      </div>

      {/* API Error Notification */}
      {joinMutation.isError && (
        <ErrorState
          title="Failed to Join Stokvel"
          error={joinMutation.error}
          onRetry={handleConfirmJoin}
        />
      )}

      {/* View 1: Form Input Screen */}
      {!isConfirming ? (
        <Card variant="elevated" glow="gold" className="p-6 md:p-8">
          <div className="flex items-center gap-3 mb-2 text-mtn-gold">
            <div className="w-10 h-10 rounded-xl bg-mtn-gold/10 border border-mtn-gold/30 flex items-center justify-center">
              <UserPlus className="w-6 h-6 text-mtn-gold" />
            </div>
            <div>
              <h1 className="text-xl font-black text-mtn-cream">Join a Stokvel</h1>
              <p className="text-xs text-mtn-cream-secondary">
                Enter your invite code and MoMo credentials to become a member.
              </p>
            </div>
          </div>

          <form onSubmit={handleProceedToConfirmation} className="mt-6 space-y-4">
            {/* Invite Code */}
            <Input
              label="Invite Code"
              placeholder="e.g., STOK-8842"
              value={inviteCode}
              onChange={(e) => {
                setInviteCode(e.target.value.toUpperCase());
                if (errors.inviteCode) {
                  setErrors((prev) => ({ ...prev, inviteCode: undefined }));
                }
              }}
              error={errors.inviteCode}
              leftIcon={<Key className="w-4 h-4 text-mtn-gold" />}
              helperText="The unique code shared by the Stokvel administrator."
              autoFocus={!searchParams.get('code')}
            />

            {/* Display Name */}
            <Input
              label="Your Full Name / Display Name"
              placeholder="e.g., Thabo Mokoena"
              value={displayName}
              onChange={(e) => {
                setDisplayName(e.target.value);
                if (errors.displayName) {
                  setErrors((prev) => ({ ...prev, displayName: undefined }));
                }
              }}
              error={errors.displayName}
              leftIcon={<User className="w-4 h-4" />}
              helperText="How other members of the Stokvel will identify you."
            />

            {/* Phone Number */}
            <Input
              label="MTN MoMo Phone Number"
              type="tel"
              placeholder="e.g., 083 123 4567"
              value={msisdn}
              onChange={(e) => {
                setMsisdn(e.target.value);
                if (errors.msisdn) {
                  setErrors((prev) => ({ ...prev, msisdn: undefined }));
                }
              }}
              error={errors.msisdn}
              leftIcon={<Phone className="w-4 h-4" />}
              helperText="Used for automated contribution requests and payout disbursements."
            />

            {/* Continue to Confirmation Button */}
            <div className="pt-2">
              <Button
                type="submit"
                variant="primary"
                size="lg"
                fullWidth
                label="Review & Join"
              />
            </div>
          </form>
        </Card>
      ) : (
        /* View 2: Confirmation Review Screen */
        <Card variant="elevated" glow="gold" className="p-6 md:p-8 space-y-5">
          <div className="text-center space-y-2">
            <HexAvatar name={displayName} size="lg" status="online" className="mx-auto mb-2" />
            <h2 className="text-xl font-black text-mtn-cream">Confirm Membership</h2>
            <p className="text-xs text-mtn-cream-secondary max-w-xs mx-auto">
              Please verify your details before joining this Stokvel rotation.
            </p>
          </div>

          {/* Verification Details Box */}
          <div className="bg-mtn-base border border-mtn-border rounded-xl p-4 space-y-3">
            <div className="flex items-center justify-between border-b border-mtn-border/50 pb-2">
              <span className="text-xs text-mtn-cream-secondary">Target Invite Code</span>
              <span className="text-xs font-mono font-bold text-mtn-gold">{inviteCode}</span>
            </div>
            <div className="flex items-center justify-between border-b border-mtn-border/50 pb-2">
              <span className="text-xs text-mtn-cream-secondary">Member Name</span>
              <span className="text-xs font-bold text-mtn-cream">{displayName}</span>
            </div>
            <div className="flex items-center justify-between">
              <span className="text-xs text-mtn-cream-secondary">MoMo Number</span>
              <span className="text-xs font-mono font-bold text-mtn-cream">{msisdn}</span>
            </div>
          </div>

          {/* Terms Notice */}
          <div className="p-3 bg-mtn-surface border border-mtn-border rounded-xl flex items-start gap-2.5 text-left">
            <ShieldCheck className="w-5 h-5 text-mtn-green shrink-0 mt-0.5" />
            <p className="text-[11px] text-mtn-cream-secondary leading-relaxed">
              By confirming, you will be enrolled into the Stokvel roster on the backend. Contributions and rotation payouts will be linked to your MoMo account.
            </p>
          </div>

          {/* Actions */}
          <div className="space-y-2 pt-2">
            <Button
              variant="primary"
              size="lg"
              fullWidth
              loading={joinMutation.isPending}
              disabled={joinMutation.isPending}
              onClick={handleConfirmJoin}
              leftIcon={<Check className="w-4 h-4" />}
              label={joinMutation.isPending ? 'Joining on Server...' : 'Confirm & Join Stokvel'}
            />
            <Button
              variant="ghost"
              size="sm"
              fullWidth
              disabled={joinMutation.isPending}
              onClick={() => setIsConfirming(false)}
              label="Cancel and Edit"
            />
          </div>
        </Card>
      )}
    </div>
  );
};

export const JoinGroup = JoinGroupPage;
export default JoinGroupPage;
