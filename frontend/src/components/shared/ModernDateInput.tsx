import { Calendar as CalendarIcon } from 'lucide-react'
import { cn } from './Card'

interface ModernDateInputProps
    extends Omit<React.InputHTMLAttributes<HTMLInputElement>, 'type' | 'onChange'> {
    value: string
    onChange: (value: string) => void
    compact?: boolean
}

export function ModernDateInput({
                                    value,
                                    onChange,
                                    compact = false,
                                    className,
                                    ...props
                                }: ModernDateInputProps) {
    return (
        <div className="relative">
            <input
                {...props}
                type="date"
                value={value}
                onChange={(e) => onChange(e.target.value)}
                className={cn(
                    'w-full pl-9 pr-3 rounded-lg border',
                    'bg-white dark:bg-gray-800 text-gray-900 dark:text-gray-100',
                    'border-gray-300 dark:border-gray-600',
                    'focus:outline-none focus:ring-2 focus:ring-primary-500 focus:border-transparent',
                    'transition-colors',
                    // Hide the native calendar button but stretch it to cover the whole
                    // input so clicking anywhere still opens the picker.
                    '[&::-webkit-calendar-picker-indicator]:absolute',
                    '[&::-webkit-calendar-picker-indicator]:inset-0',
                    '[&::-webkit-calendar-picker-indicator]:w-full',
                    '[&::-webkit-calendar-picker-indicator]:h-full',
                    '[&::-webkit-calendar-picker-indicator]:opacity-0',
                    '[&::-webkit-calendar-picker-indicator]:cursor-pointer',
                    // Firefox
                    '[&::-moz-calendar-picker-indicator]:opacity-0',
                    compact ? 'py-1.5 text-sm' : 'py-2 text-sm',
                    className,
                )}
            />
            <CalendarIcon
                className={cn(
                    'pointer-events-none absolute left-3 top-1/2 -translate-y-1/2 text-gray-400',
                    compact ? 'w-4 h-4' : 'w-4 h-4',
                )}
            />
        </div>
    )
}