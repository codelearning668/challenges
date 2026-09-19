import { Plus, List, LayoutGrid } from 'lucide-react'
import { Button } from './Button'
import type { ViewMode } from '@/hooks/useViewMode'

interface ListPageHeaderProps {
    title: string
    viewMode?: ViewMode
    onViewModeChange?: (mode: ViewMode) => void
    addLabel?: string
    onAdd?: () => void
    showAdd?: boolean
}

export function ListPageHeader({
                                   title,
                                   viewMode,
                                   onViewModeChange,
                                   addLabel = 'Add',
                                   onAdd,
                                   showAdd = false,
                               }: ListPageHeaderProps) {
    const showToggle = viewMode !== undefined && onViewModeChange !== undefined

    return (
        <div className="flex items-center justify-between mb-6">
            <h1 className="text-2xl font-bold text-gray-900 dark:text-gray-100">{title}</h1>

            <div className="flex items-center gap-2">
                {showToggle && (
                    <div className="flex items-center rounded-lg border border-gray-300 dark:border-gray-600 p-0.5">
                        <button
                            type="button"
                            onClick={() => onViewModeChange!('table')}
                            aria-label="Table view"
                            aria-pressed={viewMode === 'table'}
                            className={
                                'p-1.5 rounded-md transition-colors ' +
                                (viewMode === 'table'
                                    ? 'bg-primary-600 text-white'
                                    : 'text-gray-500 dark:text-gray-400 hover:bg-gray-100 dark:hover:bg-gray-700')
                            }
                        >
                            <List className="w-4 h-4" />
                        </button>
                        <button
                            type="button"
                            onClick={() => onViewModeChange!('grid')}
                            aria-label="Grid view"
                            aria-pressed={viewMode === 'grid'}
                            className={
                                'p-1.5 rounded-md transition-colors ' +
                                (viewMode === 'grid'
                                    ? 'bg-primary-600 text-white'
                                    : 'text-gray-500 dark:text-gray-400 hover:bg-gray-100 dark:hover:bg-gray-700')
                            }
                        >
                            <LayoutGrid className="w-4 h-4" />
                        </button>
                    </div>
                )}

                {showAdd && onAdd && (
                    <Button onClick={onAdd}>
                        <Plus className="w-4 h-4 mr-2" />
                        {addLabel}
                    </Button>
                )}
            </div>
        </div>
    )
}