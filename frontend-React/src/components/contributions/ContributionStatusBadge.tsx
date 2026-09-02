import React from 'react';
import { HexBadge } from '../ui/HexBadge';
import { CheckCircle2, Clock, XCircle, HelpCircle } from 'lucide-react';
import type { ContributionStatus } from '@/types';

export interface ContributionStatusBadgeProps {
  status: ContributionStatus['status'] | string;
  size?: 'sm' | 'md';
  className?: string;
}

/**
 * Maps backend contribution status to Mzansi Futurism design system tokens:
 * - paid -> success (green)
 * - pending -> neutral/warning (gold)
 * - failed -> error (red)
 */
export const ContributionStatusBadge: React.FC<ContributionStatusBadgeProps> = ({
  status,
  size = 'sm',
  className = '',
}) => {
  const normalizedStatus = (status || '').toLowerCase();

  switch (normalizedStatus) {
    case 'paid':
    case 'successful':
      return (
        <HexBadge
          variant="green"
          size={size}
          icon={<CheckCircle2 className="w-3 h-3 text-mtn-green" />}
          className={className}
        >
          Paid
        </HexBadge>
      );

    case 'pending':
      return (
        <HexBadge
          variant="gold"
          size={size}
          icon={<Clock className="w-3 h-3 text-mtn-gold" />}
          className={className}
        >
          Pending
        </HexBadge>
      );

    case 'failed':
      return (
        <HexBadge
          variant="red"
          size={size}
          icon={<XCircle className="w-3 h-3 text-mtn-red" />}
          className={className}
        >
          Failed
        </HexBadge>
      );

    default:
      return (
        <HexBadge
          variant="neutral"
          size={size}
          icon={<HelpCircle className="w-3 h-3 text-mtn-cream-secondary" />}
          className={className}
        >
          {status || 'Unknown'}
        </HexBadge>
      );
  }
};

export default ContributionStatusBadge;
