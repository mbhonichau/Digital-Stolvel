import React from 'react';

export interface SkeletonProps {
  className?: string;
  variant?: 'text' | 'circular' | 'rectangular' | 'rounded';
  width?: string | number;
  height?: string | number;
}

/**
 * Mzansi Futurism Skeleton Shimmer Component
 */
export const Skeleton: React.FC<SkeletonProps> = ({
  className = '',
  variant = 'rounded',
  width,
  height,
}) => {
  const variantClasses = {
    text: 'rounded h-4 w-full',
    circular: 'rounded-full',
    rectangular: 'rounded-none',
    rounded: 'rounded-xl',
  };

  const style: React.CSSProperties = {
    width: width,
    height: height,
  };

  return (
    <div
      style={style}
      className={`bg-mtn-surface/80 border border-mtn-border/40 animate-pulse ${variantClasses[variant]} ${className}`}
      role="status"
      aria-label="Loading..."
    />
  );
};

export const CardSkeleton: React.FC<{ className?: string }> = ({ className = '' }) => {
  return (
    <div
      className={`p-5 rounded-2xl bg-mtn-surface border border-mtn-border/60 space-y-3.5 animate-pulse ${className}`}
    >
      <div className="flex items-center justify-between">
        <Skeleton variant="text" className="w-1/3 h-5" />
        <Skeleton variant="rounded" className="w-16 h-5" />
      </div>
      <Skeleton variant="text" className="w-2/3 h-3" />
      <div className="grid grid-cols-3 gap-2 pt-2">
        <Skeleton variant="rounded" className="h-14" />
        <Skeleton variant="rounded" className="h-14" />
        <Skeleton variant="rounded" className="h-14" />
      </div>
    </div>
  );
};

export const ListItemSkeleton: React.FC<{ count?: number }> = ({ count = 3 }) => {
  return (
    <div className="space-y-3">
      {Array.from({ length: count }).map((_, i) => (
        <div
          key={i}
          className="p-3.5 rounded-xl bg-mtn-base border border-mtn-border/50 flex items-center justify-between gap-3 animate-pulse"
        >
          <div className="flex items-center gap-3 flex-1">
            <Skeleton variant="circular" className="w-9 h-9 shrink-0" />
            <div className="space-y-1.5 flex-1">
              <Skeleton variant="text" className="w-2/5 h-3.5" />
              <Skeleton variant="text" className="w-3/5 h-2.5" />
            </div>
          </div>
          <div className="space-y-1 w-16 text-right">
            <Skeleton variant="text" className="w-full h-3.5" />
            <Skeleton variant="rounded" className="w-full h-4" />
          </div>
        </div>
      ))}
    </div>
  );
};

export default Skeleton;
