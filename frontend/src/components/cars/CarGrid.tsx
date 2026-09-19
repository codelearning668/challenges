import MediaCard from '@/components/shared/MediaCard'
import { formatNumber } from '@/utils/format'
import type { CarDetailResponse } from '@/types/car'

interface CarGridProps {
    items: CarDetailResponse[]
    onItemClick: (car: CarDetailResponse) => void
}

export function CarGrid({ items, onItemClick }: CarGridProps) {
    return (
        <div className="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-4 xl:grid-cols-5 gap-3">
            {items.map((car) => (
                <MediaCard
                    key={car.id}
                    compact
                    onClick={() => onItemClick(car)}
                    headerRow={
                        <div className="flex items-baseline justify-between gap-2">
              <span className="text-xs font-medium text-gray-500 dark:text-gray-400 truncate">
                {car.brand}
              </span>
                            <span className="text-sm font-semibold text-gray-900 dark:text-gray-100 truncate text-right">
                {car.name}
              </span>
                        </div>
                    }
                    details={[
                        {
                            label: 'HP',
                            value: car.horsePower != null ? formatNumber(car.horsePower) : '—',
                        },
                        {
                            label: 'Torque',
                            value: car.torque != null ? `${formatNumber(car.torque)} Nm` : '—',
                        },
                        { label: 'Drive', value: car.wheelDrive ?? '—' },
                    ]}
                />
            ))}
        </div>
    )
}