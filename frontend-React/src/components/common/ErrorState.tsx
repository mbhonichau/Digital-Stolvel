import React from 'react';
import { AlertCircle, RefreshCw } from 'lucide-react';
import type { ApiError } from '@/types';

interface ErrorStateProps {
  error?: ApiError | Error | string | null;
  title?: string;
  onRetry?: () => void;
  className?: string;
}

export const ErrorState: React.FC<ErrorStateProps> = ({
  error,
  title = 'Unable to Load Data',
  onRetry,
  className = '',
}) => {
  const errorMessage =
    typeof error === 'string'
      ? error
      : (error as ApiError)?.message ||
        (error as Error)?.message ||
        'A connection or server error occurred. Please verify backend service availability.';

  const statusCode = (error as ApiError)?.statusCode;

  return (
    <div
      className={`flex flex-col items-center justify-center p-6 text-center bg-mtn-surface border border-mtn-red rounded-xl max-w-lg mx-auto my-4 shadow-subtle ${className}`}
      role="alert"
    >
      <div className="w-12 h-12 bg-mtn-red-muted rounded-full flex items-center justify-center text-mtn-red mb-3">
        <AlertCircle className="w-6 h-6" />
      </div>
      <h3 className="text-base font-bold text-mtn-cream">{title}</h3>
      {statusCode && (
        <span className="text-xs font-mono bg-mtn-red-muted text-mtn-red px-2 py-0.5 rounded mt-1 border border-mtn-red/30">
          HTTP {statusCode}
        </span>
      )}
      <p className="text-sm text-mtn-cream-secondary mt-2 max-w-sm">{errorMessage}</p>
      {onRetry && (
        <button
          onClick={onRetry}
          type="button"
          className="mt-4 inline-flex items-center gap-2 px-4 py-2 text-sm font-bold text-mtn-cream bg-mtn-red hover:bg-mtn-red-hover rounded-lg transition-colors shadow-sm focus:outline-none focus:ring-2 focus:ring-mtn-red focus:ring-offset-1"
        >
          <RefreshCw className="w-4 h-4" />
          Retry Request
        </button>
      )}
    </div>
  );
};

export default ErrorState;
