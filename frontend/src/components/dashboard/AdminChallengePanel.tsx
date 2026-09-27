import { useMemo, useState } from 'react'
import { Link } from 'react-router-dom'
import { useMutation, useQueryClient } from '@tanstack/react-query'
import {
    ChevronDown,
    ChevronRight,
    Check,
    X,
    Pencil,
    Trash2,
    Users,
    ArrowRight,
    CalendarClock,
} from 'lucide-react'
import { challengeApi } from '@/services/api'
import { useConfirm } from '@/components/shared/ConfirmProvider'
import { DatePicker } from '@/components/shared/DatePicker'
import { RankBadge } from '@/components/shared/RankBadge'
import {
    formatDate,
    formatDurationJson,
    formatLapTime,
    isChallengeActive,
    parseLapTime,
} from '@/utils/format'
import { toast } from '@/stores/useToastStore'
import type {
    ChallengeDetailResponse,
    ParticipantDetailResponse,
} from '@/types/challenge'

const inputCls =
    'w-full px-2 py-1 rounded-md border border-gray-300 dark:border-gray-600 bg-white dark:bg-gray-800 text-gray-900 dark:text-gray-100 text-sm focus:outline-none focus:ring-2 focus:ring-primary-500'

interface Props {
    challenge: ChallengeDetailResponse
}

export function AdminChallengePanel({ challenge }: Props) {
    const queryClient = useQueryClient()
    const confirm = useConfirm()

    const [expanded, setExpanded] = useState(false)

    // Inline lap-time edit
    const [editingName, setEditingName] = useState<string | null>(null)
    const [draft, setDraft] = useState('')

    // Inline end-date edit
    const [editEndDate, setEditEndDate] = useState(false)
    const [draftEndDate, setDraftEndDate] = useState('')

    const active = isChallengeActive(challenge.challengeEndDate)
    const participants = challenge.participants ?? []

    const sorted = useMemo<ParticipantDetailResponse[]>(() => {
        return [...participants].sort((a, b) => {
            const sa = a.participantBestLapTime ? parseLapTime(a.participantBestLapTime) : null
            const sb = b.participantBestLapTime ? parseLapTime(b.participantBestLapTime) : null
            if (sa == null && sb == null) return a.participantName.localeCompare(b.participantName)
            if (sa == null) return 1
            if (sb == null) return -1
            return sa - sb
        })
    }, [participants])

    // ---------- mutations ----------

    const invalidate = async () => {
        await Promise.all([
            queryClient.invalidateQueries({ queryKey: ['challenge', challenge.challengeId] }),
            queryClient.invalidateQueries({ queryKey: ['challenges'] }),
        ])
    }

    const updateLapMutation = useMutation({
        mutationFn: (vars: { name: string; lap: string | null }) =>
            challengeApi.updateLapTime(challenge.challengeId, {
                participantName: vars.name,
                newLapTime: vars.lap,
            }),
        onSuccess: async () => {
            await invalidate()
            toast.success('Lap time updated')
            setEditingName(null)
            setDraft('')
        },
    })

    const updateEndDateMutation = useMutation({
        mutationFn: (endDate: string) =>
            challengeApi.updateEndDate(challenge.challengeId, { endDate }),
        onSuccess: async () => {
            await invalidate()
            toast.success('End date updated')
            setEditEndDate(false)
            setDraftEndDate('')
        },
    })

    // ---------- lap-time handlers ----------

    const beginEdit = (p: ParticipantDetailResponse) => {
        setEditingName(p.participantName)
        setDraft('')
    }

    const cancel = () => {
        setEditingName(null)
        setDraft('')
    }

    const save = (name: string) => {
        const secs = parseLapTime(draft)
        if (!Number.isFinite(secs) || secs <= 0) {
            toast.error('Enter a lap time like 1:22.555')
            return
        }
        updateLapMutation.mutate({ name, lap: formatLapTime(secs) })
    }

    const clear = async (name: string) => {
        const ok = await confirm({
            title: 'Clear lap time',
            message: `Clear ${name}'s lap time? They will stay registered for the challenge.`,
            confirmLabel: 'Clear',
            variant: 'danger',
        })
        if (ok) updateLapMutation.mutate({ name, lap: null })
    }

    // ---------- end-date handlers ----------

    const beginEditEndDate = () => {
        setDraftEndDate(challenge.challengeEndDate)
        setEditEndDate(true)
    }

    const cancelEditEndDate = () => {
        setEditEndDate(false)
        setDraftEndDate('')
    }

    const saveEndDate = () => {
        if (!draftEndDate) {
            toast.warning('Pick a date first')
            return
        }
        const today = new Date()
        today.setHours(0, 0, 0, 0)
        if (new Date(`${draftEndDate}T00:00:00`).getTime() < today.getTime()) {
            toast.error('End date cannot be in the past')
            return
        }
        updateEndDateMutation.mutate(draftEndDate)
    }

    const subtitle = [challenge.carBrand, challenge.carName, challenge.trackCountry]
        .filter(Boolean)
        .join(' · ')

    return (
        <div className="bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 overflow-hidden">
            {editEndDate ? (
                /* ---------- Edit end-date header ---------- */
                <div className="flex flex-wrap items-center gap-3 px-5 py-4 bg-primary-50/40 dark:bg-primary-900/20">
                    <div className="flex items-center gap-2 shrink-0">
                        <CalendarClock className="w-4 h-4 text-primary-600 dark:text-primary-400" />
                        <span className="text-sm font-semibold text-gray-900 dark:text-gray-100">
              Update end date
            </span>
                    </div>

                    <div className="flex-1 min-w-[200px] max-w-xs">
                        <DatePicker
                            value={draftEndDate}
                            onChange={setDraftEndDate}
                            placeholder="Pick a date"
                        />
                    </div>

                    <div className="flex items-center gap-1.5">
                        <button
                            type="button"
                            onClick={saveEndDate}
                            disabled={updateEndDateMutation.isPending}
                            className="p-1.5 rounded-lg hover:bg-green-100 dark:hover:bg-green-900/30 disabled:opacity-50"
                            aria-label="Save end date"
                        >
                            <Check className="w-4 h-4 text-green-600 dark:text-green-400" />
                        </button>
                        <button
                            type="button"
                            onClick={cancelEditEndDate}
                            className="p-1.5 rounded-lg hover:bg-gray-100 dark:hover:bg-gray-700"
                            aria-label="Cancel"
                        >
                            <X className="w-4 h-4 text-gray-500" />
                        </button>
                    </div>

                    <div className="flex-1 hidden sm:block" />
                    <p className="text-xs text-gray-500 dark:text-gray-400 hidden md:block">
                        Currently ends {formatDate(challenge.challengeEndDate)}
                    </p>
                </div>
            ) : (
                /* ---------- Normal header ---------- */
                <div className="flex items-center gap-4 px-5 py-4 hover:bg-gray-50 dark:hover:bg-gray-700/40 transition-colors">
                    <button
                        type="button"
                        onClick={() => setExpanded((v) => !v)}
                        aria-expanded={expanded}
                        className="flex items-center gap-4 flex-1 min-w-0 text-left"
                    >
            <span className="shrink-0 text-gray-400">
              {expanded ? (
                  <ChevronDown className="w-5 h-5" />
              ) : (
                  <ChevronRight className="w-5 h-5" />
              )}
            </span>

                        <div className="flex-1 min-w-0">
                            <div className="flex items-center gap-2 flex-wrap mb-1">
                                <h3 className="text-base font-bold text-gray-900 dark:text-gray-100 truncate">
                                    {challenge.trackName}
                                </h3>
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
                          'w-1.5 h-1.5 rounded-full ' +
                          (active ? 'bg-green-500' : 'bg-gray-400')
                      }
                  />
                                    {active ? 'Active' : 'Closed'}
                </span>
                            </div>
                            <p className="text-xs text-gray-500 dark:text-gray-400 truncate">
                                {subtitle}
                            </p>
                        </div>

                        <div className="hidden sm:flex items-center gap-6 shrink-0">
                            <div className="text-right">
                                <p className="text-[10px] font-medium uppercase tracking-wide text-gray-400">
                                    Ends
                                </p>
                                <p className="text-xs font-semibold text-gray-700 dark:text-gray-200">
                                    {formatDate(challenge.challengeEndDate)}
                                </p>
                            </div>
                            <div className="text-right">
                                <p className="text-[10px] font-medium uppercase tracking-wide text-gray-400">
                                    Racers
                                </p>
                                <p className="text-xs font-semibold text-gray-700 dark:text-gray-200 inline-flex items-center gap-1">
                                    <Users className="w-3 h-3" />
                                    {participants.length}
                                </p>
                            </div>
                            <div className="text-right">
                                <p className="text-[10px] font-medium uppercase tracking-wide text-gray-400">
                                    Best
                                </p>
                                <p className="text-xs font-semibold font-mono text-gray-700 dark:text-gray-200">
                                    {formatDurationJson(challenge.bestLapTime)}
                                </p>
                            </div>
                        </div>
                    </button>

                    {/* End-date edit trigger — admin only, active challenges only */}
                    {active && (
                        <button
                            type="button"
                            onClick={beginEditEndDate}
                            className="shrink-0 p-2 rounded-lg text-gray-500 hover:bg-gray-100 dark:hover:bg-gray-700 dark:text-gray-400 transition-colors"
                            aria-label="Update end date"
                            title="Update end date"
                        >
                            <Pencil className="w-4 h-4" />
                        </button>
                    )}
                </div>
            )}

            {/* ---------- Expanded participants ---------- */}
            {expanded && !editEndDate && (
                <div className="border-t border-gray-200 dark:border-gray-700 bg-gray-50/50 dark:bg-gray-800/50">
                    {participants.length === 0 ? (
                        <p className="text-center text-sm text-gray-500 dark:text-gray-400 py-6">
                            No participants yet.
                        </p>
                    ) : (
                        <div className="overflow-x-auto">
                            <table className="w-full">
                                <thead className="bg-white dark:bg-gray-800">
                                <tr>
                                    <th className="px-4 py-2 text-left text-[10px] font-semibold uppercase tracking-wider text-gray-500 dark:text-gray-400 w-12">
                                        #
                                    </th>
                                    <th className="px-4 py-2 text-left text-[10px] font-semibold uppercase tracking-wider text-gray-500 dark:text-gray-400">
                                        Participant
                                    </th>
                                    <th className="px-4 py-2 text-left text-[10px] font-semibold uppercase tracking-wider text-gray-500 dark:text-gray-400">
                                        Best Lap
                                    </th>
                                    <th className="px-4 py-2 text-right text-[10px] font-semibold uppercase tracking-wider text-gray-500 dark:text-gray-400 w-24">
                                        Actions
                                    </th>
                                </tr>
                                </thead>
                                <tbody className="divide-y divide-gray-100 dark:divide-gray-700">
                                {sorted.map((p, i) => {
                                    const isEditing = editingName === p.participantName
                                    return (
                                        <tr
                                            key={p.participantId}
                                            className="hover:bg-gray-50 dark:hover:bg-gray-700/30"
                                        >
                                            <td className="px-4 py-2.5 text-sm">
                                                <RankBadge index={i} />
                                            </td>
                                            <td className="px-4 py-2.5 text-sm font-medium text-gray-900 dark:text-gray-100">
                                                {p.participantName}
                                            </td>
                                            <td className="px-4 py-2.5 text-sm font-mono text-gray-700 dark:text-gray-200">
                                                {isEditing ? (
                                                    <input
                                                        type="text"
                                                        autoFocus
                                                        value={draft}
                                                        onChange={(e) => setDraft(e.target.value)}
                                                        placeholder="1:22.555"
                                                        className={inputCls + ' max-w-[140px]'}
                                                    />
                                                ) : (
                                                    formatDurationJson(p.participantBestLapTime)
                                                )}
                                            </td>
                                            <td className="px-4 py-2.5 text-right">
                                                {isEditing ? (
                                                    <div className="flex items-center justify-end gap-1">
                                                        <button
                                                            type="button"
                                                            onClick={() => save(p.participantName)}
                                                            disabled={updateLapMutation.isPending}
                                                            className="p-1.5 rounded-lg hover:bg-green-50 dark:hover:bg-green-900/20 disabled:opacity-50"
                                                            aria-label="Save"
                                                        >
                                                            <Check className="w-4 h-4 text-green-600 dark:text-green-400" />
                                                        </button>
                                                        <button
                                                            type="button"
                                                            onClick={cancel}
                                                            className="p-1.5 rounded-lg hover:bg-gray-100 dark:hover:bg-gray-700"
                                                            aria-label="Cancel"
                                                        >
                                                            <X className="w-4 h-4 text-gray-500" />
                                                        </button>
                                                    </div>
                                                ) : (
                                                    <div className="flex items-center justify-end gap-1">
                                                        <button
                                                            type="button"
                                                            onClick={() => beginEdit(p)}
                                                            className="p-1.5 rounded-lg hover:bg-gray-100 dark:hover:bg-gray-700"
                                                            aria-label="Edit lap time"
                                                            title="Edit lap time"
                                                        >
                                                            <Pencil className="w-4 h-4 text-gray-500" />
                                                        </button>
                                                        {p.participantBestLapTime != null && (
                                                            <button
                                                                type="button"
                                                                onClick={() => clear(p.participantName)}
                                                                className="p-1.5 rounded-lg hover:bg-red-50 dark:hover:bg-red-900/20"
                                                                aria-label="Clear lap time"
                                                                title="Clear lap time"
                                                            >
                                                                <Trash2 className="w-4 h-4 text-red-500" />
                                                            </button>
                                                        )}
                                                    </div>
                                                )}
                                            </td>
                                        </tr>
                                    )
                                })}
                                </tbody>
                            </table>
                        </div>
                    )}
                    <div className="flex items-center justify-end px-5 py-3 bg-white dark:bg-gray-800 border-t border-gray-200 dark:border-gray-700">
                        <Link
                            to={`/challenges/${challenge.challengeId}`}
                            className="text-xs font-semibold text-primary-600 dark:text-primary-400 inline-flex items-center gap-1 hover:gap-2 transition-all"
                        >
                            Open full challenge
                            <ArrowRight className="w-3.5 h-3.5" />
                        </Link>
                    </div>
                </div>
            )}
        </div>
    )
}