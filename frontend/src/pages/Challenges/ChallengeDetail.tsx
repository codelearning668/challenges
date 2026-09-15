import { useState } from 'react'
import { ArrowLeft, UserPlus, Timer, Trash2, Pencil, Check, X } from 'lucide-react'
import { useNavigate, useParams } from 'react-router-dom'
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { challengeApi } from '@/services/api'
import { Button } from '@/components/shared/Button'
import { Card } from '@/components/shared/Card'
import { Table } from '@/components/shared/Table'
import { formatDate, formatDurationJson } from '@/utils/format'
import { usePermissions } from '@/hooks/usePermissions'
import { useAuthStore } from '@/stores/useAuthStore'
import { toast } from '@/stores/useToastStore'
import type {
    ChallengeDetailResponse,
    ParticipantDetailResponse,
} from '@/types/challenge'

function Field({ label, value }: { label: string; value: React.ReactNode }) {
    return (
        <div>
            <label className="text-sm font-medium text-gray-500 dark:text-gray-400">{label}</label>
            <p className="mt-1 text-gray-900 dark:text-gray-100">{value}</p>
        </div>
    )
}

export function ChallengeDetail() {
    const params = useParams()
    const navigate = useNavigate()
    const queryClient = useQueryClient()
    const challengeId = Number(params.id)
    const { isAdmin, canRegisterForChallenge, canUpdateLapTime } = usePermissions()
    const user = useAuthStore((s) => s.user)

    const [editEndDate, setEditEndDate] = useState(false)
    const [draftEndDate, setDraftEndDate] = useState('')

    const { data, isLoading, isError, error } = useQuery({
        queryKey: ['challenge', challengeId],
        queryFn: () => challengeApi.get(challengeId),
        enabled: Number.isFinite(challengeId),
        refetchOnMount: 'always',
    })

    const registerMutation = useMutation({
        mutationFn: () => challengeApi.register(challengeId),
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: ['challenge', challengeId] })
            toast.success('Registered for this challenge')
        },
    })

    const deleteMutation = useMutation({
        mutationFn: () => challengeApi.delete(challengeId),
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: ['challenges'] })
            toast.success('Challenge deleted')
            navigate('/challenges')
        },
    })

    const updateEndDateMutation = useMutation({
        mutationFn: (endDate: string) =>
            challengeApi.updateEndDate(challengeId, { endDate }),
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: ['challenge', challengeId] })
            queryClient.invalidateQueries({ queryKey: ['challenges'] })
            toast.success('End date updated')
            setEditEndDate(false)
        },
    })

    const handleDelete = () => {
        if (confirm('Delete this challenge? This cannot be undone.')) {
            deleteMutation.mutate()
        }
    }

    const beginEditEndDate = (current: string) => {
        setDraftEndDate(current)
        setEditEndDate(true)
    }

    const saveEndDate = () => {
        if (!draftEndDate) {
            toast.warning('Pick a date first')
            return
        }
        updateEndDateMutation.mutate(draftEndDate)
    }

    if (isLoading) return <div className="text-center py-12">Loading...</div>
    if (isError || !data?.data) {
        return (
            <div className="text-center py-12">
                <p className="text-red-600 dark:text-red-400">
                    Failed to load challenge: {error instanceof Error ? error.message : 'unknown error'}
                </p>
            </div>
        )
    }

    const c: ChallengeDetailResponse = data.data
    const alreadyRegistered = c.participants.some(
        (p) => p.participantName === user?.username,
    )

    const showAnyAction = isAdmin || canRegisterForChallenge || canUpdateLapTime

    return (
        <div>
            <div className="mb-6">
                <Button variant="ghost" onClick={() => navigate('/challenges')} className="mr-4">
                    <ArrowLeft className="w-4 h-4 mr-2" />
                    Back to List
                </Button>
                <h1 className="text-2xl font-bold text-gray-900 dark:text-gray-100">
                    {c.trackName} · {c.carBrand} {c.carName}
                </h1>
            </div>

            <Card>
                <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                    {/* End Date — inline editable for admins */}
                    <div>
                        <label className="text-sm font-medium text-gray-500 dark:text-gray-400">
                            End Date
                        </label>
                        {editEndDate ? (
                            <div className="mt-1 flex items-center gap-2">
                                <input
                                    type="date"
                                    value={draftEndDate}
                                    onChange={(e) => setDraftEndDate(e.target.value)}
                                    className="flex-1 px-3 py-2 rounded-lg border border-gray-300 dark:border-gray-600 bg-white dark:bg-gray-800 text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-primary-500"
                                />
                                <Button
                                    size="sm"
                                    onClick={saveEndDate}
                                    isLoading={updateEndDateMutation.isPending}
                                >
                                    <Check className="w-4 h-4" />
                                </Button>
                                <Button
                                    size="sm"
                                    variant="secondary"
                                    onClick={() => setEditEndDate(false)}
                                >
                                    <X className="w-4 h-4" />
                                </Button>
                            </div>
                        ) : (
                            <div className="mt-1 flex items-center gap-2">
                                <p className="text-gray-900 dark:text-gray-100">
                                    {formatDate(c.challengeEndDate)}
                                </p>
                                {isAdmin && (
                                    <button
                                        type="button"
                                        onClick={() => beginEditEndDate(c.challengeEndDate)}
                                        className="p-1 rounded hover:bg-gray-100 dark:hover:bg-gray-700 text-gray-500 dark:text-gray-400"
                                        aria-label="Edit end date"
                                    >
                                        <Pencil className="w-4 h-4" />
                                    </button>
                                )}
                            </div>
                        )}
                    </div>

                    <Field
                        label="Track"
                        value={`${c.trackName}${c.trackCountry ? ` (${c.trackCountry})` : ''}`}
                    />
                    <Field
                        label="Track Length"
                        value={c.trackLengthKm != null ? `${c.trackLengthKm.toFixed(2)} km` : '—'}
                    />
                    <Field label="Car" value={`${c.carBrand} ${c.carName}`} />
                    <Field
                        label="Horsepower"
                        value={c.carHorsePower != null ? `${c.carHorsePower} hp` : '—'}
                    />
                    <Field
                        label="Torque"
                        value={c.carTorque != null ? `${c.carTorque} Nm` : '—'}
                    />
                    <Field label="Best Lap" value={formatDurationJson(c.bestLapTime)} />
                    <Field label="Best Driver" value={c.bestParticipantName ?? '—'} />
                </div>

                {showAnyAction && (
                    <div className="flex justify-end gap-2 mt-6 pt-6 border-t border-gray-200 dark:border-gray-700">
                        {canRegisterForChallenge && !alreadyRegistered && (
                            <Button
                                onClick={() => registerMutation.mutate()}
                                isLoading={registerMutation.isPending}
                            >
                                <UserPlus className="w-4 h-4 mr-2" />
                                Register me
                            </Button>
                        )}

                        {alreadyRegistered && (
                            <span className="text-sm text-gray-500 dark:text-gray-400 self-center">
                You are registered for this challenge.
              </span>
                        )}

                        {canUpdateLapTime && (
                            <Button
                                variant="secondary"
                                onClick={() => navigate(`/challenges/${challengeId}/lap-time`)}
                            >
                                <Timer className="w-4 h-4 mr-2" />
                                Update lap time
                            </Button>
                        )}

                        {isAdmin && (
                            <Button
                                variant="danger"
                                onClick={handleDelete}
                                isLoading={deleteMutation.isPending}
                            >
                                <Trash2 className="w-4 h-4 mr-2" />
                                Delete
                            </Button>
                        )}
                    </div>
                )}
            </Card>

            <Card className="mt-6">
                <h2 className="text-lg font-semibold text-gray-900 dark:text-gray-100 mb-4">
                    Participants
                </h2>
                <Table<ParticipantDetailResponse>
                    columns={[
                        { key: 'participantName', label: 'Participant' },
                        {
                            key: 'participantBestLapTime',
                            label: 'Best Lap',
                            render: (v) => formatDurationJson(v),
                        },
                    ]}
                    rows={c.participants ?? []}
                    emptyMessage="No participants yet"
                />
            </Card>
        </div>
    )
}