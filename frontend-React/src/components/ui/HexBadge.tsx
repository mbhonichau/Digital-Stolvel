import React from 'react';

export type HexBadgeVariant = 'gold' | 'green' | 'red' | 'blue' | 'neutral';
export type HexBadgeSize = 'sm' | 'md';

export interface HexBadgeProps {
  label?: React.ReactNode;
  children?: React.ReactNode;
  variant?: HexBadgeVariant;
  size?: HexBadgeSize;
  icon?: React.ReactNode;
  className?: string;
}

export const HexBadge: React.FC<HexBadgeProps> = ({
  label,
  children,
  variant = 'gold',
  size = 'md',
  icon,
  className = '',
}) => {
  const variantStyles: Record<HexBadgeVariant, string> = {
    gold: 'bg-mtn-gold-muted text-mtn-gold border-mtn-gold/50 shadow-sm',
    green: 'bg-mtn-green-muted text-mtn-green border-mtn-green/50 shadow-sm',
    red: 'bg-mtn-red-muted text-mtn-red border-mtn-red/50 shadow-sm',
    blue: 'bg-mtn-blue-muted text-mtn-blue border-mtn-blue/50 shadow-sm',
    neutral: 'bg-mtn-surface text-mtn-cream-secondary border-mtn-border',
  };

  const sizeStyles: Record<HexBadgeSize, string> = {
    sm: 'text-[10px] px-2 py-0.5 gap-1 font-semibold uppercase tracking-wider',
    md: 'text-xs px-2.5 py-1 gap-1.5 font-bold tracking-tight',
  };

  return (
    <span
      className={`inline-flex items-center justify-center rounded-lg border backdrop-blur-sm ${variantStyles[variant]} ${sizeStyles[size]} ${className}`}
    >
      {icon && <span className="inline-flex shrink-0">{icon}</span>}
      <span>{label || children}</span>
    </span>
  );
};

export default HexBadge;
