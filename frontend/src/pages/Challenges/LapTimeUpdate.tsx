import { useState } from 'react'
import { ArrowLeft, Save } from 'lucide-react'
import { useNavigate, useParams } from 'react-router-dom'
import { useForm } from 'react-hook-form'
import { useMutation, useQueryClient } from '@tanstack/react-query'
import { Button } from '@/components/shared/Button'
import { Input } from '@/components/shared/Input'
import { Card } from '@/components/shared/Card'
import { challengeApi } from '@/services/api'
import { useAuthStore } from '@/stores/useAuthStore'
import { secondsToIsoDuration } from '@/utils/format'

interface FormValues {
  lapTime: string
}

export function LapTimeUpdate() {
  const params = useParams()
  const navigate = useNavigate()
  const queryClient = useQueryClient()
  const user = useAuthStore((s) => s.user)
  const challengeId = Number(params.id)

  const [formError, setFormError] = useState<string | null>(null)

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<FormValues>()

  const mutation = useMutation({
    mutationFn: async (values: FormValues) => {
      if (!user) throw new Error('Please log in before updating a lap time')
      const seconds = Number(values.lapTime)
      if (!Number.isFinite(seconds) || seconds <= 0) {
        throw new Error('Lap time must be a positive number')
      }
      await challengeApi.updateLapTime(challengeId, {
        participantName: user.username,
        newLapTime: secondsToIsoDuration(seconds),
      })
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['challenge', challengeId] })
      navigate(`/challenges/${challengeId}`)
    },
    onError: (err) => {
      setFormError(err instanceof Error ? err.message : 'Failed to update lap time')
    },
  })

  const onSubmit = (values: FormValues) => {
    setFormError(null)
    mutation.mutate(values)
  }

  return (
      <div>
        <div className="mb-6">
          <Button
              variant="ghost"
              onClick={() => navigate(`/challenges/${challengeId}`)}
              className="mr-4"
          >
            <ArrowLeft className="w-4 h-4 mr-2" />
            Back to Challenge
          </Button>
          <h1 className="text-2xl font-bold text-gray-900 dark:text-gray-100">Update Lap Time</h1>
        </div>

        <Card>
          <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
            {formError && <p className="text-sm text-red-600 dark:text-red-400">{formError}</p>}

            <Input
                label="Lap Time (seconds)"
                type="number"
                step="0.001"
                min="0"
                placeholder="e.g., 123.456"
                {...register('lapTime', { required: 'Lap time is required' })}
                error={errors.lapTime?.message}
            />

            <div className="flex justify-end gap-2 pt-4">
              <Button type="submit" isLoading={mutation.isPending}>
                <Save className="w-4 h-4 mr-2" />
                Save Lap Time
              </Button>
            </div>
          </form>
        </Card>
      </div>
  )
}