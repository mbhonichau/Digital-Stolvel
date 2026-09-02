import React from 'react';
import { Loader2 } from 'lucide-react';

export type ButtonVariant = 'primary' | 'secondary' | 'success' | 'danger' | 'outline' | 'ghost';
export type ButtonSize = 'sm' | 'md' | 'lg';

export interface ButtonProps extends React.ButtonHTMLAttributes<HTMLButtonElement> {
  label?: string;
  variant?: ButtonVariant;
  size?: ButtonSize;
  loading?: boolean;
  leftIcon?: React.ReactNode;
  rightIcon?: React.ReactNode;
  fullWidth?: boolean;
}

export const Button: React.FC<ButtonProps> = ({
  children,
  label,
  variant = 'primary',
  size = 'md',
  loading = false,
  disabled = false,
  leftIcon,
  rightIcon,
  fullWidth = false,
  className = '',
  ...props
}) => {
  const baseStyles =
    'inline-flex items-center justify-center font-bold transition-all duration-150 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-offset-mtn-base select-none rounded-xl active:scale-[0.97] touch-manipulation disabled:opacity-50 disabled:cursor-not-allowed disabled:active:scale-100';

  // Minimum 44px touch targets on mobile (size md & lg), sm is min-h-[38px]
  const sizeStyles: Record<ButtonSize, string> = {
    sm: 'text-xs px-3.5 py-2 min-h-[38px] gap-1.5',
    md: 'text-sm px-4 py-2.5 min-h-[44px] gap-2',
    lg: 'text-base px-6 py-3.5 min-h-[48px] gap-2.5',
  };

  const variantStyles: Record<ButtonVariant, string> = {
    primary:
      'bg-mtn-gold text-mtn-base hover:bg-mtn-gold-hover active:bg-mtn-gold-hover shadow-sm focus:ring-mtn-gold',
    secondary:
      'bg-mtn-surface text-mtn-cream border border-mtn-border hover:bg-mtn-card active:bg-mtn-card hover:border-mtn-cream-secondary/40 focus:ring-mtn-gold',
    success:
      'bg-mtn-green text-mtn-cream hover:bg-mtn-green-hover active:bg-mtn-green-hover shadow-sm focus:ring-mtn-green',
    danger:
      'bg-mtn-red text-mtn-cream hover:bg-mtn-red-hover active:bg-mtn-red-hover shadow-sm focus:ring-mtn-red',
    outline:
      'bg-transparent text-mtn-gold border border-mtn-gold hover:bg-mtn-gold-muted active:bg-mtn-gold-muted focus:ring-mtn-gold',
    ghost:
      'bg-transparent text-mtn-cream-secondary hover:text-mtn-cream active:text-mtn-cream hover:bg-mtn-surface active:bg-mtn-surface focus:ring-mtn-gold',
  };

  const widthStyle = fullWidth ? 'w-full' : '';

  return (
    <button
      disabled={disabled || loading}
      className={`${baseStyles} ${sizeStyles[size]} ${variantStyles[variant]} ${widthStyle} ${className}`}
      {...props}
    >
      {loading ? (
        <Loader2 className="w-4 h-4 animate-spin text-current shrink-0" />
      ) : (
        leftIcon && <span className="inline-flex shrink-0">{leftIcon}</span>
      )}
      <span className="truncate">{label || children}</span>
      {!loading && rightIcon && <span className="inline-flex shrink-0">{rightIcon}</span>}
    </button>
  );
};

export default Button;
