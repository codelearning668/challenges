import { useState } from 'react'
import { ArrowLeft, Plus } from 'lucide-react'
import { useNavigate } from 'react-router-dom'
import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { useQuery } from '@tanstack/react-query'
import { challengeSchema, type ChallengeFormData } from '@/utils/validators'
import { Button } from '@/components/shared/Button'
import { Input } from '@/components/shared/Input'
import { Select } from '@/components/shared/Select'
import { Card } from '@/components/shared/Card'
import { carApi, trackApi, challengeApi } from '@/services/api'

export function ChallengeForm() {
  const navigate = useNavigate()
  const [isLoading, setIsLoading] = useState(false)
  const [formError, setFormError] = useState<string | null>(null)

  const { data: tracksRes, isLoading: tracksLoading } = useQuery({
    queryKey: ['tracks', 'all'],
    queryFn: () => trackApi.search(),
  })
  const { data: carsRes, isLoading: carsLoading } = useQuery({
    queryKey: ['cars', 'all'],
    queryFn: () => carApi.search(),
  })

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<ChallengeFormData>({
    resolver: zodResolver(challengeSchema),
    defaultValues: {
      // Empty strings so the DOM renders blank; zod coerces to number on submit.
      trackId: '' as unknown as number,
      carId: '' as unknown as number,
      endDate: '',
    },
  })

  const onSubmit = async (data: ChallengeFormData) => {
    try {
      setIsLoading(true)
      setFormError(null)
      await challengeApi.create({
        trackId: data.trackId,
        carId: data.carId,
        endDate: data.endDate,
      })
      navigate('/challenges')
    } catch (err) {
      setFormError(err instanceof Error ? err.message : 'Failed to create challenge')
    } finally {
      setIsLoading(false)
    }
  }

  const trackOptions = (tracksRes?.data ?? []).map((t) => ({
    value: t.id,
    label: t.country ? `${t.name} (${t.country})` : t.name,
  }))

  const carOptions = (carsRes?.data ?? []).map((c) => ({
    value: c.id,
    label: `${c.brand} ${c.name}`,
  }))

  const optionsLoading = tracksLoading || carsLoading

  return (
      <div>
        <div className="mb-6">
          <Button variant="ghost" onClick={() => navigate('/challenges')} className="mr-4">
            <ArrowLeft className="w-4 h-4 mr-2" />
            Back to List
          </Button>
          <h1 className="text-2xl font-bold text-gray-900 dark:text-gray-100">
            Add New Challenge
          </h1>
        </div>

        <Card>
          <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
            {formError && (
                <p className="text-sm text-red-600 dark:text-red-400">{formError}</p>
            )}

            <Select
                label="Track"
                options={trackOptions}
                placeholder={optionsLoading ? 'Loading tracks…' : 'Select a track'}
                disabled={optionsLoading}
                {...register('trackId')}
                error={errors.trackId?.message}
            />

            <Select
                label="Car"
                options={carOptions}
                placeholder={optionsLoading ? 'Loading cars…' : 'Select a car'}
                disabled={optionsLoading}
                {...register('carId')}
                error={errors.carId?.message}
            />

            <Input
                label="End Date"
                type="date"
                {...register('endDate')}
                error={errors.endDate?.message}
            />

            <div className="flex justify-end gap-2 pt-4">
              <Button
                  type="submit"
                  isLoading={isLoading}
                  disabled={optionsLoading}
              >
                <Plus className="w-4 h-4 mr-2" />
                Create Challenge
              </Button>
            </div>
          </form>
        </Card>
      </div>
  )
}