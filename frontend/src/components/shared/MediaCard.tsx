import React from 'react'
import { ImageOff } from 'lucide-react'
import { cn } from '@/components/shared/Card'

export interface MediaDetail {
    label: string
    value: string
}

interface MediaCardProps {
    imageUrl?: string
    title?: string
    subtitle?: string
    headerRow?: React.ReactNode
    details?: MediaDetail[]
    children?: React.ReactNode
    onClick?: () => void
    className?: string
    compact?: boolean
}

function MediaCard({
                       imageUrl,
                       title,
                       subtitle,
                       headerRow,
                       details,
                       children,
                       onClick,
                       className,
                       compact = false,
                   }: MediaCardProps) {
    const showDefaultHeader = !headerRow && (title || subtitle)

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
            <div
                className={cn(
                    'w-full bg-gray-100 dark:bg-gray-700 flex items-center justify-center overflow-hidden',
                    compact ? 'h-28' : 'h-44',
                )}
            >
                {imageUrl ? (
                    <img src={imageUrl} alt={title ?? 'image'} className="w-full h-full object-cover" />
                ) : (
                    <div className="flex flex-col items-center justify-center text-gray-400 dark:text-gray-500">
                        <ImageOff className={compact ? 'w-6 h-6' : 'w-10 h-10'} />
                        <span className={cn('mt-1', compact ? 'text-[10px]' : 'text-xs')}>
              No image
            </span>
                    </div>
                )}
            </div>

            <div className={cn(compact ? 'p-2.5' : 'p-4')}>
                {headerRow && <div>{headerRow}</div>}

                {showDefaultHeader && (
                    <>
                        {title && (
                            <h3
                                className={cn(
                                    'font-semibold text-gray-900 dark:text-gray-100 truncate',
                                    compact ? 'text-sm' : 'text-base',
                                )}
                            >
                                {title}
                            </h3>
                        )}
                        {subtitle && (
                            <p
                                className={cn(
                                    'text-gray-500 dark:text-gray-400 truncate',
                                    compact ? 'text-xs mt-0.5' : 'text-sm mt-1',
                                )}
                            >
                                {subtitle}
                            </p>
                        )}
                    </>
                )}

                {details && details.length > 0 && (
                    <ul className={cn(compact ? 'mt-2 space-y-0.5' : 'mt-3 space-y-1')}>
                        {details.map((detail, index) => (
                            <li
                                key={index}
                                className={cn('flex justify-between gap-2', compact ? 'text-xs' : 'text-sm')}
                            >
                <span className="text-gray-500 dark:text-gray-400 shrink-0">
                  {detail.label}
                </span>
                                <span className="font-medium text-gray-900 dark:text-gray-100 text-right truncate">
                  {detail.value}
                </span>
                            </li>
                        ))}
                    </ul>
                )}

                {children && <div className={compact ? 'mt-2' : 'mt-3'}>{children}</div>}
            </div>
        </button>
    )
}

export default MediaCard