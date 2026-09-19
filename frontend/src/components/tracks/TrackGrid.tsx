import MediaCard from '@/components/shared/MediaCard'
import { formatNumber } from '@/utils/format'
import type { TrackDetailResponse } from '@/types/track'

interface TrackGridProps {
    items: TrackDetailResponse[]
    onItemClick: (track: TrackDetailResponse) => void
}

export function TrackGrid({ items, onItemClick }: TrackGridProps) {
    return (
        <div className="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-4 xl:grid-cols-5 gap-3">
            {items.map((track) => (
                <MediaCard
                    key={track.id}
                    compact
                    onClick={() => onItemClick(track)}
                    title={track.name}
                    subtitle={track.country ?? ''}
                    details={[
                        {
                            label: 'Length',
                            value: track.lengthKm != null ? `${formatNumber(track.lengthKm)} km` : '—',
                        },
                    ]}
                />
            ))}
        </div>
    )
}