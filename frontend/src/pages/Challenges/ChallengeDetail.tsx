import { useMemo, useState } from 'react'
import { ArrowLeft } from 'lucide-react'
import { useNavigate, useParams } from 'react-router-dom'
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { challengeApi } from '@/services/api'
import { Button } from '@/components/shared/Button'
import { Card } from '@/components/shared/Card'
import { useConfirm } from '@/components/shared/ConfirmProvider'
import { ChallengeInfoCard } from '@/components/challenges/ChallengeInfoCard'
import { ChallengeActions } from '@/components/challenges/ChallengeActions'
import { ParticipantsTable } from '@/components/challenges/ParticipantsTable'
import {
    durationToSeconds,
    formatLapTime,
    isChallengeActive,
    parseLapTime,
} from '@/utils/format'
import { usePermissions } from '@/hooks/usePermissions'
import { useAuthStore } from '@/stores/useAuthStore'
import { toast } from '@/stores/useToastStore'
import type { ParticipantDetailResponse } from '@/types/challenge'

export function ChallengeDetail() {
    const params = useParams()
    const navigate = useNavigate()
    const queryClient = useQueryClient()
    const confirm = useConfirm()
    const challengeId = Number(params.id)
    const { isAdmin, isParticipant } = usePermissions()
    const user = useAuthStore((s) => s.user)

    // End-date edit state
    const [editEndDate, setEditEndDate] = useState(false)
    const [draftEndDate, setDraftEndDate] = useState('')

    // Admin inline lap-time edit state
    const [editingParticipant, setEditingParticipant] = useState<string | null>(null)
    const [draftLapTime, setDraftLapTime] = useState('')

    const { data, isLoading, isError, error } = useQuery({
        queryKey: ['challenge', challengeId],
        queryFn: () => challengeApi.get(challengeId),
        enabled: Number.isFinite(challengeId),
        refetchOnMount: 'always',
    })

    const invalidate = () => {
        queryClient.invalidateQueries({ queryKey: ['challenge', challengeId] })
        queryClient.invalidateQueries({ queryKey: ['challenges'] })
    }

    const registerMutation = useMutation({
        mutationFn: () => challengeApi.register(challengeId),
        onSuccess: () => {
            invalidate()
            toast.success('Registered for this challenge')
        },
    })

    const quitMutation = useMutation({
        mutationFn: () => challengeApi.quit(challengeId),
        onSuccess: () => {
            invalidate()
            toast.success('You quit this challenge')
        },
    })

    const deleteMutation = useMutation({
        mutationFn: () => challengeApi.delete(challengeId),
        onSuccess: () => {
            invalidate()
            toast.success('Challenge deleted')
            navigate('/challenges')
        },
    })

    const updateEndDateMutation = useMutation({
        mutationFn: (endDate: string) =>
            challengeApi.updateEndDate(challengeId, { endDate }),
        onSuccess: () => {
            invalidate()
            toast.success('End date updated')
            setEditEndDate(false)
        },
    })

    const updateLapTimeMutation = useMutation({
        mutationFn: (vars: { participantName: string; newLapTime: string | null }) =>
            challengeApi.updateLapTime(challengeId, {
                participantName: vars.participantName,
                newLapTime: vars.newLapTime,
            }),
        onSuccess: () => {
            invalidate()
            toast.success('Lap time updated')
            cancelEditLapTime()
        },
    })

    // Sorted participants — best lap first, no-time entries last, tie-broken by name.
    const sortedParticipants = useMemo<ParticipantDetailResponse[]>(() => {
        const list = data?.data?.participants ?? []
        return [...list].sort((a, b) => {
            const sa = durationToSeconds(a.participantBestLapTime)
            const sb = durationToSeconds(b.participantBestLapTime)
            if (sa == null && sb == null) return a.participantName.localeCompare(b.participantName)
            if (sa == null) return 1
            if (sb == null) return -1
            return sa - sb
        })
    }, [data])

    // ---------- end-date handlers ----------

    const beginEditEndDate = () => {
        if (!data?.data) return
        setDraftEndDate(data.data.challengeEndDate)
        setEditEndDate(true)
    }

    const saveEndDate = () => {
        if (!draftEndDate) {
            toast.warning('Pick a date first')
            return
        }
        updateEndDateMutation.mutate(draftEndDate)
    }

    // ---------- challenge actions ----------

    const handleDelete = async () => {
        const ok = await confirm({
            title: 'Delete challenge',
            message:
                'This will permanently remove the challenge and all its participants. This cannot be undone.',
            confirmLabel: 'Delete challenge',
            variant: 'danger',
        })
        if (ok) deleteMutation.mutate()
    }

    const handleQuit = async () => {
        const ok = await confirm({
            title: 'Quit challenge',
            message:
                'You will be removed from this challenge. You can re-register while it is still active.',
            confirmLabel: 'Quit',
        })
        if (ok) quitMutation.mutate()
    }

    // ---------- lap-time inline edit ----------

    const beginEditLapTime = (participant: ParticipantDetailResponse) => {
        setEditingParticipant(participant.participantName)
        setDraftLapTime('') // admin starts fresh; existing value is shown in the row
    }

    const cancelEditLapTime = () => {
        setEditingParticipant(null)
        setDraftLapTime('')
    }

    const saveLapTime = (participantName: string) => {
        const trimmed = draftLapTime.trim()
        if (!trimmed) {
            toast.error('Enter a lap time like 1:22.555')
            return
        }
        const seconds = parseLapTime(trimmed)
        if (!Number.isFinite(seconds) || seconds <= 0) {
            toast.error('Enter a lap time like 1:22.555')
            return
        }
        updateLapTimeMutation.mutate({
            participantName,
            newLapTime: formatLapTime(seconds),
        })
    }

    const clearLapTime = async (participantName: string) => {
        const ok = await confirm({
            title: 'Clear lap time',
            message: `Clear ${participantName}'s lap time? They will still be registered for the challenge.`,
            confirmLabel: 'Clear',
            variant: 'danger',
        })
        if (!ok) return
        updateLapTimeMutation.mutate({ participantName, newLapTime: null })
    }

    // ---------- render ----------

    if (isLoading) return <div className="text-center py-12">Loading...</div>
    if (isError || !data?.data) {
        return (
            <div className="text-center py-12">
                <p className="text-red-600 dark:text-red-400">
                    Failed to load challenge:{' '}
                    {error instanceof Error ? error.message : 'unknown error'}
                </p>
            </div>
        )
    }

    const challenge = data.data
    const active = isChallengeActive(challenge.challengeEndDate)
    const alreadyRegistered = challenge.participants.some(
        (p) => p.participantName === user?.username,
    )

    const canRegister = active && isParticipant && !alreadyRegistered
    const canQuit = active && isParticipant && alreadyRegistered
    const canEditOwnLapTime =
        isAdmin || (isParticipant && active && alreadyRegistered)
    const showAnyAction = canRegister || canQuit || canEditOwnLapTime || isAdmin

    return (
        <div>
            <div className="mb-6">
                <Button variant="ghost" onClick={() => navigate('/challenges')} className="mr-4">
                    <ArrowLeft className="w-4 h-4 mr-2" />
                    Back to List
                </Button>
                <div className="inline-flex items-baseline gap-3 flex-wrap">
                    <h1 className="text-2xl font-bold text-gray-900 dark:text-gray-100">
                        {challenge.trackName} · {challenge.carBrand} {challenge.carName}
                    </h1>
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
                </div>
            </div>

            <Card>
                <ChallengeInfoCard
                    challenge={challenge}
                    editEndDate={editEndDate}
                    draftEndDate={draftEndDate}
                    onDraftEndDateChange={setDraftEndDate}
                    onSaveEndDate={saveEndDate}
                    onCancelEditEndDate={() => setEditEndDate(false)}
                    isSavingEndDate={updateEndDateMutation.isPending}
                />

                {showAnyAction && (
                    <ChallengeActions
                        isAdmin={isAdmin}
                        active={active}
                        editEndDate={editEndDate}
                        canRegister={canRegister}
                        canQuit={canQuit}
                        canEditOwnLapTime={canEditOwnLapTime}
                        onRegister={() => registerMutation.mutate()}
                        onQuit={handleQuit}
                        onUpdateLapTime={() => navigate(`/challenges/${challengeId}/lap-time`)}
                        onBeginEditEndDate={beginEditEndDate}
                        onDelete={handleDelete}
                        isRegistering={registerMutation.isPending}
                        isQuitting={quitMutation.isPending}
                        isDeleting={deleteMutation.isPending}
                    />
                )}
            </Card>

            <Card className="mt-6">
                <ParticipantsTable
                    participants={sortedParticipants}
                    isAdmin={isAdmin}
                    editingParticipant={editingParticipant}
                    draftLapTime={draftLapTime}
                    onDraftLapTimeChange={setDraftLapTime}
                    onBeginEditLapTime={beginEditLapTime}
                    onCancelEditLapTime={cancelEditLapTime}
                    onSaveLapTime={saveLapTime}
                    onClearLapTime={clearLapTime}
                    isSavingLapTime={updateLapTimeMutation.isPending}
                />
            </Card>
        </div>
    )
}