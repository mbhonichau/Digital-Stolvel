import React from 'react';
import { FolderOpen } from 'lucide-react';

interface EmptyStateProps {
  title?: string;
  description?: string;
  actionLabel?: string;
  onAction?: () => void;
  icon?: React.ReactNode;
  className?: string;
}

export const EmptyState: React.FC<EmptyStateProps> = ({
  title = 'No Records Found',
  description = 'There are currently no items available.',
  actionLabel,
  onAction,
  icon,
  className = '',
}) => {
  return (
    <div
      className={`flex flex-col items-center justify-center p-8 text-center bg-mtn-surface border border-mtn-border rounded-xl max-w-md mx-auto my-4 shadow-subtle ${className}`}
    >
      <div className="w-12 h-12 bg-mtn-base border border-mtn-border rounded-full flex items-center justify-center text-mtn-cream-secondary mb-3">
        {icon || <FolderOpen className="w-6 h-6" />}
      </div>
      <h3 className="text-base font-bold text-mtn-cream">{title}</h3>
      <p className="text-sm text-mtn-cream-secondary mt-1 max-w-xs">{description}</p>
      {actionLabel && onAction && (
        <button
          onClick={onAction}
          type="button"
          className="mt-4 px-4 py-2 text-sm font-bold text-mtn-base bg-mtn-gold hover:bg-mtn-gold-hover rounded-lg transition-colors shadow-sm focus:outline-none focus:ring-2 focus:ring-mtn-gold"
        >
          {actionLabel}
        </button>
      )}
    </div>
  );
};

export default EmptyState;
