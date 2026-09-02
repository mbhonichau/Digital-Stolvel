import React from 'react';

export type CardVariant = 'default' | 'elevated' | 'glass' | 'interactive';
export type CardGlow = 'none' | 'gold' | 'green' | 'red' | 'blue';

export interface CardProps extends React.HTMLAttributes<HTMLDivElement> {
  children: React.ReactNode;
  variant?: CardVariant;
  glow?: CardGlow;
  className?: string;
}

export const Card: React.FC<CardProps> = ({
  children,
  variant = 'default',
  glow = 'none',
  className = '',
  onClick,
  ...props
}) => {
  const baseStyles = 'rounded-2xl border transition-all duration-200';

  const variantStyles: Record<CardVariant, string> = {
    default: 'bg-mtn-surface border-mtn-border text-mtn-cream shadow-subtle',
    elevated: 'bg-mtn-card border-mtn-border text-mtn-cream shadow-card',
    glass: 'glass-surface text-mtn-cream',
    interactive:
      'bg-mtn-surface border-mtn-border text-mtn-cream hover:bg-mtn-card hover:border-mtn-gold/40 cursor-pointer active:scale-[0.99] shadow-subtle',
  };

  const glowStyles: Record<CardGlow, string> = {
    none: '',
    gold: 'glow-gold border-mtn-gold/40',
    green: 'glow-green border-mtn-green/40',
    red: 'glow-red border-mtn-red/40',
    blue: 'glow-blue border-mtn-blue/40',
  };

  return (
    <div
      onClick={onClick}
      className={`${baseStyles} ${variantStyles[variant]} ${glowStyles[glow]} ${className}`}
      {...props}
    >
      {children}
    </div>
  );
};

export default Card;
