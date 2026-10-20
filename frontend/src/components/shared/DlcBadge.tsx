interface DlcBadgeProps {
    value: boolean
    showBase?: boolean
}

export function DlcBadge({ value, showBase = false }: DlcBadgeProps) {
    if (value) {
        return (
            <span className="inline-flex items-center px-1.5 py-0.5 rounded text-[10px] font-semibold uppercase tracking-wide bg-purple-50 text-purple-700 dark:bg-purple-900/30 dark:text-purple-300">
        DLC
      </span>
        )
    }
    if (showBase) {
        return (
            <span className="inline-flex items-center px-1.5 py-0.5 rounded text-[10px] font-semibold uppercase tracking-wide bg-gray-100 text-gray-600 dark:bg-gray-700 dark:text-gray-300">
        Base sim
      </span>
        )
    }
    return <span className="text-gray-400 dark:text-gray-500">—</span>
}