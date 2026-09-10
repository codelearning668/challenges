import { clsx, type ClassValue } from 'clsx'
import { twMerge } from 'tailwind-merge'
import { X } from 'lucide-react'

export function cn(...inputs: ClassValue[]) {
  return twMerge(clsx(inputs))
}

interface ModalProps {
  isOpen: boolean
  onClose: () => void
  title: string
  children: React.ReactNode
  actions?: React.ReactNode
  size?: 'sm' | 'md' | 'lg' | 'xl'
}

export function Modal({
  isOpen,
  onClose,
  title,
  children,
  actions,
  size = 'md',
}: ModalProps) {
  if (!isOpen) return null

  const sizeClasses = {
    sm: 'max-w-md',
    md: 'max-w-2xl',
    lg: 'max-w-4xl',
    xl: 'max-w-6xl',
  }

  return (
    <div className="fixed inset-0 z-50 overflow-y-auto">
      <div className="flex min-h-screen items-center justify-center p-4 text-center">
        <div className="fixed inset-0 bg-black/50 transition-opacity" onClick={onClose} />
        
        <div className={`relative w-full ${sizeClasses[size]} transform rounded-lg bg-white dark:bg-gray-800 p-6 shadow-xl transition-all`}>
          <div className="flex items-center justify-between mb-4">
            <h3 className="text-lg font-semibold text-gray-900 dark:text-gray-100">{title}</h3>
            <button
              onClick={onClose}
              className="rounded-lg p-2 text-gray-400 hover:bg-gray-100 dark:hover:bg-gray-700 hover:text-gray-500"
            >
              <X className="w-5 h-5" />
            </button>
          </div>
          
          <div className="max-h-[calc(100vh-8rem)] overflow-y-auto">
            {children}
          </div>
          
          {actions && (
            <div className="flex justify-end gap-2 mt-4 pt-4 border-t border-gray-200 dark:border-gray-700">
              {actions}
            </div>
          )}
        </div>
      </div>
    </div>
  )
}