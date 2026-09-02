import React from 'react';
import { Card } from '../ui/Card';
import { HexAvatar } from '../ui/HexAvatar';
import { ContributionStatusBadge } from './ContributionStatusBadge';
import { LoadingSpinner } from '../common/LoadingSpinner';
import { EmptyState } from '../common/EmptyState';
import { Receipt, RefreshCw } from 'lucide-react';
import type { ContributionStatus } from '@/types';

export interface LedgerTableProps {
  contributions?: ContributionStatus[];
  isLoading?: boolean;
  onRefresh?: () => void;
  onContribute?: () => void;
  className?: string;
}

/**
 * Pure presentation ledger table displaying contribution status records.
 * Optimized for mobile list rendering (375px+) with minimum 44px touch targets.
 */
export const LedgerTable: React.FC<LedgerTableProps> = ({
  contributions = [],
  isLoading = false,
  onRefresh,
  onContribute,
  className = '',
}) => {
  return (
    <Card variant="default" className={`p-4 md:p-5 space-y-4 ${className}`}>
      {/* Table Header */}
      <div className="flex items-center justify-between">
        <div>
          <h2 className="text-sm font-bold text-mtn-cream flex items-center gap-2">
            <Receipt className="w-4 h-4 text-mtn-gold" />
            Contribution Ledger
          </h2>
          <p className="text-[11px] text-mtn-cream-secondary">
            Live backend records &bull; Never locally inferred
          </p>
        </div>

        {onRefresh && (
          <button
            type="button"
            onClick={onRefresh}
            disabled={isLoading}
            className="w-10 h-10 rounded-xl bg-mtn-base border border-mtn-border text-mtn-cream-secondary hover:text-mtn-cream active:text-mtn-gold active:bg-mtn-surface transition-colors disabled:opacity-50 flex items-center justify-center shrink-0 touch-target"
            title="Refresh Ledger"
            aria-label="Refresh Ledger"
          >
            <RefreshCw className={`w-4 h-4 ${isLoading ? 'animate-spin' : ''}`} />
          </button>
        )}
      </div>

      {/* Content Area */}
      {isLoading ? (
        <div className="py-8">
          <LoadingSpinner label="Loading contribution records..." size="md" />
        </div>
      ) : contributions.length === 0 ? (
        <EmptyState
          title="No Contributions Found"
          description="No active contribution records have been generated on the server for this cycle yet."
          actionLabel={onContribute ? 'Contribute Now' : undefined}
          onAction={onContribute}
        />
      ) : (
        <div className="divide-y divide-mtn-border/60">
          {contributions.map((item) => {
            const formattedDate = item.paidAt
              ? new Date(item.paidAt).toLocaleDateString(undefined, {
                  month: 'short',
                  day: 'numeric',
                  hour: '2-digit',
                  minute: '2-digit',
                })
              : 'Awaiting Payment';

            return (
              <div key={item.id} className="py-3.5 flex items-center justify-between gap-2.5">
                <div className="flex items-center gap-2.5 min-w-0 flex-1">
                  <HexAvatar name={item.displayName} size="sm" />
                  <div className="min-w-0 flex-1">
                    <p className="text-xs font-bold text-mtn-cream truncate">{item.displayName}</p>
                    <div className="flex items-center gap-1.5 text-[10px] text-mtn-cream-muted mt-0.5 flex-wrap">
                      <span>{formattedDate}</span>
                      {item.momoReference && (
                        <span className="font-mono text-mtn-cream-secondary truncate max-w-[140px]" title={item.momoReference}>
                          &bull; Ref: {item.momoReference}
                        </span>
                      )}
                    </div>
                  </div>
                </div>

                <div className="flex flex-col items-end shrink-0 gap-1 pl-2">
                  <span className="text-xs font-bold text-mtn-cream font-mono">
                    R{item.amount}
                  </span>
                  <ContributionStatusBadge status={item.status} />
                </div>
              </div>
            );
          })}
        </div>
      )}
    </Card>
  );
};

export default LedgerTable;
