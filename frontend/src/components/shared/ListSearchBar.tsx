import { Loader2, SlidersHorizontal } from 'lucide-react'

interface ListSearchBarProps {
    value: string
    onChange: (value: string) => void
    placeholder: string
    showSpinner: boolean
    filtersOpen: boolean
    activeFilterCount: number
    onToggleFilters: () => void
}

export function ListSearchBar({
                                  value,
                                  onChange,
                                  placeholder,
                                  showSpinner,
                                  filtersOpen,
                                  activeFilterCount,
                                  onToggleFilters,
                              }: ListSearchBarProps) {
    return (
        <div className="flex gap-2">
            <div className="flex-1 relative">
                <input
                    type="text"
                    placeholder={placeholder}
                    value={value}
                    onChange={(e) => onChange(e.target.value)}
                    className="w-full px-4 py-2 pr-10 rounded-lg border border-gray-300 dark:border-gray-600 bg-white dark:bg-gray-800 text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-primary-500"
                />
                {showSpinner && (
                    <Loader2 className="absolute right-3 top-1/2 -translate-y-1/2 w-4 h-4 text-gray-400 animate-spin" />
                )}
            </div>
            <button
                type="button"
                onClick={onToggleFilters}
                className={
                    'inline-flex items-center gap-2 px-3 py-2 rounded-lg border text-sm font-medium transition-colors ' +
                    (filtersOpen || activeFilterCount > 0
                        ? 'border-primary-500 text-primary-600 dark:text-primary-400 bg-primary-50 dark:bg-primary-900/20'
                        : 'border-gray-300 dark:border-gray-600 text-gray-700 dark:text-gray-300 bg-white dark:bg-gray-800 hover:bg-gray-50 dark:hover:bg-gray-700')
                }
            >
                <SlidersHorizontal className="w-4 h-4" />
                Filters
                {activeFilterCount > 0 && (
                    <span className="text-xs font-semibold px-1.5 py-0.5 rounded bg-primary-600 text-white">
            {activeFilterCount}
          </span>
                )}
            </button>
        </div>
    )
}