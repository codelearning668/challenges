import React from "react";
import { ImageOff } from 'lucide-react'
import { cn } from '@/components/shared/Card'

export interface MediaDetail {
  label: string
  value: string
}

interface MediaCardProps {
  /** URL of the image to display. When omitted, an empty placeholder is shown. */
  imageUrl?: string
  /** Name/title of the item (e.g. car or track name). */
  title: string
  /** Optional subtitle shown under the title. */
  subtitle?: string
  /** Optional list of detail rows shown below the title. */
  details?: MediaDetail[]
  /** Optional custom content rendered below the details. */
  children?: React.ReactNode
  /** Optional click handler; when provided the card becomes clickable. */
  onClick?: () => void
  className?: string
}

function MediaCard({
  imageUrl,
  title,
  subtitle,
  details,
  children,
  onClick,
  className,
}: MediaCardProps) {
  return (
    <button
      type="button"
      className={cn(
        'block w-full text-left bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 overflow-hidden hover:shadow-md transition-shadow focus:outline-none focus:ring-2 focus:ring-primary-500',
        onClick ? 'cursor-pointer' : 'cursor-default',
        className,
      )}
      onClick={onClick}
    >
      {/* Image placeholder area */}
      <div className="w-full h-44 bg-gray-100 dark:bg-gray-700 flex items-center justify-center overflow-hidden">
        {imageUrl ? (
          <img
            src={imageUrl}
            alt={title}
            className="w-full h-full object-cover"
          />
        ) : (
          <div className="flex flex-col items-center justify-center text-gray-400 dark:text-gray-500">
            <ImageOff className="w-10 h-10" />
            <span className="text-xs mt-1">No image</span>
          </div>
        )}
      </div>

      {/* Content */}
      <div className="p-4">
        <h3 className="font-semibold text-gray-900 dark:text-gray-100 truncate">{title}</h3>
        {subtitle && (
          <p className="text-sm text-gray-500 dark:text-gray-400 mt-1">{subtitle}</p>
        )}

        {details && details.length > 0 && (
          <ul className="mt-3 space-y-1">
            {details.map((detail, index) => (
              <li key={index} className="flex justify-between text-sm">
                <span className="text-gray-500 dark:text-gray-400">{detail.label}</span>
                <span className="font-medium text-gray-900 dark:text-gray-100">{detail.value}</span>
              </li>
            ))}
          </ul>
        )}

        {children && <div className="mt-3">{children}</div>}
      </div>
    </button>
  )
}

export default MediaCard