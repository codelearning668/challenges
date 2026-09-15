import { useEffect, useState } from 'react'
import { AlertCircle, AlertTriangle, CheckCircle2, Info, X } from 'lucide-react'
import { clsx, type ClassValue } from 'clsx'
import { twMerge } from 'tailwind-merge'
import { useToastStore, type ToastItem, type ToastType } from '@/stores/useToastStore'

function cn(...inputs: ClassValue[]) {
    return twMerge(clsx(inputs))
}

const ICONS: Record<ToastType, React.ElementType> = {
    success: CheckCircle2,
    error: AlertCircle,
    warning: AlertTriangle,
    info: Info,
}

const CONTAINER_STYLES: Record<ToastType, string> = {
    success:
        'bg-green-50 text-green-900 border-green-200 dark:bg-green-900/60 dark:text-green-50 dark:border-green-800',
    error:
        'bg-red-50 text-red-900 border-red-200 dark:bg-red-900/60 dark:text-red-50 dark:border-red-800',
    warning:
        'bg-yellow-50 text-yellow-900 border-yellow-200 dark:bg-yellow-900/60 dark:text-yellow-50 dark:border-yellow-800',
    info:
        'bg-blue-50 text-blue-900 border-blue-200 dark:bg-blue-900/60 dark:text-blue-50 dark:border-blue-800',
}

const ICON_COLOR: Record<ToastType, string> = {
    success: 'text-green-600 dark:text-green-300',
    error: 'text-red-600 dark:text-red-300',
    warning: 'text-yellow-600 dark:text-yellow-300',
    info: 'text-blue-600 dark:text-blue-300',
}

function ToastCard({ toast }: { toast: ToastItem }) {
    const dismiss = useToastStore((s) => s.dismiss)
    const [visible, setVisible] = useState(false)

    useEffect(() => {
        const t = window.setTimeout(() => setVisible(true), 10)
        return () => window.clearTimeout(t)
    }, [])

    useEffect(() => {
        if (toast.duration <= 0) return
        const t = window.setTimeout(() => dismiss(toast.id), toast.duration)
        return () => window.clearTimeout(t)
    }, [toast.id, toast.duration, dismiss])

    const Icon = ICONS[toast.type]

    return (
        <div
            role="alert"
    className={cn(
        'flex items-start gap-3 w-80 max-w-[calc(100vw-2rem)] px-4 py-3 rounded-lg border shadow-lg backdrop-blur-sm',
        'transition-all duration-200 ease-out',
        visible ? 'opacity-100 translate-x-0' : 'opacity-0 translate-x-4',
        CONTAINER_STYLES[toast.type],
)}
>
    <Icon className={cn('w-5 h-5 shrink-0 mt-0.5', ICON_COLOR[toast.type])} />
    <p className="flex-1 text-sm font-medium break-words whitespace-pre-wrap">
        {toast.message}
        </p>
        <button
    type="button"
    onClick={() => dismiss(toast.id)}
    aria-label="Dismiss"
    className="shrink-0 p-1 -mr-1 rounded hover:bg-black/10 dark:hover:bg-white/10 transition-colors"
    >
    <X className="w-4 h-4" />
        </button>
        </div>
)
}

export function Toaster() {
    const toasts = useToastStore((s) => s.toasts)

    if (toasts.length === 0) return null

    return (
        <div className="fixed top-4 right-4 z-[100] flex flex-col gap-2 pointer-events-none">
            {toasts.map((t) => (
                    <div key={t.id} className="pointer-events-auto">
                <ToastCard toast={t} />
    </div>
))}
    </div>
)
}