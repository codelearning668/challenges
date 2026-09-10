import { forwardRef } from 'react'
import { clsx, type ClassValue } from 'clsx'
import { twMerge } from 'tailwind-merge'

export function cn(...inputs: ClassValue[]) {
  return twMerge(clsx(inputs))
}

interface SelectOption {
  value: string | number
  label: string
  icon?: React.ReactNode
}

interface SelectProps extends React.SelectHTMLAttributes<HTMLSelectElement> {
  label?: string
  error?: string
  helperText?: string
  options: SelectOption[]
  icon?: React.ReactNode
  /** Text shown in the disabled first option. */
  placeholder?: string
}

export const Select = forwardRef<HTMLSelectElement, SelectProps>(function Select(
    {
      className,
      label,
      error,
      helperText,
      options,
      icon,
      placeholder = 'Select an option',
      ...props
    },
    ref,
) {
  const inputId = props.id || (label ? label.toLowerCase().replace(/\s+/g, '-') : undefined)

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
          <select
              ref={ref}
              id={inputId}
              // Uncontrolled <select> defaults to the first non-disabled option.
              // Pinning to '' keeps the placeholder selected until the user picks.
              defaultValue=""
              className={cn(
                  'w-full px-3 py-2 bg-white dark:bg-gray-800 border rounded-lg',
                  'text-gray-900 dark:text-gray-100',
                  'focus:outline-none focus:ring-2 focus:ring-primary-500 focus:border-transparent',
                  'transition-colors duration-200 appearance-none cursor-pointer',
                  'disabled:opacity-50 disabled:cursor-not-allowed',
                  error ? 'border-red-500' : 'border-gray-300 dark:border-gray-600',
                  icon ? 'pl-10 pr-8' : 'pl-3 pr-8',
                  className,
              )}
              {...props}
          >
            <option value="" disabled>
              {placeholder}
            </option>
            {options.map((option) => (
                <option key={option.value} value={option.value}>
                  {option.label}
                </option>
            ))}
          </select>
          <div className="absolute right-3 top-1/2 -translate-y-1/2 pointer-events-none">
            <svg
                className="w-4 h-4 text-gray-400"
                fill="none"
                stroke="currentColor"
                viewBox="0 0 24 24"
            >
              <path
                  strokeLinecap="round"
                  strokeLinejoin="round"
                  strokeWidth={2}
                  d="M19 9l-7 7-7-7"
              />
            </svg>
          </div>
        </div>
        {error && <p className="mt-1 text-sm text-red-600 dark:text-red-400">{error}</p>}
        {helperText && !error && (
            <p className="mt-1 text-sm text-gray-500 dark:text-gray-400">{helperText}</p>
        )}
      </div>
  )
})