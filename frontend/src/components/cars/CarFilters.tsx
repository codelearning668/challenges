import { FilterPanel, filterInputCls, filterLabelCls } from '@/components/shared/FilterPanel'
import type { WheelDrive } from '@/types/car'

export interface CarFiltersState {
    brand: string
    horsePower: string
    torque: string
    wheelDrive: WheelDrive | ''
}

export const EMPTY_CAR_FILTERS: CarFiltersState = {
    brand: '',
    horsePower: '',
    torque: '',
    wheelDrive: '',
}

interface CarFiltersProps {
    isOpen: boolean
    filters: CarFiltersState
    onChange: (next: CarFiltersState) => void
    onClear: () => void
    activeCount: number
}

export function CarFilters({ isOpen, filters, onChange, onClear, activeCount }: CarFiltersProps) {
    return (
        <FilterPanel isOpen={isOpen} activeFilterCount={activeCount} onClear={onClear}>
            <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-3">
                <div>
                    <label className={filterLabelCls}>Brand</label>
                    <input
                        type="text"
                        placeholder="e.g. Ferrari"
                        value={filters.brand}
                        onChange={(e) => onChange({ ...filters, brand: e.target.value })}
                        className={filterInputCls}
                    />
                </div>
                <div>
                    <label className={filterLabelCls}>Wheel drive</label>
                    <select
                        value={filters.wheelDrive}
                        onChange={(e) =>
                            onChange({ ...filters, wheelDrive: e.target.value as WheelDrive | '' })
                        }
                        className={filterInputCls}
                    >
                        <option value="">Any</option>
                        <option value="FRONT">FRONT</option>
                        <option value="REAR">REAR</option>
                        <option value="ALL">ALL</option>
                    </select>
                </div>
                <div>
                    <label className={filterLabelCls}>Horsepower (exact)</label>
                    <input
                        type="number"
                        min={0}
                        placeholder="e.g. 500"
                        value={filters.horsePower}
                        onChange={(e) => onChange({ ...filters, horsePower: e.target.value })}
                        className={filterInputCls}
                    />
                </div>
                <div>
                    <label className={filterLabelCls}>Torque (exact)</label>
                    <input
                        type="number"
                        min={0}
                        placeholder="e.g. 500"
                        value={filters.torque}
                        onChange={(e) => onChange({ ...filters, torque: e.target.value })}
                        className={filterInputCls}
                    />
                </div>
            </div>
        </FilterPanel>
    )
}