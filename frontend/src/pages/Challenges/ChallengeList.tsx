import { useState } from 'react'
import { Plus, Loader2, SlidersHorizontal } from 'lucide-react'
import { keepPreviousData, useQuery } from '@tanstack/react-query'
import { useNavigate } from 'react-router-dom'
import { challengeApi } from '@/services/api'
import { Button } from '@/components/shared/Button'
import { Card } from '@/components/shared/Card'
import { Table } from '@/components/shared/Table'
import {
    formatDate,
    formatDurationJson,
    isChallengeActive,
} from '@/utils/format'
import { usePermissions } from '@/hooks/usePermissions'
import { useDebouncedValue } from '@/hooks/useDebouncedValue'
import type {
    ChallengeSummaryResponse,
    SearchChallengesCriteria,
} from '@/types/challenge'

interface Filters {
    endDate: string
    bestParticipantName: string
    trackCountry: string
    carBrand: string
    carName: string
}

const EMPTY_FILTERS: Filters = {
    endDate: '',
    bestParticipantName: '',
    trackCountry: '',
    carBrand: '',
    carName: '',
}

const filterInputCls =
    'w-full px-3 py-2 rounded-lg border border-gray-300 dark:border-gray-600 bg-white dark:bg-gray-800 text-gray-900 dark:text-gray-100 text-sm focus:outline-none focus:ring-2 focus:ring-primary-500'

const filterLabelCls =
    'block text-xs font-medium text-gray-500 dark:text-gray-400 mb-1'

function StatusBadge({ endDate }: { endDate: string }) {
    const active = isChallengeActive(endDate)
    return (
        <span
            className={
                'inline-flex items-center px-2 py-0.5 rounded text-xs font-medium ' +
                (active
                    ? 'bg-green-50 text-green-700 dark:bg-green-900/30 dark:text-green-300'
                    : 'bg-gray-100 text-gray-600 dark:bg-gray-700 dark:text-gray-300')
            }
        >
      {active ? 'Active' : 'Closed'}
    </span>
    )
}

export function ChallengeList() {
    const [search, setSearch] = useState('')
    const [filters, setFilters] = useState<Filters>(EMPTY_FILTERS)
    const [showFilters, setShowFilters] = useState(false)
    const navigate = useNavigate()
    const { isAdmin } = usePermissions()

    const dSearch = useDebouncedValue(search, 300)
    const dBest = useDebouncedValue(filters.bestParticipantName, 300)
    const dTrackCountry = useDebouncedValue(filters.trackCountry, 300)
    const dCarBrand = useDebouncedValue(filters.carBrand, 300)
    const dCarName = useDebouncedValue(filters.carName, 300)
    // endDate is a date picker; updates immediately.

    const criteria: SearchChallengesCriteria = {
        ...(dSearch.trim() ? { trackName: dSearch.trim() } : {}),
        ...(filters.endDate ? { endDate: filters.endDate } : {}),
        ...(dBest.trim() ? { bestParticipantName: dBest.trim() } : {}),
        ...(dTrackCountry.trim() ? { trackCountry: dTrackCountry.trim() } : {}),
        ...(dCarBrand.trim() ? { carBrand: dCarBrand.trim() } : {}),
        ...(dCarName.trim() ? { carName: dCarName.trim() } : {}),
    }

    const criteriaKey = JSON.stringify(criteria)

    const { data, isLoading, isFetching } = useQuery({
        queryKey: ['challenges', criteriaKey],
        queryFn: () => challengeApi.search(criteria),
        placeholderData: keepPreviousData,
        refetchOnMount: 'always',
    })

    const rows = data?.data ?? []
    const firstLoad = isLoading && !data

    const activeFilterCount = Object.values(filters).filter((v) => v !== '').length
    const clearFilters = () => setFilters(EMPTY_FILTERS)
    const isFiltering = dSearch.trim().length > 0 || activeFilterCount > 0

    return (
        <div>
            <div className="flex items-center justify-between mb-6">
                <h1 className="text-2xl font-bold text-gray-900 dark:text-gray-100">Challenges</h1>
                {isAdmin && (
                    <Button onClick={() => navigate('/challenges/new')}>
                        <Plus className="w-4 h-4 mr-2" />
                        Add Challenge
                    </Button>
                )}
            </div>

            <div className="mb-4 space-y-3">
                <div className="flex gap-2">
                    <div className="flex-1 relative">
                        <input
                            type="text"
                            placeholder="Search by track name..."
                            value={search}
                            onChange={(e) => setSearch(e.target.value)}
                            className="w-full px-4 py-2 pr-10 rounded-lg border border-gray-300 dark:border-gray-600 bg-white dark:bg-gray-800 text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-primary-500"
                        />
                        {isFetching && !firstLoad && isFiltering && (
                            <Loader2 className="absolute right-3 top-1/2 -translate-y-1/2 w-4 h-4 text-gray-400 animate-spin" />
                        )}
                    </div>
                    <button
                        type="button"
                        onClick={() => setShowFilters((v) => !v)}
                        className={
                            'inline-flex items-center gap-2 px-3 py-2 rounded-lg border text-sm font-medium transition-colors ' +
                            (showFilters || activeFilterCount > 0
                                ? 'border-primary-500 text-primary-600 dark:text-primary-400 bg-primary-50 dark:bg-primary-900/20'
                                : 'border-gray-300 dark:border-gray-600 text-gray-700 dark:text-gray-300 bg-white dark:bg-gray-800 hover:bg-gray-50 dark:hover:bg-gray-700')
                        }
                    >
                        <SlidersHorizontal className="w-4 h-4" />
                        Filters
                        {activeFilterCount > 0 && (
                            <span className="text-xs font-semibold px-1.5 py-0.5 rounded bg-primary-600 text-white">
                {activeFilterCount}
              </span>
                        )}
                    </button>
                </div>

                {showFilters && (
                    <div className="p-4 rounded-lg border border-gray-200 dark:border-gray-700 bg-gray-50 dark:bg-gray-800/50">
                        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-5 gap-3">
                            <div>
                                <label className={filterLabelCls}>End Date</label>
                                <input
                                    type="date"
                                    value={filters.endDate}
                                    onChange={(e) => setFilters({ ...filters, endDate: e.target.value })}
                                    className={filterInputCls}
                                />
                            </div>
                            <div>
                                <label className={filterLabelCls}>Best Driver</label>
                                <input
                                    type="text"
                                    placeholder="e.g. Martin"
                                    value={filters.bestParticipantName}
                                    onChange={(e) =>
                                        setFilters({ ...filters, bestParticipantName: e.target.value })
                                    }
                                    className={filterInputCls}
                                />
                            </div>
                            <div>
                                <label className={filterLabelCls}>Track Country</label>
                                <input
                                    type="text"
                                    placeholder="e.g. Italy"
                                    value={filters.trackCountry}
                                    onChange={(e) =>
                                        setFilters({ ...filters, trackCountry: e.target.value })
                                    }
                                    className={filterInputCls}
                                />
                            </div>
                            <div>
                                <label className={filterLabelCls}>Car Brand</label>
                                <input
                                    type="text"
                                    placeholder="e.g. Ferrari"
                                    value={filters.carBrand}
                                    onChange={(e) => setFilters({ ...filters, carBrand: e.target.value })}
                                    className={filterInputCls}
                                />
                            </div>
                            <div>
                                <label className={filterLabelCls}>Car Name</label>
                                <input
                                    type="text"
                                    placeholder="e.g. 488 GTB"
                                    value={filters.carName}
                                    onChange={(e) => setFilters({ ...filters, carName: e.target.value })}
                                    className={filterInputCls}
                                />
                            </div>
                        </div>
                        {activeFilterCount > 0 && (
                            <div className="flex justify-end mt-3">
                                <button
                                    type="button"
                                    onClick={clearFilters}
                                    className="text-sm font-medium text-gray-500 hover:text-gray-700 dark:text-gray-400 dark:hover:text-gray-200"
                                >
                                    Clear filters
                                </button>
                            </div>
                        )}
                    </div>
                )}
            </div>

            <Card>
                {firstLoad ? (
                    <p className="text-center text-gray-500 dark:text-gray-400 py-8">Loading...</p>
                ) : (
                    <Table<ChallengeSummaryResponse>
                        columns={[
                            {
                                key: 'challengeEndDate',
                                label: 'Status',
                                render: (v) => <StatusBadge endDate={v} />,
                            },
                            { key: 'trackName', label: 'Track' },
                            { key: 'trackCountry', label: 'Country' },
                            { key: 'carBrand', label: 'Brand' },
                            { key: 'carName', label: 'Car' },
                            { key: 'challengeEndDate', label: 'End Date', render: (v) => formatDate(v) },
                            { key: 'bestLapTime', label: 'Best Lap', render: (v) => formatDurationJson(v) },
                            { key: 'bestParticipantName', label: 'Best Driver' },
                        ]}
                        rows={rows}
                        onRowClick={(row) => navigate(`/challenges/${row.challengeId}`)}
                        emptyMessage="No challenges found"
                    />
                )}
            </Card>
        </div>
    )
}