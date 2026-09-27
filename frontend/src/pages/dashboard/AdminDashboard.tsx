import { useMemo, useState } from 'react'
import { useQueries, useQuery } from '@tanstack/react-query'
import { Flag, Clock, Users, Loader2, Search } from 'lucide-react'
import { challengeApi } from '@/services/api'
import { Card } from '@/components/shared/Card'
import { AdminChallengePanel } from '@/components/dashboard/AdminChallengePanel'
import { isChallengeActive } from '@/utils/format'
import { useDebouncedValue } from '@/hooks/useDebouncedValue'
import type {
    ChallengeDetailResponse,
    ChallengeSummaryResponse,
} from '@/types/challenge'

type StatusFilter = 'all' | 'active' | 'closed'

// ---------- stat tile ----------

interface StatTileProps {
    icon: React.ElementType
    label: string
    value: React.ReactNode
    tone: 'blue' | 'orange' | 'green'
}

const TONE: Record<StatTileProps['tone'], string> = {
    blue: 'bg-blue-50 text-blue-600 dark:bg-blue-900/20 dark:text-blue-400',
    orange: 'bg-orange-50 text-orange-600 dark:bg-orange-900/20 dark:text-orange-400',
    green: 'bg-green-50 text-green-600 dark:bg-green-900/20 dark:text-green-400',
}

function StatTile({ icon: Icon, label, value, tone }: StatTileProps) {
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

// ---------- status pill group ----------

function StatusFilterPills({
                               value,
                               onChange,
                               counts,
                           }: {
    value: StatusFilter
    onChange: (v: StatusFilter) => void
    counts: Record<StatusFilter, number>
}) {
    const options: { key: StatusFilter; label: string }[] = [
        { key: 'active', label: 'Active' },
        { key: 'closed', label: 'Closed' },
        { key: 'all', label: 'All' },
    ]

    return (
        <div className="inline-flex items-center rounded-lg border border-gray-300 dark:border-gray-600 p-0.5 bg-white dark:bg-gray-800">
            {options.map((opt) => {
                const selected = value === opt.key
                return (
                    <button
                        key={opt.key}
                        type="button"
                        onClick={() => onChange(opt.key)}
                        className={
                            'px-3 py-1.5 text-xs font-medium rounded-md transition-colors ' +
                            (selected
                                ? 'bg-primary-600 text-white'
                                : 'text-gray-600 dark:text-gray-300 hover:bg-gray-100 dark:hover:bg-gray-700')
                        }
                    >
                        {opt.label}
                        <span
                            className={
                                'ml-1.5 text-[10px] font-semibold px-1.5 py-0.5 rounded ' +
                                (selected
                                    ? 'bg-white/20 text-white'
                                    : 'bg-gray-100 dark:bg-gray-700 text-gray-500 dark:text-gray-400')
                            }
                        >
              {counts[opt.key]}
            </span>
                    </button>
                )
            })}
        </div>
    )
}

// ---------- main component ----------

export function AdminDashboard() {
    const { data: listData, isLoading: listLoading } = useQuery({
        queryKey: ['challenges'],
        queryFn: () => challengeApi.search(),
        refetchOnMount: 'always',
    })

    const summaries: ChallengeSummaryResponse[] = listData?.data ?? []

    const detailQueries = useQueries({
        queries: summaries.map((s) => ({
            queryKey: ['challenge', s.challengeId] as const,
            queryFn: () => challengeApi.get(s.challengeId),
            refetchOnMount: 'always' as const,
        })),
    })

    const detailsLoading = detailQueries.some((q) => q.isLoading)
    const isLoading = listLoading || detailsLoading

    const challenges: ChallengeDetailResponse[] = useMemo(
        () =>
            detailQueries
                .map((q) => q.data?.data)
                .filter((c): c is ChallengeDetailResponse => !!c),
        [detailQueries],
    )

    // ---------- stats ----------

    const stats = useMemo(() => {
        const now = Date.now()
        const weekMs = 7 * 24 * 60 * 60 * 1000

        let active = 0
        let closed = 0
        let endingSoon = 0
        let totalRacers = 0

        for (const c of challenges) {
            const isActive = isChallengeActive(c.challengeEndDate)
            if (isActive) {
                active += 1
                const t = new Date(c.challengeEndDate).getTime()
                if (t >= now && t <= now + weekMs) endingSoon += 1
                totalRacers += (c.participants ?? []).length
            } else {
                closed += 1
            }
        }
        return { active, closed, endingSoon, totalRacers }
    }, [challenges])

    // ---------- filter & search ----------

    const [statusFilter, setStatusFilter] = useState<StatusFilter>('active')
    const [search, setSearch] = useState('')
    const dSearch = useDebouncedValue(search, 200)

    const filtered = useMemo(() => {
        let list = challenges

        if (statusFilter === 'active') {
            list = list.filter((c) => isChallengeActive(c.challengeEndDate))
        } else if (statusFilter === 'closed') {
            list = list.filter((c) => !isChallengeActive(c.challengeEndDate))
        }

        const q = dSearch.trim().toLowerCase()
        if (q) {
            list = list.filter(
                (c) =>
                    c.trackName.toLowerCase().includes(q) ||
                    c.carBrand.toLowerCase().includes(q) ||
                    c.carName.toLowerCase().includes(q),
            )
        }

        // Active first, then by soonest end date.
        return [...list].sort((a, b) => {
            const aActive = isChallengeActive(a.challengeEndDate)
            const bActive = isChallengeActive(b.challengeEndDate)
            if (aActive !== bActive) return aActive ? -1 : 1
            return (
                new Date(a.challengeEndDate).getTime() -
                new Date(b.challengeEndDate).getTime()
            )
        })
    }, [challenges, statusFilter, dSearch])

    const counts: Record<StatusFilter, number> = {
        active: stats.active,
        closed: stats.closed,
        all: challenges.length,
    }

    return (
        <div>
            <div className="mb-6">
                <h1 className="text-2xl font-bold text-gray-900 dark:text-gray-100">
                    Admin dashboard
                </h1>
                <p className="text-sm text-gray-500 dark:text-gray-400 mt-1">
                    Manage challenges and record participant lap times
                </p>
            </div>

            {/* Stat tiles */}
            <div className="grid grid-cols-1 sm:grid-cols-3 gap-4 mb-6">
                <StatTile
                    icon={Flag}
                    label="Active challenges"
                    value={stats.active}
                    tone="blue"
                />
                <StatTile
                    icon={Clock}
                    label="Ending in 7 days"
                    value={stats.endingSoon}
                    tone="orange"
                />
                <StatTile
                    icon={Users}
                    label="Racers competing"
                    value={stats.totalRacers}
                    tone="green"
                />
            </div>

            {/* Toolbar */}
            <Card className="mb-4">
                <div className="flex flex-col sm:flex-row sm:items-center gap-3 sm:justify-between">
                    <StatusFilterPills
                        value={statusFilter}
                        onChange={setStatusFilter}
                        counts={counts}
                    />

                    <div className="relative w-full sm:max-w-xs">
                        <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-gray-400 pointer-events-none" />
                        <input
                            type="text"
                            placeholder="Search track, brand or car..."
                            value={search}
                            onChange={(e) => setSearch(e.target.value)}
                            className="w-full pl-9 pr-3 py-2 rounded-lg border border-gray-300 dark:border-gray-600 bg-white dark:bg-gray-800 text-gray-900 dark:text-gray-100 text-sm focus:outline-none focus:ring-2 focus:ring-primary-500"
                        />
                        {listLoading && search.trim().length > 0 && (
                            <Loader2 className="absolute right-3 top-1/2 -translate-y-1/2 w-4 h-4 text-gray-400 animate-spin" />
                        )}
                    </div>
                </div>
            </Card>

            {/* Challenge list */}
            {isLoading ? (
                <Card>
                    <p className="text-center text-gray-500 dark:text-gray-400 py-10">
                        Loading challenges…
                    </p>
                </Card>
            ) : filtered.length === 0 ? (
                <Card>
                    <p className="text-center text-gray-500 dark:text-gray-400 py-10">
                        {challenges.length === 0
                            ? 'No challenges exist yet.'
                            : statusFilter === 'active'
                                ? 'No active challenges right now.'
                                : 'No challenges match the current filter.'}
                    </p>
                </Card>
            ) : (
                <div className="space-y-3">
                    {filtered.map((c) => (
                        <AdminChallengePanel key={c.challengeId} challenge={c} />
                    ))}
                </div>
            )}
        </div>
    )
}