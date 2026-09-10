import { useState } from 'react'
import { Plus } from 'lucide-react'
import { useQuery } from '@tanstack/react-query'
import { useNavigate } from 'react-router-dom'
import { trackApi } from '@/services/api'
import { Button } from '@/components/shared/Button'
import { Card } from '@/components/shared/Card'
import MediaCard from '@/components/shared/MediaCard'
import { formatNumber } from '@/utils/format'
import type { TrackDetailResponse, SearchTracksCriteria } from '@/types/track'

export function TrackList() {
    const navigate = useNavigate()
    const [search, setSearch] = useState('')

    const criteria: SearchTracksCriteria = search.trim() ? { name: search.trim() } : {}

    const { data, isLoading, isError, error } = useQuery({
        queryKey: ['tracks', criteria],
        queryFn: () => trackApi.search(criteria),
    })

    const items: TrackDetailResponse[] = [...(data?.data ?? [])].sort((a, b) =>
        a.name.localeCompare(b.name),
    )

    return (
        <div>
            <div className="flex items-center justify-between mb-6">
                <h1 className="text-2xl font-bold text-gray-900 dark:text-gray-100">Tracks</h1>
                <Button onClick={() => navigate('/tracks/new')}>
                    <Plus className="w-4 h-4 mr-2" />
                    Add Track
                </Button>
            </div>

            <div className="mb-4">
                <input
                    type="text"
                    placeholder={isLoading ? 'Loading...' : 'Search tracks by name...'}
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
                        Failed to load tracks: {error instanceof Error ? error.message : 'unknown error'}
                    </p>
                ) : items.length > 0 ? (
                    <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
                        {items.map((track) => (
                            <MediaCard
                                key={track.id}
                                title={track.name}
                                subtitle={track.country ?? ''}
                                details={[
                                    {
                                        label: 'Length',
                                        value: track.lengthKm != null ? `${formatNumber(track.lengthKm)} km` : '—',
                                    },
                                ]}
                                onClick={() => navigate(`/tracks/${track.id}`)}
                            />
                        ))}
                    </div>
                ) : (
                    <p className="text-center text-gray-500 dark:text-gray-400 py-8">No tracks found</p>
                )}
            </Card>
        </div>
    )
}