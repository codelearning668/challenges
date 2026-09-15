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
import {
  formatLapTime,
  parseLapTime,
  secondsToDurationPayload,
} from '@/utils/format'
import { toast } from '@/stores/useToastStore'

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
    watch,
    formState: { errors },
  } = useForm<FormValues>({ defaultValues: { lapTime: '' } })

  const typed = watch('lapTime')
  const previewSeconds = typed ? parseLapTime(typed) : NaN
  const previewValid = Number.isFinite(previewSeconds) && previewSeconds > 0

  const mutation = useMutation({
    mutationFn: async (values: FormValues) => {
      if (!user) throw new Error('Please log in before updating a lap time')

      const seconds = parseLapTime(values.lapTime)
      if (!Number.isFinite(seconds) || seconds <= 0) {
        throw new Error('Enter a lap time like 1:22.555 or 82.555')
      }

      await challengeApi.updateLapTime(challengeId, {
        participantName: user.username,
        // POJO, not an ISO string.
        newLapTime: secondsToDurationPayload(seconds),
      })
    },
    onSuccess: async () => {
      await queryClient.invalidateQueries({ queryKey: ['challenge', challengeId] })
      toast.success('Lap time updated')
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
          <h1 className="text-2xl font-bold text-gray-900 dark:text-gray-100">
            Update Lap Time
          </h1>
        </div>

        <Card>
          <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
            {formError && (
                <p className="text-sm text-red-600 dark:text-red-400">{formError}</p>
            )}

            <Input
                label="Lap Time"
                type="text"
                inputMode="decimal"
                autoComplete="off"
                placeholder="e.g., 1:22.555"
                {...register('lapTime', {
                  required: 'Lap time is required',
                  validate: (v) => {
                    const s = parseLapTime(v)
                    return Number.isFinite(s) && s > 0
                        ? true
                        : 'Use mm:ss.mmm (1:22.555) or plain seconds (82.555)'
                  },
                })}
                error={errors.lapTime?.message}
                helperText="Format: mm:ss.mmm — for example 1:22.555, or plain seconds like 82.555."
            />

            {typed && (
                <div className="rounded-lg bg-gray-50 dark:bg-gray-800/60 border border-gray-200 dark:border-gray-700 px-4 py-3">
                  {previewValid ? (
                      <p className="text-sm text-gray-700 dark:text-gray-300">
                        Will save as{' '}
                        <span className="font-mono font-semibold text-gray-900 dark:text-gray-100">
                    {formatLapTime(previewSeconds)}
                  </span>{' '}
                        <span className="text-gray-500 dark:text-gray-400">
                    ({previewSeconds} s)
                  </span>
                      </p>
                  ) : (
                      <p className="text-sm text-red-600 dark:text-red-400">
                        Couldn't parse that. Try <span className="font-mono">1:22.555</span>.
                      </p>
                  )}
                </div>
            )}

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