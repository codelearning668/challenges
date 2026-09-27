import { useEffect, useRef, useState } from 'react'
import { createPortal } from 'react-dom'
import { DayPicker } from 'react-day-picker'
import { format, parseISO, isValid, startOfDay } from 'date-fns'
import { Calendar as CalendarIcon, X } from 'lucide-react'
import { clsx, type ClassValue } from 'clsx'
import { twMerge } from 'tailwind-merge'
import type { FieldError } from 'react-hook-form'
import 'react-day-picker/style.css'

function cn(...inputs: ClassValue[]) {
    return twMerge(clsx(inputs))
}

/** Today at local midnight, as yyyy-MM-dd. */
function todayLocalISO(): string {
    const d = new Date()
    d.setHours(0, 0, 0, 0)
    const yyyy = d.getFullYear()
    const mm = String(d.getMonth() + 1).padStart(2, '0')
    const dd = String(d.getDate()).padStart(2, '0')
    return `${yyyy}-${mm}-${dd}`
}

interface DatePickerProps {
    /** yyyy-MM-dd or '' */
    value: string
    onChange: (value: string) => void
    label?: string
    error?: string | FieldError
    helperText?: string
    placeholder?: string
    disabled?: boolean
    id?: string
    /**
     * Earliest selectable date, yyyy-MM-dd.
     * - undefined → today (past dates blocked)
     * - null      → no restriction
     * - string    → that specific date
     */
    min?: string | null
}

export function DatePicker({
                               value,
                               onChange,
                               label,
                               error,
                               helperText,
                               placeholder = 'Pick a date',
                               disabled = false,
                               id,
                               min,
                           }: DatePickerProps) {
    const inputId = id || (label ? label.toLowerCase().replace(/\s+/g, '-') : undefined)
    const buttonRef = useRef<HTMLButtonElement>(null)
    const popoverRef = useRef<HTMLDivElement>(null)
    const [open, setOpen] = useState(false)
    const [coords, setCoords] = useState({ top: 0, left: 0 })

    const today = startOfDay(new Date())
    const resolvedMin =
        min === null
            ? undefined
            : startOfDay(parseISO(min ?? todayLocalISO()))

    const selected = value ? parseISO(value) : undefined
    const display = selected && isValid(selected) ? format(selected, 'MMM dd, yyyy') : null

    useEffect(() => {
        if (!open) return

        const reposition = () => {
            const rect = buttonRef.current?.getBoundingClientRect()
            if (!rect) return
            setCoords({ top: rect.bottom + 6, left: rect.left })
        }

        const onClickOutside = (e: MouseEvent) => {
            const t = e.target as Node
            if (popoverRef.current?.contains(t) || buttonRef.current?.contains(t)) return
            setOpen(false)
        }

        const onEscape = (e: KeyboardEvent) => {
            if (e.key === 'Escape') setOpen(false)
        }

        reposition()
        document.addEventListener('mousedown', onClickOutside)
        document.addEventListener('keydown', onEscape)
        window.addEventListener('scroll', reposition, true)
        window.addEventListener('resize', reposition)
        return () => {
            document.removeEventListener('mousedown', onClickOutside)
            document.removeEventListener('keydown', onEscape)
            window.removeEventListener('scroll', reposition, true)
            window.removeEventListener('resize', reposition)
        }
    }, [open])

    const handleOpen = () => {
        if (disabled) return
        const rect = buttonRef.current?.getBoundingClientRect()
        if (rect) setCoords({ top: rect.bottom + 6, left: rect.left })
        setOpen((v) => !v)
    }

    const handleSelect = (date: Date | undefined) => {
        if (!date) return
        // Guard against a past selection slipping through (typed input, stale UI,
        // manual keyboard nav).
        if (resolvedMin && startOfDay(date).getTime() < resolvedMin.getTime()) {
            return
        }
        onChange(format(date, 'yyyy-MM-dd'))
        setOpen(false)
    }

    const handleClear = (e: React.MouseEvent) => {
        e.stopPropagation()
        onChange('')
    }

    const errorMessage =
        typeof error === 'string' ? error : (error as FieldError | undefined)?.message

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

            <button
                ref={buttonRef}
                id={inputId}
                type="button"
                disabled={disabled}
                onClick={handleOpen}
                aria-haspopup="dialog"
                aria-expanded={open}
                className={cn(
                    'w-full flex items-center justify-between gap-2',
                    'px-3 py-2 rounded-lg border text-left',
                    'bg-white dark:bg-gray-800 text-gray-900 dark:text-gray-100',
                    'transition-colors duration-200',
                    'focus:outline-none focus:ring-2 focus:ring-primary-500 focus:border-transparent',
                    'disabled:opacity-50 disabled:cursor-not-allowed',
                    error ? 'border-red-500' : 'border-gray-300 dark:border-gray-600',
                )}
            >
        <span className="flex items-center gap-2 min-w-0">
          <CalendarIcon className="w-4 h-4 text-gray-400 shrink-0" />
          <span
              className={cn(
                  'text-sm truncate',
                  !display && 'text-gray-400 dark:text-gray-500',
              )}
          >
            {display ?? placeholder}
          </span>
        </span>

                {display && !disabled && (
                    <span
                        role="button"
                        tabIndex={-1}
                        onClick={handleClear}
                        onKeyDown={(e) => {
                            if (e.key === 'Enter') {
                                e.preventDefault()
                                onChange('')
                            }
                        }}
                        className="shrink-0 p-0.5 rounded hover:bg-gray-100 dark:hover:bg-gray-700"
                        aria-label="Clear date"
                    >
            <X className="w-3.5 h-3.5 text-gray-400" />
          </span>
                )}
            </button>

            {open &&
                createPortal(
                    <div
                        ref={popoverRef}
                        role="dialog"
                        style={{ position: 'fixed', top: coords.top, left: coords.left }}
                        className="z-[200] rounded-xl border border-gray-200 dark:border-gray-700 bg-white dark:bg-gray-800 shadow-lg p-3"
                    >
                        <DayPicker
                            mode="single"
                            selected={selected}
                            onSelect={handleSelect}
                            disabled={resolvedMin ? { before: resolvedMin } : undefined}
                            defaultMonth={selected ?? resolvedMin ?? today}
                            showOutsideDays
                            classNames={{
                                root: 'text-gray-900 dark:text-gray-100',
                                months: 'flex',
                                month: 'space-y-2',
                                month_caption: 'flex justify-center items-center h-8 relative',
                                caption_label: 'text-sm font-semibold',
                                nav: 'absolute inset-x-0 top-0 flex justify-between items-center',
                                button_previous:
                                    'w-7 h-7 flex items-center justify-center rounded-md text-gray-500 hover:bg-gray-100 dark:hover:bg-gray-700',
                                button_next:
                                    'w-7 h-7 flex items-center justify-center rounded-md text-gray-500 hover:bg-gray-100 dark:hover:bg-gray-700',
                                month_grid: 'w-full border-collapse',
                                weekdays: '',
                                weekday:
                                    'text-[11px] font-medium uppercase text-gray-400 w-9 h-8 text-center',
                                week: '',
                                day: 'p-0 text-center',
                                day_button:
                                    'w-9 h-9 text-sm font-medium rounded-md transition-colors ' +
                                    'hover:bg-primary-50 dark:hover:bg-primary-900/30 ' +
                                    'focus:outline-none focus:ring-2 focus:ring-primary-500',
                                selected:
                                    '[&>button]:!bg-primary-600 [&>button]:!text-white [&>button:hover]:!bg-primary-700',
                                today:
                                    '[&>button]:font-bold [&>button]:text-primary-600 dark:[&>button]:text-primary-400',
                                outside: 'text-gray-300 dark:text-gray-600',
                                disabled:
                                    'opacity-30 cursor-not-allowed [&>button]:cursor-not-allowed [&>button:hover]:!bg-transparent',
                                hidden: 'invisible',
                            }}
                        />
                    </div>,
                    document.body,
                )}

            {errorMessage && (
                <p className="mt-1 text-sm text-red-600 dark:text-red-400">{errorMessage}</p>
            )}
            {helperText && !error && (
                <p className="mt-1 text-sm text-gray-500 dark:text-gray-400">{helperText}</p>
            )}
        </div>
    )
}