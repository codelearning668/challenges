import { Link } from 'react-router-dom'
import { useQueries, useQuery } from '@tanstack/react-query'
import { Flag, Clock, ArrowRight, Users, Timer, TrendingUp } from 'lucide-react'
import { challengeApi } from '@/services/api'
import { Card } from '@/components/shared/Card'
import {
    formatDate,
    formatGap,
    isChallengeActive,
    parseLapTime,
} from '@/utils/format'
import { useAuthStore } from '@/stores/useAuthStore'
import type {
    ChallengeDetailResponse,
    ChallengeSummaryResponse,
} from '@/types/challenge'

interface StatProps {
    icon: React.ElementType
    label: string
    value: React.ReactNode
    tone: 'blue' | 'orange'
}

const TONE: Record<StatProps['tone'], string> = {
    blue: 'bg-blue-50 text-blue-600 dark:bg-blue-900/20 dark:text-blue-400',
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

interface MyStats {
    position: number | null
    totalRacers: number
    myLap: string | null
    gapToLeader: number | null
    interval: number | null
}

function computeMyStats(challenge: ChallengeDetailResponse, username: string): MyStats {
    const withTimes = [...(challenge.participants ?? [])]
        .filter((p) => p.participantBestLapTime != null)
        .sort(
            (a, b) =>
                parseLapTime(a.participantBestLapTime!) -
                parseLapTime(b.participantBestLapTime!),
        )

    const myIndex = withTimes.findIndex((p) => p.participantName === username)

    if (myIndex === -1) {
        return {
            position: null,
            totalRacers: (challenge.participants ?? []).length,
            myLap: null,
            gapToLeader: null,
            interval: null,
        }
    }

    const me = withTimes[myIndex]
    const leader = withTimes[0]
    const ahead = myIndex > 0 ? withTimes[myIndex - 1] : null

    const mySec = parseLapTime(me.participantBestLapTime!)
    const leaderSec = leader?.participantBestLapTime
        ? parseLapTime(leader.participantBestLapTime)
        : null
    const aheadSec = ahead?.participantBestLapTime
        ? parseLapTime(ahead.participantBestLapTime)
        : null

    return {
        position: myIndex,
        totalRacers: (challenge.participants ?? []).length,
        myLap: me.participantBestLapTime,
        gapToLeader: myIndex > 0 && leaderSec != null ? mySec - leaderSec : null,
        interval: myIndex > 0 && aheadSec != null ? mySec - aheadSec : null,
    }
}

const POSITION_STYLES = [
    { ring: 'bg-yellow-50 text-yellow-700 dark:bg-yellow-900/30 dark:text-yellow-300' },
    { ring: 'bg-gray-100 text-gray-700 dark:bg-gray-700 dark:text-gray-200' },
    { ring: 'bg-amber-50 text-amber-800 dark:bg-amber-900/30 dark:text-amber-300' },
]

function PositionBadge({ position }: { position: number | null }) {
    if (position === null) {
        return (
            <div className="w-14 h-14 rounded-xl bg-gray-100 dark:bg-gray-700 flex items-center justify-center">
        <span className="text-[10px] font-semibold uppercase text-gray-400 dark:text-gray-500 text-center leading-tight">
          No
          <br />
          lap
        </span>
            </div>
        )
    }

    const medal = POSITION_STYLES[position]
    const ordinal = position + 1

    if (medal) {
        return (
            <div className={`w-14 h-14 rounded-xl flex flex-col items-center justify-center ${medal.ring}`}>
                <span className="text-lg font-extrabold leading-none">{ordinal}</span>
                <span className="text-[9px] font-semibold uppercase tracking-wide mt-0.5">place</span>
            </div>
        )
    }

    return (
        <div className="w-14 h-14 rounded-xl bg-gray-100 dark:bg-gray-700 flex flex-col items-center justify-center">
      <span className="text-lg font-extrabold text-gray-700 dark:text-gray-200 leading-none">
        {ordinal}
      </span>
            <span className="text-[9px] font-semibold uppercase tracking-wide text-gray-500 dark:text-gray-400 mt-0.5">
        place
      </span>
        </div>
    )
}

function CardStat({
                      icon: Icon,
                      label,
                      value,
                      emphasize = false,
                      muted = false,
                  }: {
    icon: React.ElementType
    label: string
    value: string
    emphasize?: boolean
    muted?: boolean
}) {
    return (
        <div className="flex-1 min-w-0">
            <div className="flex items-center gap-1.5 text-gray-500 dark:text-gray-400">
                <Icon className="w-3.5 h-3.5" />
                <span className="text-[11px] font-medium uppercase tracking-wide">{label}</span>
            </div>
            <p
                className={
                    'mt-1.5 font-mono tabular-nums truncate ' +
                    (emphasize
                        ? 'text-2xl font-bold text-gray-900 dark:text-gray-100'
                        : muted
                            ? 'text-lg font-medium text-gray-400 dark:text-gray-500'
                            : 'text-lg font-semibold text-gray-900 dark:text-gray-100')
                }
            >
                {value}
            </p>
        </div>
    )
}

function ChallengeCard({
                           challenge,
                           username,
                       }: {
    challenge: ChallengeDetailResponse
    username: string
}) {
    const stats = computeMyStats(challenge, username)
    const active = isChallengeActive(challenge.challengeEndDate)
    const isLeader = stats.position === 0

    const subtitle = [challenge.carBrand, challenge.carName, challenge.trackCountry]
        .filter(Boolean)
        .join(' · ')

    return (
        <Link
            to={`/challenges/${challenge.challengeId}`}
            className="group block bg-white dark:bg-gray-800 rounded-2xl shadow-sm border border-gray-200 dark:border-gray-700 overflow-hidden hover:shadow-lg hover:border-primary-300 dark:hover:border-primary-700 transition-all"
        >
            <div className="flex items-start gap-4 px-5 pt-5">
                <PositionBadge position={stats.position} />
                <div className="flex-1 min-w-0 pt-1">
                    <div className="flex items-center gap-2 mb-1">
            <span
                className={
                    'inline-flex items-center gap-1.5 px-2 py-0.5 rounded-full text-[10px] font-semibold uppercase tracking-wide ' +
                    (active
                        ? 'bg-green-100 text-green-700 dark:bg-green-900/40 dark:text-green-300'
                        : 'bg-gray-100 text-gray-600 dark:bg-gray-700 dark:text-gray-300')
                }
            >
              <span
                  className={
                      'w-1.5 h-1.5 rounded-full ' + (active ? 'bg-green-500' : 'bg-gray-400')
                  }
              />
                {active ? 'Active' : 'Closed'}
            </span>
                        <span className="inline-flex items-center gap-1 text-[11px] text-gray-500 dark:text-gray-400">
              <Users className="w-3 h-3" />
                            {stats.totalRacers}
            </span>
                    </div>
                    <h3 className="text-lg font-bold text-gray-900 dark:text-gray-100 truncate">
                        {challenge.trackName}
                    </h3>
                    <p className="text-xs text-gray-500 dark:text-gray-400 truncate mt-0.5">
                        {subtitle}
                    </p>
                </div>
            </div>

            <div className="flex items-end gap-6 px-5 py-5">
                <CardStat icon={Timer} label="Your best lap" value={stats.myLap ?? '—'} emphasize />
                <CardStat
                    icon={TrendingUp}
                    label="Leader"
                    value={isLeader ? 'Leader' : formatGap(stats.gapToLeader)}
                    muted={isLeader}
                />
                <CardStat
                    icon={TrendingUp}
                    label="Interval"
                    value={isLeader ? 'Leader' : formatGap(stats.interval)}
                    muted={isLeader}
                />
            </div>

            <div className="flex items-center justify-between px-5 py-3 bg-gray-50 dark:bg-gray-800/60 border-t border-gray-200 dark:border-gray-700">
        <span className="text-xs text-gray-500 dark:text-gray-400">
          Ends {formatDate(challenge.challengeEndDate)}
        </span>
                <span className="text-xs font-semibold text-primary-600 dark:text-primary-400 inline-flex items-center gap-1 group-hover:gap-2 transition-all">
          View details
          <ArrowRight className="w-3.5 h-3.5" />
        </span>
            </div>
        </Link>
    )
}

export function ParticipantDashboard() {
    const user = useAuthStore((s) => s.user)

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

    const joinedCount = myChallenges.length

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

            <div className="grid grid-cols-1 md:grid-cols-2 gap-4 mb-6">
                <Stat icon={Flag} label="Challenges joined" value={joinedCount} tone="blue" />
                <Stat icon={Clock} label="Ending in 7 days" value={endingSoon} tone="orange" />
            </div>

            <Card>
                <div className="flex items-center justify-between mb-5">
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
                ) : myChallenges.length === 0 ? (
                    <p className="text-center text-gray-500 dark:text-gray-400 py-8">
                        You haven't joined any challenges yet. Head over to the{' '}
                        <Link
                            to="/challenges"
                            className="text-primary-600 hover:text-primary-700 dark:text-primary-400 font-medium"
                        >
                            Challenges
                        </Link>{' '}
                        page to find one.
                    </p>
                ) : (
                    <div className="grid grid-cols-1 xl:grid-cols-2 gap-4">
                        {myChallenges.map((c) => (
                            <ChallengeCard
                                key={c.challengeId}
                                challenge={c}
                                username={user!.username}
                            />
                        ))}
                    </div>
                )}
            </Card>
        </div>
    )
}