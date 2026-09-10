import { useState } from 'react'
import { Plus } from 'lucide-react'
import { useQuery } from '@tanstack/react-query'
import { useNavigate } from 'react-router-dom'
import { challengeApi } from '@/services/api'
import { Button } from '@/components/shared/Button'
import { Card } from '@/components/shared/Card'
import { Table } from '@/components/shared/Table'
import { formatDate, formatDurationJson } from '@/utils/format'
import type { ChallengeSummaryResponse } from '@/types/challenge'

export function ChallengeList() {
    const [search, setSearch] = useState('')
    const navigate = useNavigate()

    const { data, isLoading } = useQuery({
        queryKey: ['challenges', search],
        queryFn: () =>
            challengeApi.search(search.trim() ? { trackName: search.trim() } : {}),
    })

    const rows = data?.data ?? []

    return (
        <div>
            <div className="flex items-center justify-between mb-6">
                <h1 className="text-2xl font-bold text-gray-900 dark:text-gray-100">Challenges</h1>
                <Button onClick={() => navigate('/challenges/new')}>
                    <Plus className="w-4 h-4 mr-2" />
                    Add Challenge
                </Button>
            </div>

            <div className="mb-4">
                <input
                    type="text"
                    placeholder={isLoading ? 'Loading...' : 'Search by track name...'}
                    value={search}
                    onChange={(e) => setSearch(e.target.value)}
                    disabled={isLoading}
                    className="w-full px-4 py-2 rounded-lg border border-gray-300 dark:border-gray-600 bg-white dark:bg-gray-800 text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-primary-500 disabled:opacity-50"
                />
            </div>

            <Card>
                <Table<ChallengeSummaryResponse>
                    columns={[
                        { key: 'trackName', label: 'Track' },
                        { key: 'trackCountry', label: 'Country' },
                        { key: 'carBrand', label: 'Brand' },
                        { key: 'carName', label: 'Car' },
                        { key: 'challengeEndDate', label: 'End Date', render: (v) => formatDate(v) },
                        {
                            key: 'bestLapTime',
                            label: 'Best Lap',
                            render: (v) => formatDurationJson(v),
                        },
                        { key: 'bestParticipantName', label: 'Best Driver' },
                    ]}
                    rows={rows}
                    onRowClick={(row) => navigate(`/challenges/${row.challengeId}`)}
                    emptyMessage={isLoading ? 'Loading...' : 'No challenges found'}
                />
            </Card>
        </div>
    )
}