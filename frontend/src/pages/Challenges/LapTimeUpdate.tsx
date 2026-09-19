import { useState } from 'react'
import { ArrowLeft, Save, Trash2 } from 'lucide-react'
import { useNavigate, useParams } from 'react-router-dom'
import { useForm } from 'react-hook-form'
import { useMutation, useQueryClient } from '@tanstack/react-query'
import { Button } from '@/components/shared/Button'
import { Input } from '@/components/shared/Input'
import { Card } from '@/components/shared/Card'
import { useConfirm } from '@/components/shared/ConfirmProvider'
import { challengeApi } from '@/services/api'
import { useAuthStore } from '@/stores/useAuthStore'
import { formatLapTime, parseLapTime } from '@/utils/format'
import { toast } from '@/stores/useToastStore'

interface FormValues {
  lapTime: string
}

export function LapTimeUpdate() {
  const params = useParams()
  const navigate = useNavigate()
  const queryClient = useQueryClient()
  const confirm = useConfirm()
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

  const invalidate = () =>
      queryClient.invalidateQueries({ queryKey: ['challenge', challengeId] })

  const saveMutation = useMutation({
    mutationFn: async (values: FormValues) => {
      if (!user) throw new Error('Please log in before updating a lap time')
      const seconds = parseLapTime(values.lapTime)
      if (!Number.isFinite(seconds) || seconds <= 0) {
        throw new Error('Enter a lap time like 1:22.555')
      }
      await challengeApi.updateLapTime(challengeId, {
        participantName: user.username,
        newLapTime: formatLapTime(seconds),
      })
    },
    onSuccess: async () => {
      await invalidate()
      toast.success('Lap time updated')
      navigate(`/challenges/${challengeId}`)
    },
    onError: (err) => {
      setFormError(err instanceof Error ? err.message : 'Failed to update lap time')
    },
  })

  const clearMutation = useMutation({
    mutationFn: async () => {
      if (!user) throw new Error('Please log in before clearing a lap time')
      await challengeApi.updateLapTime(challengeId, {
        participantName: user.username,
        newLapTime: null,
      })
    },
    onSuccess: async () => {
      await invalidate()
      toast.success('Lap time cleared')
      navigate(`/challenges/${challengeId}`)
    },
    onError: (err) => {
      setFormError(err instanceof Error ? err.message : 'Failed to clear lap time')
    },
  })

  const onSubmit = (values: FormValues) => {
    setFormError(null)
    saveMutation.mutate(values)
  }

  const handleClearLapTime = async () => {
    const ok = await confirm({
      title: 'Clear lap time',
      message: 'Remove your recorded lap time for this challenge?',
      confirmLabel: 'Clear',
      variant: 'danger',
    })
    if (ok) clearMutation.mutate()
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
                        : 'Use m:ss.S up to m:ss.SSS — e.g. 1:22.555'
                  },
                })}
                error={errors.lapTime?.message}
                helperText="Format: m:ss.S up to m:ss.SSS — e.g. 1:22.555. Plain seconds also accepted."
            />

            {typed && (
                <div className="rounded-lg bg-gray-50 dark:bg-gray-800/60 border border-gray-200 dark:border-gray-700 px-4 py-3">
                  {previewValid ? (
                      <p className="text-sm text-gray-700 dark:text-gray-300">
                        Will save as{' '}
                        <span className="font-mono font-semibold text-gray-900 dark:text-gray-100">
                    {formatLapTime(previewSeconds)}
                  </span>
                      </p>
                  ) : (
                      <p className="text-sm text-red-600 dark:text-red-400">
                        Couldn't parse that. Try <span className="font-mono">1:22.555</span>.
                      </p>
                  )}
                </div>
            )}

            <div className="flex justify-between gap-2 pt-4">
              <Button
                  type="button"
                  variant="danger"
                  onClick={handleClearLapTime}
                  isLoading={clearMutation.isPending}
              >
                <Trash2 className="w-4 h-4 mr-2" />
                Clear Lap Time
              </Button>
              <Button type="submit" isLoading={saveMutation.isPending}>
                <Save className="w-4 h-4 mr-2" />
                Save Lap Time
              </Button>
            </div>
          </form>
        </Card>
      </div>
  )
}