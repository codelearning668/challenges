import { forwardRef } from 'react'
import { clsx, type ClassValue } from 'clsx'
import { twMerge } from 'tailwind-merge'
import type { FieldError } from 'react-hook-form'

export function cn(...inputs: ClassValue[]) {
  return twMerge(clsx(inputs))
}

interface InputProps extends React.InputHTMLAttributes<HTMLInputElement> {
  label?: string
  error?: string | FieldError
  helperText?: string
  icon?: React.ReactNode
}

const PICKER_TYPES = ['date', 'datetime-local', 'month', 'week', 'time']

export const Input = forwardRef<HTMLInputElement, InputProps>(function Input(
    { className, label, error, helperText, icon, type, onClick, autoComplete, ...props },
    ref,
) {
  const inputId = props.id || (label ? label.toLowerCase().replace(/\s+/g, '-') : undefined)
  const isPicker = type != null && PICKER_TYPES.includes(type)

  const handleClick = (e: React.MouseEvent<HTMLInputElement>) => {
    if (isPicker && 'showPicker' in HTMLInputElement.prototype) {
      try {
        ;(e.currentTarget as HTMLInputElement).showPicker()
      } catch {
        // Safari < 16, cross-origin, or not user-activated — fall back to native
      }
    }
    onClick?.(e)
  }

  return (
      <div className="w-full">
        {label && (
            <label
                htmlFor={inputId}
                className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1"
            >
              {label}
            </label>
        )}
        <div className="relative">
          {icon && (
              <div className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400">{icon}</div>
          )}
          <input
              ref={ref}
              id={inputId || undefined}
              type={type}
              onClick={handleClick}
              // Chrome aggressively autofills date inputs from history; opting out
              // keeps a fresh form actually empty.
              autoComplete={autoComplete ?? (isPicker ? 'off' : undefined)}
              className={cn(
                  'w-full px-3 py-2 bg-white dark:bg-gray-800 border rounded-lg',
                  'text-gray-900 dark:text-gray-100 placeholder-gray-400',
                  'focus:outline-none focus:ring-2 focus:ring-primary-500 focus:border-transparent',
                  'transition-colors duration-200',
                  isPicker && 'cursor-pointer',
                  error ? 'border-red-500' : 'border-gray-300 dark:border-gray-600',
                  icon ? 'pl-10' : '',
                  className,
              )}
              {...props}
          />
        </div>
        {error && (
            <p className="mt-1 text-sm text-red-600 dark:text-red-400">
              {typeof error === 'string' ? error : error?.message}
            </p>
        )}
        {helperText && !error && (
            <p className="mt-1 text-sm text-gray-500 dark:text-gray-400">{helperText}</p>
        )}
      </div>
  )
})