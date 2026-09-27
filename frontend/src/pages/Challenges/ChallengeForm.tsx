import { useState } from 'react'
import { ArrowLeft, Plus } from 'lucide-react'
import { useNavigate } from 'react-router-dom'
import { Controller, useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { useQuery, useQueryClient } from '@tanstack/react-query'
import { challengeSchema, type ChallengeFormData } from '@/utils/validators'
import { Button } from '@/components/shared/Button'
import { Select } from '@/components/shared/Select'
import { DatePicker } from '@/components/shared/DatePicker'
import { Card } from '@/components/shared/Card'
import { carApi, trackApi, challengeApi } from '@/services/api'
import { useSimulators } from '@/hooks/useSimulators'
import { toast } from '@/stores/useToastStore'

export function ChallengeForm() {
  const navigate = useNavigate()
  const queryClient = useQueryClient()
  const [isLoading, setIsLoading] = useState(false)
  const [formError, setFormError] = useState<string | null>(null)

  const [simulatorId, setSimulatorId] = useState('')
  const [dlcFilter, setDlcFilter] = useState<'' | 'true' | 'false'>('')

  const { data: simulatorsRes, isLoading: simulatorsLoading } = useSimulators()

  // Build the shared criteria for both dropdowns.
  const listCriteria = {
    ...(simulatorId ? { simulatorId: Number(simulatorId) } : {}),
    ...(dlcFilter !== '' ? { fromDlc: dlcFilter === 'true' } : {}),
  }

  const { data: tracksRes, isLoading: tracksLoading } = useQuery({
    queryKey: ['tracks', 'challenge-form', simulatorId, dlcFilter],
    queryFn: () => trackApi.search(listCriteria),
  })
  const { data: carsRes, isLoading: carsLoading } = useQuery({
    queryKey: ['cars', 'challenge-form', simulatorId, dlcFilter],
    queryFn: () => carApi.search(listCriteria),
  })

  const {
    control,
    register,
    handleSubmit,
    setValue,
    formState: { errors },
  } = useForm<ChallengeFormData>({
    resolver: zodResolver(challengeSchema),
    defaultValues: {
      trackId: '' as unknown as number,
      carId: '' as unknown as number,
      endDate: '',
    },
  })

  const onSimulatorChange = (value: string) => {
    setSimulatorId(value)
    // Reset track/car — they may not belong to the new simulator
    setValue('trackId', '' as unknown as number)
    setValue('carId', '' as unknown as number)
  }

  const onDlcFilterChange = (value: '' | 'true' | 'false') => {
    setDlcFilter(value)
    // Reset track/car — they may not match the new DLC filter
    setValue('trackId', '' as unknown as number)
    setValue('carId', '' as unknown as number)
  }

  const onSubmit = async (data: ChallengeFormData) => {
    try {
      setIsLoading(true)
      setFormError(null)
      await challengeApi.create({
        trackId: data.trackId,
        carId: data.carId,
        endDate: data.endDate,
      })
      await queryClient.invalidateQueries({ queryKey: ['challenges'] })
      toast.success('Challenge created')
      navigate('/challenges')
    } catch (err) {
      setFormError(err instanceof Error ? err.message : 'Failed to create challenge')
    } finally {
      setIsLoading(false)
    }
  }

  const simulatorOptions = (simulatorsRes?.data ?? []).map((s) => ({
    value: s.id,
    label: s.name,
  }))

  const trackOptions = (tracksRes?.data ?? []).map((t) => ({
    value: t.id,
    label: [
      t.name,
      t.country ? `(${t.country})` : null,
      t.fromDlc ? '· DLC' : null,
    ]
        .filter(Boolean)
        .join(' '),
  }))

  const carOptions = (carsRes?.data ?? []).map((c) => ({
    value: c.id,
    label: [c.brand, c.name, c.fromDlc ? '· DLC' : null]
        .filter(Boolean)
        .join(' '),
  }))

  const optionsLoading = tracksLoading || carsLoading
  const noSimulatorChosen = !simulatorId

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

            <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
              <Select
                  label="Simulator"
                  options={simulatorOptions}
                  placeholder={
                    simulatorsLoading ? 'Loading simulators…' : 'Select a simulator'
                  }
                  disabled={simulatorsLoading}
                  value={simulatorId}
                  onChange={(e) => onSimulatorChange(e.target.value)}
              />

              <Select
                  label="Availability"
                  options={[
                    { value: 'false', label: 'Base game only' },
                    { value: 'true', label: 'DLC only' },
                  ]}
                  placeholder="Any"
                  value={dlcFilter}
                  onChange={(e) =>
                      onDlcFilterChange(e.target.value as '' | 'true' | 'false')
                  }
                  helperText="Filter the dropdowns below."
              />
            </div>

            <Select
                label="Track"
                options={trackOptions}
                placeholder={
                  noSimulatorChosen
                      ? 'Pick a simulator first'
                      : optionsLoading
                          ? 'Loading tracks…'
                          : trackOptions.length === 0
                              ? 'No tracks match the filter'
                              : 'Select a track'
                }
                disabled={noSimulatorChosen || optionsLoading || trackOptions.length === 0}
                {...register('trackId')}
                error={errors.trackId?.message}
            />

            <Select
                label="Car"
                options={carOptions}
                placeholder={
                  noSimulatorChosen
                      ? 'Pick a simulator first'
                      : optionsLoading
                          ? 'Loading cars…'
                          : carOptions.length === 0
                              ? 'No cars match the filter'
                              : 'Select a car'
                }
                disabled={noSimulatorChosen || optionsLoading || carOptions.length === 0}
                {...register('carId')}
                error={errors.carId?.message}
            />

            <Controller
                name="endDate"
                control={control}
                render={({ field }) => (
                    <DatePicker
                        label="End Date"
                        value={field.value}
                        onChange={field.onChange}
                        placeholder="Pick an end date"
                        error={errors.endDate?.message}
                        helperText="Only today or later can be selected."
                    />
                )}
            />

            <div className="flex justify-end gap-2 pt-4">
              <Button
                  type="submit"
                  isLoading={isLoading}
                  disabled={optionsLoading || noSimulatorChosen}
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