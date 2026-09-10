import { useState } from 'react'
import { Link } from 'react-router-dom'
import { Car as CarIcon, Flag, Calendar, ImageOff } from 'lucide-react'
import { useQuery } from '@tanstack/react-query'
import { carApi, trackApi, challengeApi } from '@/services/api'
import { Modal } from '@/components/shared/Modal'
import { formatDate, formatDurationJson, formatNumber } from '@/utils/format'
import type { CarDetailResponse } from '@/types/car'
import type { TrackDetailResponse } from '@/types/track'
import type { ChallengeSummaryResponse } from '@/types/challenge'

type Category = 'cars' | 'tracks' | 'challenges'

type Selection =
    | { type: 'cars'; item: CarDetailResponse }
    | { type: 'tracks'; item: TrackDetailResponse }
    | { type: 'challenges'; item: ChallengeSummaryResponse }

// ---------- shared bits ----------

function DetailRow({ label, value }: { label: string; value: React.ReactNode }) {
  return (
      <div className="flex items-start justify-between gap-4 py-2 border-b border-gray-100 dark:border-gray-700 last:border-0">
        <span className="text-sm text-gray-500 dark:text-gray-400">{label}</span>
        <span className="text-sm font-medium text-gray-900 dark:text-gray-100 text-right break-words">
        {value ?? '—'}
      </span>
      </div>
  )
}

function CarDetails({ car }: { car: CarDetailResponse }) {
  return (
      <div>
        <DetailRow label="Name" value={car.name} />
        <DetailRow label="Brand" value={car.brand} />
        <DetailRow
            label="Horsepower"
            value={car.horsePower != null ? `${formatNumber(car.horsePower)} hp` : null}
        />
        <DetailRow
            label="Torque"
            value={car.torque != null ? `${formatNumber(car.torque)} Nm` : null}
        />
        <DetailRow label="Drive" value={car.wheelDrive} />
      </div>
  )
}

function TrackDetails({ track }: { track: TrackDetailResponse }) {
  return (
      <div>
        <DetailRow label="Name" value={track.name} />
        <DetailRow label="Country" value={track.country} />
        <DetailRow
            label="Length"
            value={track.lengthKm != null ? `${formatNumber(track.lengthKm)} km` : null}
        />
      </div>
  )
}

function ChallengeDetails({ challenge }: { challenge: ChallengeSummaryResponse }) {
  return (
      <div>
        <DetailRow label="Track" value={challenge.trackName} />
        <DetailRow label="Country" value={challenge.trackCountry} />
        <DetailRow
            label="Car"
            value={`${challenge.carBrand} ${challenge.carName}`.trim()}
        />
        <DetailRow
            label="End Date"
            value={challenge.challengeEndDate ? formatDate(challenge.challengeEndDate) : null}
        />
        <DetailRow label="Best Lap" value={formatDurationJson(challenge.bestLapTime)} />
        <DetailRow label="Best Driver" value={challenge.bestParticipantName} />

        <div className="mt-4 flex justify-end">
          <Link
              to={`/challenges/${challenge.challengeId}`}
              className="text-sm font-medium text-primary-600 hover:text-primary-700 dark:text-primary-400"
          >
            View full details →
          </Link>
        </div>
      </div>
  )
}

function DetailPanel({ selection }: { selection: Selection }) {
  if (selection.type === 'cars') return <CarDetails car={selection.item} />
  if (selection.type === 'tracks') return <TrackDetails track={selection.item} />
  return <ChallengeDetails challenge={selection.item} />
}

function DetailModalBody({ selection }: { selection: Selection }) {
  const img = (selection.item as { imageUrl?: string | null }).imageUrl ?? undefined
  const title =
      selection.type === 'cars' ? selection.item.name
          : selection.type === 'tracks' ? selection.item.name
              : `${selection.item.trackName}`

  return (
      <div>
        <div className="w-full h-56 mb-4 rounded-lg bg-gradient-to-br from-gray-100 to-gray-200 dark:from-gray-700 dark:to-gray-800 border border-gray-200 dark:border-gray-700 flex items-center justify-center overflow-hidden">
          {img ? (
              <img src={img} alt={title} className="w-full h-full object-cover" />
          ) : (
              <div className="flex flex-col items-center justify-center text-gray-400 dark:text-gray-500">
                <ImageOff className="w-10 h-10" />
                <span className="text-xs mt-1">No image</span>
              </div>
          )}
        </div>

        <DetailPanel selection={selection} />
      </div>
  )
}

// ---------- generic grid ----------

interface GridProps<T> {
  items: T[]
  getKey: (item: T) => React.Key
  getTitle: (item: T) => string
  getSubtitle: (item: T) => string
  onSelect: (item: T) => void
}

function Grid<T>({ items, getKey, getTitle, getSubtitle, onSelect }: GridProps<T>) {
  return (
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
        {items.map((item) => {
          const subtitle = getSubtitle(item)
          return (
              <button
                  key={getKey(item)}
                  onClick={() => onSelect(item)}
                  className="text-left bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 p-4 hover:shadow-md hover:border-primary-300 dark:hover:border-primary-700 transition-all"
              >
                <h3 className="font-semibold text-gray-900 dark:text-gray-100 truncate">
                  {getTitle(item)}
                </h3>
                {subtitle && (
                    <p className="text-sm text-gray-500 dark:text-gray-400 mt-1 truncate">{subtitle}</p>
                )}
              </button>
          )
        })}
      </div>
  )
}

// ---------- main ----------

const byName = (a: { name?: string | null }, b: { name?: string | null }) =>
    (a.name ?? '').localeCompare(b.name ?? '')

export function Dashboard() {
  const [activeCategory, setActiveCategory] = useState<Category>('cars')
  const [selection, setSelection] = useState<Selection | null>(null)

  const { data: cars, isLoading: carsLoading } = useQuery({
    queryKey: ['cars'],
    queryFn: () => carApi.search(),
  })
  const { data: tracks, isLoading: tracksLoading } = useQuery({
    queryKey: ['tracks'],
    queryFn: () => trackApi.search(),
  })
  const { data: challenges, isLoading: challengesLoading } = useQuery({
    queryKey: ['challenges'],
    queryFn: () => challengeApi.search(),
  })

  const carsList = [...(cars?.data ?? [])].sort(byName)
  const tracksList = [...(tracks?.data ?? [])].sort(byName)
  const challengesList = [...(challenges?.data ?? [])].sort((a, b) =>
      (a.trackName ?? '').localeCompare(b.trackName ?? ''),
  )

  const tabs = [
    { key: 'cars' as const, label: 'Cars', icon: CarIcon, count: carsList.length },
    { key: 'tracks' as const, label: 'Tracks', icon: Flag, count: tracksList.length },
    { key: 'challenges' as const, label: 'Challenges', icon: Calendar, count: challengesList.length },
  ]

  const isLoading =
      activeCategory === 'cars'
          ? carsLoading
          : activeCategory === 'tracks'
              ? tracksLoading
              : challengesLoading

  return (
      <div>
        <h1 className="text-2xl font-bold text-gray-900 dark:text-gray-100 mb-4">Dashboard</h1>

        {/* Tabs */}
        <div className="flex flex-wrap items-center gap-2 mb-4">
          {tabs.map(({ key, label, icon: Icon, count }) => {
            const active = activeCategory === key
            return (
                <button
                    key={key}
                    onClick={() => setActiveCategory(key)}
                    className={
                        'flex items-center gap-2 px-3 py-1.5 rounded-lg text-sm font-medium transition-colors ' +
                        (active
                            ? 'bg-primary-600 text-white'
                            : 'bg-white dark:bg-gray-800 text-gray-700 dark:text-gray-300 border border-gray-200 dark:border-gray-700 hover:bg-gray-50 dark:hover:bg-gray-700')
                    }
                >
                  <Icon className="w-4 h-4" />
                  <span>{label}</span>
                  <span
                      className={
                          'text-xs font-semibold px-1.5 py-0.5 rounded ' +
                          (active
                              ? 'bg-white/20 text-white'
                              : 'bg-gray-100 dark:bg-gray-700 text-gray-600 dark:text-gray-300')
                      }
                  >
                {count}
              </span>
                </button>
            )
          })}
        </div>

        {/* Grid */}
        {isLoading ? (
            <p className="text-gray-500 dark:text-gray-400">Loading…</p>
        ) : activeCategory === 'cars' ? (
            carsList.length === 0 ? (
                <p className="text-gray-500 dark:text-gray-400">No cars yet.</p>
            ) : (
                <Grid
                    items={carsList}
                    getKey={(c) => c.id}
                    getTitle={(c) => c.name}
                    getSubtitle={(c) =>
                        [c.brand, c.horsePower != null ? `${formatNumber(c.horsePower)} hp` : null]
                            .filter(Boolean)
                            .join(' · ')
                    }
                    onSelect={(item) => setSelection({ type: 'cars', item })}
                />
            )
        ) : activeCategory === 'tracks' ? (
            tracksList.length === 0 ? (
                <p className="text-gray-500 dark:text-gray-400">No tracks yet.</p>
            ) : (
                <Grid
                    items={tracksList}
                    getKey={(t) => t.id}
                    getTitle={(t) => t.name}
                    getSubtitle={(t) =>
                        [t.country, t.lengthKm != null ? `${formatNumber(t.lengthKm)} km` : null]
                            .filter(Boolean)
                            .join(' · ')
                    }
                    onSelect={(item) => setSelection({ type: 'tracks', item })}
                />
            )
        ) : challengesList.length === 0 ? (
            <p className="text-gray-500 dark:text-gray-400">No challenges yet.</p>
        ) : (
            <Grid
                items={challengesList}
                getKey={(c) => c.challengeId}
                getTitle={(c) => c.trackName}
                getSubtitle={(c) =>
                    [`${c.carBrand} ${c.carName}`.trim(), formatDate(c.challengeEndDate)]
                        .filter(Boolean)
                        .join(' · ')
                }
                onSelect={(item) => setSelection({ type: 'challenges', item })}
            />
        )}

        <Modal
            isOpen={!!selection}
            onClose={() => setSelection(null)}
            title={
              selection?.type === 'challenges'
                  ? selection.item.trackName
                  : selection?.item?.name || 'Details'
            }
            size="md"
        >
          {selection && <DetailModalBody selection={selection} />}
        </Modal>
      </div>
  )
}