import { useQuery } from '@tanstack/react-query'
import { TrendingDown, TrendingUp, Minus } from 'lucide-react'
import { participantApi } from '@/services/api'
import { Modal } from '@/components/shared/Modal'
import { formatDateTime } from '@/utils/format'

interface Props {
    isOpen: boolean
    onClose: () => void
    participantId: number
    participantName: string
}

export function ParticipantTimelineModal({ isOpen, onClose, participantId, participantName }: Props) {
    const { data, isLoading } = useQuery({
        queryKey: ['participant-timeline', participantId],
        queryFn: () => participantApi.getTimeline(participantId),
        enabled: isOpen,
    })

    return (
        <Modal isOpen={isOpen} onClose={onClose} title={`${participantName}'s Lap History`} size="md">
            {isLoading ? (
                <div className="flex justify-center py-8 text-gray-500">Loading history...</div>
            ) : !data?.data || data.data.length === 0 ? (
                <p className="text-center text-gray-500 py-8">No historical lap times recorded yet.</p>
            ) : (
                <div className="overflow-x-auto">
                    <table className="w-full text-sm text-left">
                        <thead className="bg-gray-50 dark:bg-gray-800 text-gray-500 uppercase text-[10px] tracking-wider border-b border-gray-200 dark:border-gray-700">
                        <tr>
                            <th className="px-4 py-3">Date</th>
                            <th className="px-4 py-3">Lap Time</th>
                            <th className="px-4 py-3 text-right">Delta (Gap)</th>
                        </tr>
                        </thead>
                        <tbody className="divide-y divide-gray-100 dark:divide-gray-700">
                        {data.data.map((entry, idx) => {
                            // Backend sends gaps like "-00:01.250" (improvement) or "00:01.250" (regression)
                            const isImprovement = entry.gap.startsWith('-')
                            const isNeutral = entry.gap === '00:00.000' || entry.gap === '0:00.000'
                            const isRegression = !isImprovement && !isNeutral

                            // If it's an improvement, we remove the minus sign for display but keep the green color
                            const displayGap = isImprovement ? entry.gap.substring(1) : entry.gap

                            return (
                                <tr key={idx} className="hover:bg-gray-50 dark:hover:bg-gray-800/50">
                                    <td className="px-4 py-3 text-gray-500 text-xs">
                                        {formatDateTime(entry.updatedAt)}
                                    </td>
                                    <td className="px-4 py-3 font-mono font-medium text-gray-900 dark:text-gray-100">
                                        {entry.lapTime}
                                    </td>
                                    <td className="px-4 py-3 text-right font-mono text-xs">
                      <span
                          className={`inline-flex items-center gap-1 ${
                              isImprovement
                                  ? 'text-green-600 dark:text-green-400'
                                  : isRegression
                                      ? 'text-red-600 dark:text-red-400'
                                      : 'text-gray-400'
                          }`}
                      >
                        {isImprovement ? (
                            <TrendingDown className="w-3.5 h-3.5" />
                        ) : isRegression ? (
                            <TrendingUp className="w-3.5 h-3.5" />
                        ) : (
                            <Minus className="w-3.5 h-3.5" />
                        )}
                          {isNeutral ? '—' : displayGap}
                      </span>
                                    </td>
                                </tr>
                            )
                        })}
                        </tbody>
                    </table>
                </div>
            )}
        </Modal>
    )
}