import { FilterPanel, filterInputCls, filterLabelCls } from '@/components/shared/FilterPanel'
import { useSimulators } from '@/hooks/useSimulators'
import type { WheelDrive } from '@/types/car'

export interface CarFiltersState {
    brand: string
    horsePower: string
    torque: string
    wheelDrive: WheelDrive | ''
    simulatorId: string
    /** '' = any, 'true' = DLC only, 'false' = base game only */
    fromDlc: '' | 'true' | 'false'
}

export const EMPTY_CAR_FILTERS: CarFiltersState = {
    brand: '',
    horsePower: '',
    torque: '',
    wheelDrive: '',
    simulatorId: '',
    fromDlc: '',
}

interface CarFiltersProps {
    isOpen: boolean
    filters: CarFiltersState
    onChange: (next: CarFiltersState) => void
    onClear: () => void
    activeCount: number
}

export function CarFilters({
                               isOpen,
                               filters,
                               onChange,
                               onClear,
                               activeCount,
                           }: CarFiltersProps) {
    const { data: simulatorsRes } = useSimulators()
    const simulatorOptions = simulatorsRes?.data ?? []

    return (
        <FilterPanel isOpen={isOpen} activeFilterCount={activeCount} onClear={onClear}>
            <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-6 gap-3">
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
                        placeholder="e.g. 550"
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
                        placeholder="e.g. 700"
                        value={filters.torque}
                        onChange={(e) => onChange({ ...filters, torque: e.target.value })}
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
                                fromDlc: e.target.value as CarFiltersState['fromDlc'],
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