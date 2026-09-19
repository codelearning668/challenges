import { Link } from 'react-router-dom'
import { useQueries, useQuery } from '@tanstack/react-query'
import { Flag, Timer, Clock, ArrowRight } from 'lucide-react'
import { challengeApi } from '@/services/api'
import { Card } from '@/components/shared/Card'
import { Table } from '@/components/shared/Table'
import {
    durationToSeconds,
    formatDate,
    formatDurationJson,
    formatLapTime,
} from '@/utils/format'
import { useAuthStore } from '@/stores/useAuthStore'
import type { ChallengeDetailResponse, ChallengeSummaryResponse } from '@/types/challenge'

interface StatProps {
    icon: React.ElementType
    label: string
    value: React.ReactNode
    tone: 'blue' | 'green' | 'orange'
}

const TONE: Record<StatProps['tone'], string> = {
    blue: 'bg-blue-50 text-blue-600 dark:bg-blue-900/20 dark:text-blue-400',
    green: 'bg-green-50 text-green-600 dark:bg-green-900/20 dark:text-green-400',
    orange: 'bg-orange-50 text-orange-600 dark:bg-orange-900/20 dark:text-orange-400',
}

function Stat({ icon: Icon, label, value, tone }: StatProps) {
    return (
        <div className="bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 p-4 flex items-center gap-4">
            <div className={`w-11 h-11 rounded-lg flex items-center justify-center ${TONE[tone]}`}>
                <Icon className="w-5 h-5" />
            </div>
            <div className="min-w-0">
                <p className="text-xs font-medium text-gray-500 dark:text-gray-400">{label}</p>
                <p className="text-xl font-bold text-gray-900 dark:text-gray-100 mt-0.5 truncate">
                    {value}
                </p>
            </div>
        </div>
    )
}

export function Dashboard() {
    const user = useAuthStore((s) => s.user)

    // 1) fetch the challenge list
    const { data: listData, isLoading: listLoading } = useQuery({
        queryKey: ['challenges'],
        queryFn: () => challengeApi.search(),
        refetchOnMount: 'always',
    })

    const summaries: ChallengeSummaryResponse[] = listData?.data ?? []

    // 2) fetch details in parallel to know who's in each challenge
    const detailQueries = useQueries({
        queries: summaries.map((s) => ({
            queryKey: ['challenge', s.challengeId] as const,
            queryFn: () => challengeApi.get(s.challengeId),
            refetchOnMount: 'always' as const,
        })),
    })

    const detailsLoading = detailQueries.some((q) => q.isLoading)
    const isLoading = listLoading || detailsLoading

    // 3) keep only challenges where the current user appears in participants
    const myChallenges: ChallengeDetailResponse[] = detailQueries
        .map((q) => q.data?.data)
        .filter((c): c is ChallengeDetailResponse => {
            if (!c || !user) return false
            return (c.participants ?? []).some((p) => p.participantName === user.username)
        })
        .sort(
            (a, b) =>
                new Date(a.challengeEndDate).getTime() - new Date(b.challengeEndDate).getTime(),
        )

    // 4) stats
    const joinedCount = myChallenges.length

    const myBestSeconds = myChallenges
        .flatMap((c) => c.participants ?? [])
        .filter((p) => p.participantName === user?.username)
        .map((p) => durationToSeconds(p.participantBestLapTime))
        .filter((s): s is number => s != null)
        .reduce<number | null>((min, s) => (min == null || s < min ? s : min), null)

    const now = Date.now()
    const weekMs = 7 * 24 * 60 * 60 * 1000
    const endingSoon = myChallenges.filter((c) => {
        const t = new Date(c.challengeEndDate).getTime()
        return t >= now && t <= now + weekMs
    }).length

    return (
        <div>
            <div className="mb-6">
                <h1 className="text-2xl font-bold text-gray-900 dark:text-gray-100">
                    Welcome back{user ? `, ${user.username}` : ''}
                </h1>
                <p className="text-sm text-gray-500 dark:text-gray-400 mt-1">
                    Your personal racing dashboard
                </p>
            </div>

            <div className="grid grid-cols-1 md:grid-cols-3 gap-4 mb-6">
                <Stat icon={Flag} label="Challenges joined" value={joinedCount} tone="blue" />
                <Stat
                    icon={Timer}
                    label="My best lap"
                    value={myBestSeconds != null ? formatLapTime(myBestSeconds) : '—'}
                    tone="green"
                />
                <Stat icon={Clock} label="Ending in 7 days" value={endingSoon} tone="orange" />
            </div>

            <Card>
                <div className="flex items-center justify-between mb-4">
                    <h2 className="text-lg font-semibold text-gray-900 dark:text-gray-100">
                        Your challenges
                    </h2>
                    <Link
                        to="/challenges"
                        className="text-sm font-medium text-primary-600 hover:text-primary-700 dark:text-primary-400 inline-flex items-center gap-1"
                    >
                        Browse all
                        <ArrowRight className="w-4 h-4" />
                    </Link>
                </div>

                {isLoading ? (
                    <p className="text-center text-gray-500 dark:text-gray-400 py-8">
                        Loading your challenges…
                    </p>
                ) : (
                    <Table<ChallengeDetailResponse>
                        columns={[
                            { key: 'trackName', label: 'Track' },
                            { key: 'trackCountry', label: 'Country' },
                            { key: 'carName', label: 'Car', render: (_, c) => `${c.carBrand} ${c.carName}` },
                            {
                                key: 'challengeEndDate',
                                label: 'End Date',
                                render: (v) => formatDate(v),
                            },
                            {
                                key: 'bestLapTime',
                                label: 'Best Lap',
                                render: (v) => formatDurationJson(v),
                            },
                            { key: 'bestParticipantName', label: 'Best Driver' },
                        ]}
                        rows={myChallenges}
                        emptyMessage="You haven't joined any challenges yet. Head over to the Challenges page to find one."
                    />
                )}
            </Card>
        </div>
    )
}