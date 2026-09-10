import { useState } from 'react'
import { Plus } from 'lucide-react'
import { useQuery } from '@tanstack/react-query'
import { useNavigate } from 'react-router-dom'
import { carApi } from '@/services/api'
import { Button } from '@/components/shared/Button'
import { Card } from '@/components/shared/Card'
import MediaCard from '@/components/shared/MediaCard'
import { formatNumber } from '@/utils/format'
import type { CarDetailResponse, SearchCarsCriteria } from '@/types/car'

export function CarList() {
    const navigate = useNavigate()
    const [search, setSearch] = useState('')

    const criteria: SearchCarsCriteria = search.trim() ? { name: search.trim() } : {}

    const { data, isLoading, isError, error } = useQuery({
        queryKey: ['cars', criteria],
        queryFn: () => carApi.search(criteria),
    })

    const items: CarDetailResponse[] = [...(data?.data ?? [])].sort((a, b) =>
        a.name.localeCompare(b.name),
    )

    return (
        <div>
            <div className="flex items-center justify-between mb-6">
                <h1 className="text-2xl font-bold text-gray-900 dark:text-gray-100">Cars</h1>
                <Button onClick={() => navigate('/cars/new')}>
                    <Plus className="w-4 h-4 mr-2" />
                    Add Car
                </Button>
            </div>

            <div className="mb-4">
                <input
                    type="text"
                    placeholder={isLoading ? 'Loading...' : 'Search cars by name...'}
                    value={search}
                    onChange={(e) => setSearch(e.target.value)}
                    disabled={isLoading}
                    className="w-full px-4 py-2 rounded-lg border border-gray-300 dark:border-gray-600 bg-white dark:bg-gray-800 text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-primary-500 disabled:opacity-50"
                />
            </div>

            <Card>
                {isLoading ? (
                    <p className="text-center text-gray-500 dark:text-gray-400 py-8">Loading...</p>
                ) : isError ? (
                    <p className="text-center text-red-600 dark:text-red-400 py-8">
                        Failed to load cars: {error instanceof Error ? error.message : 'unknown error'}
                    </p>
                ) : items.length > 0 ? (
                    <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
                        {items.map((car) => (
                            <MediaCard
                                key={car.id}
                                title={car.name}
                                subtitle={car.brand}
                                details={[
                                    { label: 'HP', value: car.horsePower != null ? formatNumber(car.horsePower) : '—' },
                                    { label: 'Torque', value: car.torque != null ? `${formatNumber(car.torque)} Nm` : '—' },
                                    { label: 'Drive', value: car.wheelDrive ?? '—' },
                                ]}
                                onClick={() => navigate(`/cars/${car.id}`)}
                            />
                        ))}
                    </div>
                ) : (
                    <p className="text-center text-gray-500 dark:text-gray-400 py-8">No cars found</p>
                )}
            </Card>
        </div>
    )
}