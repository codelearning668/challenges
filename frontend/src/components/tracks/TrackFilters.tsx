import { FilterPanel, filterInputCls, filterLabelCls } from '@/components/shared/FilterPanel'
import { useSimulators } from '@/hooks/useSimulators'

export interface TrackFiltersState {
    country: string
    lengthKm: string
    simulatorId: string
    fromDlc: '' | 'true' | 'false'
}

export const EMPTY_TRACK_FILTERS: TrackFiltersState = {
    country: '',
    lengthKm: '',
    simulatorId: '',
    fromDlc: '',
}

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
    const { data: simulatorsRes } = useSimulators()
    const simulatorOptions = simulatorsRes?.data ?? []

    return (
        <FilterPanel isOpen={isOpen} activeFilterCount={activeCount} onClear={onClear}>
            <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-3">
                <div>
                    <label className={filterLabelCls}>Simulator</label>
                    <select
                        value={filters.simulatorId}
                        onChange={(e) => onChange({ ...filters, simulatorId: e.target.value })}
                        className={filterInputCls}
                    >
                        <option value="">Any</option>
                        {simulatorOptions.map((s) => (
                            <option key={s.id} value={s.id}>
                                {s.name}
                            </option>
                        ))}
                    </select>
                </div>
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
                <div>
                    <label className={filterLabelCls}>Availability</label>
                    <select
                        value={filters.fromDlc}
                        onChange={(e) =>
                            onChange({
                                ...filters,
                                fromDlc: e.target.value as TrackFiltersState['fromDlc'],
                            })
                        }
                        className={filterInputCls}
                    >
                        <option value="">Any</option>
                        <option value="true">DLC only</option>
                        <option value="false">Base game only</option>
                    </select>
                </div>
            </div>
        </FilterPanel>
    )
}