import React, { forwardRef } from 'react';

export interface InputProps extends React.InputHTMLAttributes<HTMLInputElement> {
  label?: string;
  error?: string;
  helperText?: string;
  leftIcon?: React.ReactNode;
  rightIcon?: React.ReactNode;
  containerClassName?: string;
}

export const Input = forwardRef<HTMLInputElement, InputProps>(
  (
    {
      label,
      error,
      helperText,
      leftIcon,
      rightIcon,
      className = '',
      containerClassName = '',
      id,
      disabled,
      ...props
    },
    ref
  ) => {
    const inputId = id || (label ? label.toLowerCase().replace(/\s+/g, '-') : undefined);

    const hasError = Boolean(error);

    return (
      <div className={`flex flex-col space-y-1.5 w-full ${containerClassName}`}>
        {label && (
          <label htmlFor={inputId} className="text-xs font-bold text-mtn-cream tracking-wide cursor-pointer select-none">
            {label}
          </label>
        )}

        <div className="relative flex items-center w-full min-h-[44px]">
          {leftIcon && (
            <div className="absolute left-3.5 flex items-center pointer-events-none text-mtn-cream-muted shrink-0">
              {leftIcon}
            </div>
          )}

          <input
            ref={ref}
            id={inputId}
            disabled={disabled}
            className={`w-full bg-mtn-base text-mtn-cream placeholder-mtn-cream-muted text-sm rounded-xl border transition-all duration-200 outline-none min-h-[44px]
              ${leftIcon ? 'pl-10' : 'pl-3.5'}
              ${rightIcon ? 'pr-10' : 'pr-3.5'}
              py-2.5
              ${
                hasError
                  ? 'border-mtn-red focus:ring-2 focus:ring-mtn-red/40 focus:border-mtn-red'
                  : 'border-mtn-border focus:border-mtn-gold focus:ring-2 focus:ring-mtn-gold/30'
              }
              disabled:opacity-50 disabled:cursor-not-allowed
              ${className}
            `}
            {...props}
          />

          {rightIcon && (
            <div className="absolute right-3.5 flex items-center text-mtn-cream-muted shrink-0">
              {rightIcon}
            </div>
          )}
        </div>

        {error ? (
          <p className="text-xs text-mtn-red font-medium tracking-tight mt-0.5">{error}</p>
        ) : helperText ? (
          <p className="text-xs text-mtn-cream-secondary tracking-tight mt-0.5">{helperText}</p>
        ) : null}
      </div>
    );
  }
);

Input.displayName = 'Input';

export default Input;
