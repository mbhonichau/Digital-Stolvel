import React from 'react';
import { Card } from '../ui/Card';
import { HexAvatar } from '../ui/HexAvatar';
import { HexBadge } from '../ui/HexBadge';
import { LoadingSpinner } from '../common/LoadingSpinner';
import { Gift, Calendar, AlertCircle } from 'lucide-react';
import type { ApiError } from '@/types';

export interface PayoutRotationBannerProps {
  recipientName?: string | null;
  cycleNumber?: number;
  dueDate?: string | null;
  amount?: number;
  isLoading?: boolean;
  error?: ApiError | Error | string | null;
  className?: string;
}

/**
 * Displays the authentic next payout recipient returned by the backend data layer.
 */
export const PayoutRotationBanner: React.FC<PayoutRotationBannerProps> = ({
  recipientName,
  cycleNumber,
  dueDate,
  amount,
  isLoading = false,
  error,
  className = '',
}) => {
  const errorMessage =
    typeof error === 'string'
      ? error
      : (error as ApiError)?.message || (error as Error)?.message;

  return (
    <Card
      variant="elevated"
      glow="green"
      className={`p-4 md:p-5 relative overflow-hidden ${className}`}
    >
      {/* Header */}
      <div className="flex items-center justify-between mb-3 border-b border-mtn-border pb-2.5">
        <div className="flex items-center gap-2">
          <Gift className="w-4 h-4 text-mtn-green" />
          <h2 className="text-xs font-bold uppercase tracking-wider text-mtn-cream">
            Next Payout Recipient
          </h2>
        </div>
        {cycleNumber !== undefined && (
          <HexBadge variant="green" size="sm">
            Cycle #{cycleNumber}
          </HexBadge>
        )}
      </div>

      {/* Body States */}
      {isLoading ? (
        <div className="py-3">
          <LoadingSpinner label="Checking backend rotation schedule..." size="sm" />
        </div>
      ) : error ? (
        <div className="py-2 text-xs text-mtn-red flex items-center gap-2">
          <AlertCircle className="w-4 h-4 shrink-0" />
          <span>Could not load rotation recipient: {errorMessage}</span>
        </div>
      ) : recipientName ? (
        <div className="flex items-center justify-between gap-3 bg-mtn-base rounded-xl p-3.5 border border-mtn-border">
          <div className="flex items-center gap-3">
            <HexAvatar name={recipientName} size="md" status="success" />
            <div>
              <p className="text-sm font-black text-mtn-cream">{recipientName}</p>
              <p className="text-[11px] text-mtn-cream-secondary flex items-center gap-1 mt-0.5">
                <Calendar className="w-3 h-3 text-mtn-gold" />
                Due {dueDate ? new Date(dueDate).toLocaleDateString() : 'Scheduled end of cycle'}
              </p>
            </div>
          </div>

          {amount !== undefined && (
            <div className="text-right">
              <span className="text-[10px] text-mtn-cream-secondary block font-medium">
                Disbursement
              </span>
              <span className="text-sm font-black text-mtn-green font-mono">
                R{amount}
              </span>
            </div>
          )}
        </div>
      ) : (
        <div className="bg-mtn-base rounded-xl p-3.5 border border-mtn-border text-center space-y-1">
          <p className="text-xs font-bold text-mtn-cream">Awaiting Backend Cycle Assignment</p>
          <p className="text-[11px] text-mtn-cream-secondary">
            The next recipient will appear once the server schedules the active rotation cycle.
          </p>
        </div>
      )}
    </Card>
  );
};

export default PayoutRotationBanner;
