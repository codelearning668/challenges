interface FilterPanelProps {
    isOpen: boolean
    activeFilterCount: number
    onClear: () => void
    children: React.ReactNode
}

export function FilterPanel({
                                isOpen,
                                activeFilterCount,
                                onClear,
                                children,
                            }: FilterPanelProps) {
    if (!isOpen) return null

    return (
        <div className="p-4 rounded-lg border border-gray-200 dark:border-gray-700 bg-gray-50 dark:bg-gray-800/50">
            {children}
            {activeFilterCount > 0 && (
                <div className="flex justify-end mt-3">
                    <button
                        type="button"
                        onClick={onClear}
                        className="text-sm font-medium text-gray-500 hover:text-gray-700 dark:text-gray-400 dark:hover:text-gray-200"
                    >
                        Clear filters
                    </button>
                </div>
            )}
        </div>
    )
}

export const filterInputCls =
    'w-full px-3 py-2 rounded-lg border border-gray-300 dark:border-gray-600 bg-white dark:bg-gray-800 text-gray-900 dark:text-gray-100 text-sm focus:outline-none focus:ring-2 focus:ring-primary-500'

export const filterLabelCls =
    'block text-xs font-medium text-gray-500 dark:text-gray-400 mb-1'