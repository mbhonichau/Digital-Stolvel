import React, { useState } from 'react';
import { useParams, useLocation, useNavigate } from 'react-router-dom';
import { useGroup } from '@/hooks';
import { Card, Button, HexBadge, LoadingSpinner, ErrorState } from '@/components';
import {
  Share2,
  Copy,
  Check,
  ArrowRight,
  ShieldCheck,
  Users,
  Calendar,
  DollarSign,
  Link as LinkIcon,
} from 'lucide-react';
import type { GroupResponse } from '@/types';

export const InvitePage: React.FC = () => {
  const { groupId, id } = useParams<{ groupId?: string; id?: string }>();
  const location = useLocation();
  const navigate = useNavigate();
  const [codeCopied, setCodeCopied] = useState(false);
  const [linkCopied, setLinkCopied] = useState(false);

  const targetGroupId = groupId || id;

  // 1. Read passed group state from CreateGroup navigation if available
  const passedGroup = (location.state as { group?: GroupResponse } | undefined)?.group;

  // 2. Otherwise query actual backend REST endpoint GET /groups/{id}
  const {
    data: fetchedGroup,
    isLoading,
    isError,
    error,
    refetch,
  } = useGroup(passedGroup ? undefined : targetGroupId);

  const group = passedGroup || fetchedGroup;

  // Shareable link computed dynamically from genuine backend group invite code
  const shareableUrl = group?.inviteCode
    ? `${window.location.origin}/join?code=${encodeURIComponent(group.inviteCode)}`
    : '';

  const handleCopyCode = () => {
    if (!group?.inviteCode) return;
    navigator.clipboard.writeText(group.inviteCode);
    setCodeCopied(true);
    setTimeout(() => setCodeCopied(false), 2500);
  };

  const handleCopyLink = () => {
    if (!shareableUrl) return;
    navigator.clipboard.writeText(shareableUrl);
    setLinkCopied(true);
    setTimeout(() => setLinkCopied(false), 2500);
  };

  const handleShare = async () => {
    if (!group) return;
    const sharePayload = {
      title: `Join ${group.name} on MTN MoMo Stokvel`,
      text: `You're invited to join "${group.name}" Stokvel! Use invite code: ${group.inviteCode} or click the link to join:`,
      url: shareableUrl,
    };

    if (navigator.share) {
      try {
        await navigator.share(sharePayload);
      } catch (err) {
        if ((err as Error).name !== 'AbortError') {
          handleCopyLink();
        }
      }
    } else {
      handleCopyLink();
    }
  };

  // State: Loading
  if (isLoading) {
    return (
      <div className="py-16 text-center">
        <LoadingSpinner label="Retrieving official group details from backend..." size="lg" />
      </div>
    );
  }

  // State: Missing ID or API Error
  if (!id && !passedGroup && !groupId) {
    return (
      <div className="py-12 space-y-4">
        <ErrorState
          title="Invalid Group Request"
          error="No group ID was specified in the route."
        />
        <div className="text-center">
          <Button variant="secondary" onClick={() => navigate('/')} label="Return to Home" />
        </div>
      </div>
    );
  }

  if (isError || !group) {
    return (
      <div className="py-12 space-y-4">
        <ErrorState
          title="Group Not Found"
          error={error || 'Unable to retrieve the requested Stokvel group from the server.'}
          onRetry={() => refetch()}
        />
        <div className="text-center">
          <Button variant="secondary" onClick={() => navigate('/')} label="Return to Home" />
        </div>
      </div>
    );
  }

  return (
    <div className="w-full space-y-6 animate-fade-in">
      {/* Success Celebration Card */}
      <Card variant="elevated" glow="green" className="p-6 md:p-8 text-center relative overflow-hidden">
        <div className="w-14 h-14 rounded-2xl bg-mtn-green-muted border border-mtn-green/40 flex items-center justify-center mx-auto mb-4 text-mtn-green shadow-sm">
          <ShieldCheck className="w-8 h-8 text-mtn-green" />
        </div>

        <div className="inline-flex items-center gap-1.5 mb-2">
          <HexBadge variant="green" size="sm">
            Stokvel Ready
          </HexBadge>
        </div>

        <h1 className="text-2xl md:text-3xl font-black text-mtn-cream tracking-tight mb-2">
          {group.name}
        </h1>

        <p className="text-xs text-mtn-cream-secondary max-w-sm mx-auto mb-6">
          Your Stokvel has been created on the backend. Share the official invite code or link below with prospective members.
        </p>

        {/* Backend-Generated Invite Code Display Box */}
        <div className="bg-mtn-base border-2 border-dashed border-mtn-gold/60 rounded-2xl p-5 max-w-sm mx-auto mb-5 shadow-card">
          <p className="text-[10px] font-bold text-mtn-cream-secondary uppercase tracking-widest mb-1">
            Backend Invite Code
          </p>
          <div className="font-mono text-3xl font-black text-mtn-gold tracking-widest select-all my-2">
            {group.inviteCode}
          </div>
          <p className="text-[10px] text-mtn-cream-muted">
            Issued by Spring Boot REST API &bull; No fake data
          </p>

          <div className="flex gap-2 justify-center mt-4">
            <Button
              variant="primary"
              size="sm"
              onClick={handleCopyCode}
              leftIcon={codeCopied ? <Check className="w-4 h-4 text-mtn-base" /> : <Copy className="w-4 h-4" />}
              label={codeCopied ? 'Code Copied!' : 'Copy Code'}
            />
            <Button
              variant="secondary"
              size="sm"
              onClick={handleShare}
              leftIcon={<Share2 className="w-4 h-4" />}
              label="Share"
            />
          </div>
        </div>

        {/* Shareable Link Box */}
        <div className="bg-mtn-surface border border-mtn-border rounded-xl p-3 max-w-sm mx-auto mb-6 text-left flex items-center gap-2">
          <LinkIcon className="w-4 h-4 text-mtn-gold shrink-0 ml-1" />
          <input
            type="text"
            readOnly
            value={shareableUrl}
            className="bg-transparent text-xs text-mtn-cream-secondary font-mono w-full outline-none select-all truncate"
          />
          <button
            type="button"
            onClick={handleCopyLink}
            className="px-2.5 py-1 text-xs font-bold text-mtn-cream bg-mtn-card hover:bg-mtn-border rounded-lg transition-colors border border-mtn-border shrink-0"
          >
            {linkCopied ? 'Copied' : 'Copy Link'}
          </button>
        </div>

        {/* Group Summary Metadata */}
        <div className="grid grid-cols-3 gap-2.5 max-w-sm mx-auto text-center border-t border-mtn-border pt-4">
          <div className="p-2.5 bg-mtn-base rounded-xl border border-mtn-border">
            <DollarSign className="w-4 h-4 text-mtn-gold mx-auto mb-1" />
            <p className="text-[10px] text-mtn-cream-secondary">Contribution</p>
            <p className="text-xs font-bold text-mtn-cream">R{group.contributionAmount}</p>
          </div>
          <div className="p-2.5 bg-mtn-base rounded-xl border border-mtn-border">
            <Calendar className="w-4 h-4 text-mtn-blue mx-auto mb-1" />
            <p className="text-[10px] text-mtn-cream-secondary">Frequency</p>
            <p className="text-xs font-bold text-mtn-cream capitalize">{group.frequency}</p>
          </div>
          <div className="p-2.5 bg-mtn-base rounded-xl border border-mtn-border">
            <Users className="w-4 h-4 text-mtn-green mx-auto mb-1" />
            <p className="text-[10px] text-mtn-cream-secondary">Members</p>
            <p className="text-xs font-bold text-mtn-cream">{group.members?.length || 1}</p>
          </div>
        </div>
      </Card>

      {/* Continue to Group CTA */}
      <div className="text-center pt-2">
        <Button
          variant="primary"
          size="lg"
          fullWidth
          onClick={() => navigate(`/group/${group.id}`)}
          rightIcon={<ArrowRight className="w-4 h-4" />}
          label="Continue to Group"
        />
      </div>
    </div>
  );
};

export const InviteShare = InvitePage;
export default InvitePage;
