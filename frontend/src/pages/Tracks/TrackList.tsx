import { useState } from 'react'
import { keepPreviousData, useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { useNavigate } from 'react-router-dom'
import { trackApi } from '@/services/api'
import { Card } from '@/components/shared/Card'
import { ListPageHeader } from '@/components/shared/ListPageHeader'
import { ListSearchBar } from '@/components/shared/ListSearchBar'
import { EntityDetailDialog } from '@/components/shared/EntityDetailDialog'
import { TrackFilters, EMPTY_TRACK_FILTERS, type TrackFiltersState } from '@/components/tracks/TrackFilters'
import { TrackTable, type TrackEditForm } from '@/components/tracks/TrackTable'
import { TrackGrid } from '@/components/tracks/TrackGrid'
import { formatNumber } from '@/utils/format'
import { usePermissions } from '@/hooks/usePermissions'
import { useDebouncedValue } from '@/hooks/useDebouncedValue'
import { useViewMode } from '@/hooks/useViewMode'
import { toast } from '@/stores/useToastStore'
import type { TrackDetailResponse, SearchTracksCriteria } from '@/types/track'

export function TrackList() {
    const navigate = useNavigate()
    const queryClient = useQueryClient()
    const { isAdmin } = usePermissions()
    const [viewMode, setViewMode] = useViewMode('tracks', 'table')

    const [search, setSearch] = useState('')
    const [filters, setFilters] = useState<TrackFiltersState>(EMPTY_TRACK_FILTERS)
    const [showFilters, setShowFilters] = useState(false)

    const [editingId, setEditingId] = useState<number | null>(null)
    const [form, setForm] = useState<TrackEditForm | null>(null)
    const [selectedTrack, setSelectedTrack] = useState<TrackDetailResponse | null>(null)

    const dSearch = useDebouncedValue(search, 300)
    const dCountry = useDebouncedValue(filters.country, 300)
    const dLength = useDebouncedValue(filters.lengthKm, 300)

    const criteria: SearchTracksCriteria = {
        ...(dSearch.trim() ? { name: dSearch.trim() } : {}),
        ...(dCountry.trim() ? { country: dCountry.trim() } : {}),
        ...(dLength.trim() && Number.isFinite(Number(dLength)) ? { lengthKm: Number(dLength) } : {}),
    }
    const criteriaKey = JSON.stringify(criteria)

    const { data, isLoading, isFetching, isError, error } = useQuery({
        queryKey: ['tracks', criteriaKey],
        queryFn: () => trackApi.search(criteria),
        placeholderData: keepPreviousData,
        refetchOnMount: 'always',
    })

    const items: TrackDetailResponse[] = [...(data?.data ?? [])].sort((a, b) =>
        a.name.localeCompare(b.name),
    )

    const updateMutation = useMutation({
        mutationFn: (vars: { id: number; body: TrackEditForm }) =>
            trackApi.update(vars.id, {
                name: vars.body.name.trim(),
                country: vars.body.country.trim() || undefined,
                lengthKm: vars.body.lengthKm === '' ? undefined : Number(vars.body.lengthKm),
            }),
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: ['tracks'] })
            toast.success('Track updated')
            cancelEdit()
        },
    })

    const beginEdit = (track: TrackDetailResponse) => {
        setEditingId(track.id)
        setForm({
            name: track.name ?? '',
            country: track.country ?? '',
            lengthKm: track.lengthKm != null ? String(track.lengthKm) : '',
        })
    }

    const cancelEdit = () => {
        setEditingId(null)
        setForm(null)
    }

    const save = (id: number) => {
        if (!form) return
        if (!form.name.trim()) {
            toast.error('Name is required')
            return
        }
        updateMutation.mutate({ id, body: form })
    }

    const openDetail = (track: TrackDetailResponse) => {
        if (editingId !== null) return
        setSelectedTrack(track)
    }

    const activeFilterCount = Object.values(filters).filter((v) => v !== '').length
    const isFiltering = dSearch.trim().length > 0 || activeFilterCount > 0
    const firstLoad = isLoading && !data

    return (
        <div>
            <ListPageHeader
                title="Tracks"
                viewMode={viewMode}
                onViewModeChange={setViewMode}
                showAdd={isAdmin}
                onAdd={() => navigate('/tracks/new')}
                addLabel="Add Track"
            />

            <div className="mb-4 space-y-3">
                <ListSearchBar
                    value={search}
                    onChange={setSearch}
                    placeholder="Search tracks by name..."
                    showSpinner={isFetching && !firstLoad && isFiltering}
                    filtersOpen={showFilters}
                    activeFilterCount={activeFilterCount}
                    onToggleFilters={() => setShowFilters((v) => !v)}
                />
                <TrackFilters
                    isOpen={showFilters}
                    filters={filters}
                    onChange={setFilters}
                    onClear={() => setFilters(EMPTY_TRACK_FILTERS)}
                    activeCount={activeFilterCount}
                />
            </div>

            {isError ? (
                <Card>
                    <p className="text-center text-red-600 dark:text-red-400 py-8">
                        Failed to load tracks: {error instanceof Error ? error.message : 'unknown error'}
                    </p>
                </Card>
            ) : firstLoad ? (
                <Card>
                    <p className="text-center text-gray-500 dark:text-gray-400 py-8">Loading...</p>
                </Card>
            ) : items.length === 0 ? (
                <Card>
                    <p className="text-center text-gray-500 dark:text-gray-400 py-8">No tracks found</p>
                </Card>
            ) : viewMode === 'grid' ? (
                <TrackGrid items={items} onItemClick={openDetail} />
            ) : (
                <TrackTable
                    items={items}
                    isAdmin={isAdmin}
                    editingId={editingId}
                    form={form}
                    onFormChange={setForm}
                    onBeginEdit={beginEdit}
                    onCancelEdit={cancelEdit}
                    onSave={save}
                    onRowClick={openDetail}
                    isSaving={updateMutation.isPending}
                />
            )}

            <EntityDetailDialog
                isOpen={selectedTrack !== null}
                onClose={() => setSelectedTrack(null)}
                title={selectedTrack?.name ?? ''}
                fields={
                    selectedTrack
                        ? [
                            { label: 'Name', value: selectedTrack.name },
                            { label: 'Country', value: selectedTrack.country ?? '—' },
                            {
                                label: 'Length',
                                value:
                                    selectedTrack.lengthKm != null
                                        ? `${formatNumber(selectedTrack.lengthKm)} km`
                                        : '—',
                            },
                        ]
                        : []
                }
            />
        </div>
    )
}