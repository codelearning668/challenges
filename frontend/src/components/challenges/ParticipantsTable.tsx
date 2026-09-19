import { Check, Pencil, Trash2, X } from 'lucide-react'
import { Table } from '@/components/shared/Table'
import { RankBadge } from '@/components/shared/RankBadge'
import {
    durationToSeconds,
    formatDurationJson,
    formatGap,
} from '@/utils/format'
import type { ParticipantDetailResponse } from '@/types/challenge'

interface ParticipantsTableProps {
    /** Already sorted by best lap; the table renders as-is. */
    participants: ParticipantDetailResponse[]
    isAdmin: boolean
    editingParticipant: string | null
    draftLapTime: string
    onDraftLapTimeChange: (value: string) => void
    onBeginEditLapTime: (participant: ParticipantDetailResponse) => void
    onCancelEditLapTime: () => void
    onSaveLapTime: (participantName: string) => void
    onClearLapTime: (participantName: string) => void
    isSavingLapTime: boolean
}

const inlineInputCls =
    'w-full px-2 py-1 rounded-md border border-gray-300 dark:border-gray-600 bg-white bg-gray-800 text-gray-900 dark:text-gray-100 text-sm focus:outline-none focus:ring-2 focus:ring-primary-500'

export function ParticipantsTable({
                                      participants,
                                      isAdmin,
                                      editingParticipant,
                                      draftLapTime,
                                      onDraftLapTimeChange,
                                      onBeginEditLapTime,
                                      onCancelEditLapTime,
                                      onSaveLapTime,
                                      onClearLapTime,
                                      isSavingLapTime,
                                  }: ParticipantsTableProps) {
    // Precompute gaps in one pass.
    //   gapToLeader[i] = participants[i] - participants[0]
    //   interval[i]    = participants[i] - participants[i-1]
    const leaderSeconds = durationToSeconds(participants[0]?.participantBestLapTime)

    const rowsWithGaps = participants.map((p, i) => {
        const seconds = durationToSeconds(p.participantBestLapTime)

        const gapToLeader =
            seconds != null && leaderSeconds != null && i > 0
                ? seconds - leaderSeconds
                : null

        let interval: number | null = null
        if (i > 0 && seconds != null) {
            // Walk backwards to the nearest participant with a recorded time —
            // a gap should compare against the one physically in front of them
            // who actually set a lap, not a DNF-style "no time" row.
            for (let j = i - 1; j >= 0; j--) {
                const ahead = durationToSeconds(participants[j].participantBestLapTime)
                if (ahead != null) {
                    interval = seconds - ahead
                    break
                }
            }
        }

        return { participant: p, gapToLeader, interval }
    })

    return (
        <>
            <div className="flex items-center justify-between mb-4">
                <h2 className="text-lg font-semibold text-gray-900 dark:text-gray-100">
                    Participants
                </h2>
                <span className="text-xs text-gray-500 dark:text-gray-400">
          sorted by best lap
        </span>
            </div>

            <Table<{ participant: ParticipantDetailResponse; gapToLeader: number | null; interval: number | null }>
                columns={[
                    {
                        key: 'rank',
                        label: '#',
                        render: (_v, _row, index) => <RankBadge index={index} />,
                    },
                    {
                        key: 'participant',
                        label: 'Participant',
                        render: (_v, row) => row.participant.participantName,
                    },
                    {
                        key: 'bestLap',
                        label: 'Best Lap',
                        render: (_v, row) => {
                            if (isAdmin && editingParticipant === row.participant.participantName) {
                                return (
                                    <input
                                        type="text"
                                        autoFocus
                                        value={draftLapTime}
                                        onChange={(e) => onDraftLapTimeChange(e.target.value)}
                                        placeholder="1:22.555"
                                        className={inlineInputCls}
                                    />
                                )
                            }
                            return formatDurationJson(row.participant.participantBestLapTime)
                        },
                    },
                    {
                        key: 'gap',
                        label: 'Gap',
                        render: (_v, row) => (
                            <span className="tabular-nums text-gray-600 dark:text-gray-300">
                {formatGap(row.gapToLeader)}
              </span>
                        ),
                    },
                    {
                        key: 'interval',
                        label: 'Int',
                        render: (_v, row) => (
                            <span className="tabular-nums text-gray-600 dark:text-gray-300">
                {formatGap(row.interval)}
              </span>
                        ),
                    },
                    ...(isAdmin
                        ? [
                            {
                                key: 'actions',
                                label: '',
                                render: (_: unknown, row: {
                                    participant: ParticipantDetailResponse
                                    gapToLeader: number | null
                                    interval: number | null
                                }) => {
                                    const isEditingThis =
                                        editingParticipant === row.participant.participantName
                                    if (isEditingThis) {
                                        return (
                                            <div className="flex items-center justify-end gap-1">
                                                <button
                                                    type="button"
                                                    onClick={() =>
                                                        onSaveLapTime(row.participant.participantName)
                                                    }
                                                    disabled={isSavingLapTime}
                                                    className="p-1.5 rounded-lg hover:bg-green-50 dark:hover:bg-green-900/20 disabled:opacity-50"
                                                    aria-label="Save lap time"
                                                >
                                                    <Check className="w-4 h-4 text-green-600 dark:text-green-400" />
                                                </button>
                                                <button
                                                    type="button"
                                                    onClick={onCancelEditLapTime}
                                                    className="p-1.5 rounded-lg hover:bg-gray-100 dark:hover:bg-gray-700"
                                                    aria-label="Cancel"
                                                >
                                                    <X className="w-4 h-4 text-gray-500" />
                                                </button>
                                            </div>
                                        )
                                    }
                                    return (
                                        <div className="flex items-center justify-end gap-1">
                                            <button
                                                type="button"
                                                onClick={() => onBeginEditLapTime(row.participant)}
                                                className="p-1.5 rounded-lg hover:bg-gray-100 dark:hover:bg-gray-700"
                                                aria-label="Edit lap time"
                                                title="Edit lap time"
                                            >
                                                <Pencil className="w-4 h-4 text-gray-500" />
                                            </button>
                                            {row.participant.participantBestLapTime != null && (
                                                <button
                                                    type="button"
                                                    onClick={() =>
                                                        onClearLapTime(row.participant.participantName)
                                                    }
                                                    className="p-1.5 rounded-lg hover:bg-red-50 dark:hover:bg-red-900/20"
                                                    aria-label="Clear lap time"
                                                    title="Clear lap time"
                                                >
                                                    <Trash2 className="w-4 h-4 text-red-500" />
                                                </button>
                                            )}
                                        </div>
                                    )
                                },
                            },
                        ]
                        : []),
                ]}
                rows={rowsWithGaps}
                emptyMessage="No participants yet"
            />
        </>
    )
}