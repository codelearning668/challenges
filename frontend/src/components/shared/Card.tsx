import React from "react";
import { clsx, type ClassValue } from 'clsx'
import { twMerge } from 'tailwind-merge'

export function cn(...inputs: ClassValue[]) {
  return twMerge(clsx(inputs))
}

interface CardProps {
  title?: string
  subtitle?: string
  children: React.ReactNode
  actions?: React.ReactNode
  className?: string
  headerAction?: React.ReactNode
}

export function Card({
  title,
  subtitle,
  children,
  actions,
  className,
  headerAction,
}: CardProps) {
  return (
    <div className={cn('bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700', className)}>
      {(title || subtitle || headerAction) && (
        <div className="px-6 py-4 border-b border-gray-200 dark:border-gray-700">
          {headerAction && (
            <div className="flex items-center justify-end mb-2">
              {headerAction}
            </div>
          )}
          {title && (
            <h2 className="text-lg font-semibold text-gray-900 dark:text-gray-100">{title}</h2>
          )}
          {subtitle && (
            <p className="text-sm text-gray-500 dark:text-gray-400 mt-1">{subtitle}</p>
          )}
        </div>
      )}
      <div className="px-6 py-4">
        {children}
      </div>
      {actions && (
        <div className="px-6 py-3 border-t border-gray-200 dark:border-gray-700 bg-gray-50 dark:bg-gray-800/50 rounded-b-xl">
          {actions}
        </div>
      )}
    </div>
  )
}