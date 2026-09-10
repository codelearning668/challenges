import { ArrowLeft, UserPlus, Timer } from 'lucide-react'
import { useNavigate, useParams } from 'react-router-dom'
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { challengeApi } from '@/services/api'
import { Button } from '@/components/shared/Button'
import { Card } from '@/components/shared/Card'
import { Table } from '@/components/shared/Table'
import { formatDate, formatDurationJson } from '@/utils/format'
import type { ChallengeDetailResponse, ParticipantDetailResponse } from '@/types/challenge'

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

  const { data, isLoading, isError, error } = useQuery({
    queryKey: ['challenge', challengeId],
    queryFn: () => challengeApi.get(challengeId),
    enabled: Number.isFinite(challengeId),
  })

  const registerMutation = useMutation({
    mutationFn: () => challengeApi.register(challengeId),
    onSuccess: () =>
        queryClient.invalidateQueries({ queryKey: ['challenge', challengeId] }),
  })

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
            <Field label="End Date" value={formatDate(c.challengeEndDate)} />
            <Field
                label="Track"
                value={`${c.trackName}${c.trackCountry ? ` (${c.trackCountry})` : ''}`}
            />
            <Field
                label="Track Length"
                value={c.trackLengthKm != null ? `${c.trackLengthKm} km` : '—'}
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

          <div className="flex justify-end gap-2 mt-6 pt-6 border-t border-gray-200 dark:border-gray-700">
            <Button
                onClick={() => registerMutation.mutate()}
                isLoading={registerMutation.isPending}
            >
              <UserPlus className="w-4 h-4 mr-2" />
              Register me
            </Button>
            <Button
                variant="secondary"
                onClick={() => navigate(`/challenges/${challengeId}/lap-time`)}
            >
              <Timer className="w-4 h-4 mr-2" />
              Update lap time
            </Button>
          </div>
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