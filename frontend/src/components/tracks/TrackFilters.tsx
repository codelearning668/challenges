import { FilterPanel, filterInputCls, filterLabelCls } from '@/components/shared/FilterPanel'

export interface TrackFiltersState {
    country: string
    lengthKm: string
}

export const EMPTY_TRACK_FILTERS: TrackFiltersState = { country: '', lengthKm: '' }

interface TrackFiltersProps {
    isOpen: boolean
    filters: TrackFiltersState
    onChange: (next: TrackFiltersState) => void
    onClear: () => void
    activeCount: number
}

export function TrackFilters({
                                 isOpen,
                                 filters,
                                 onChange,
                                 onClear,
                                 activeCount,
                             }: TrackFiltersProps) {
    return (
        <FilterPanel isOpen={isOpen} activeFilterCount={activeCount} onClear={onClear}>
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
                <div>
                    <label className={filterLabelCls}>Country</label>
                    <input
                        type="text"
                        placeholder="e.g. Italy"
                        value={filters.country}
                        onChange={(e) => onChange({ ...filters, country: e.target.value })}
                        className={filterInputCls}
                    />
                </div>
                <div>
                    <label className={filterLabelCls}>Length in km (exact)</label>
                    <input
                        type="number"
                        step="0.001"
                        min={0}
                        placeholder="e.g. 5.793"
                        value={filters.lengthKm}
                        onChange={(e) => onChange({ ...filters, lengthKm: e.target.value })}
                        className={filterInputCls}
                    />
                </div>
            </div>
        </FilterPanel>
    )
}