import React from 'react';

interface LoadingSpinnerProps {
  size?: 'sm' | 'md' | 'lg';
  label?: string;
  className?: string;
}

export const LoadingSpinner: React.FC<LoadingSpinnerProps> = ({
  size = 'md',
  label = 'Loading...',
  className = '',
}) => {
  const sizeClasses = {
    sm: 'w-4 h-4 border-2',
    md: 'w-8 h-8 border-2',
    lg: 'w-12 h-12 border-3',
  };

  return (
    <div className={`flex flex-col items-center justify-center p-6 space-y-3 ${className}`}>
      <div
        className={`rounded-full border-t-mtn-gold border-r-mtn-gold border-b-mtn-border border-l-mtn-border animate-spin ${sizeClasses[size]}`}
        role="status"
        aria-label={label}
      />
      {label && <p className="text-sm font-medium text-mtn-cream-secondary">{label}</p>}
    </div>
  );
};

export default LoadingSpinner;
