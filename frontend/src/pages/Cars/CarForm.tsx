import { useState } from 'react'
import { ArrowLeft, Plus } from 'lucide-react'
import { useNavigate } from 'react-router-dom'
import { Controller, useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { carSchema, type CarFormData } from '@/utils/validators'
import { Button } from '@/components/shared/Button'
import { Input } from '@/components/shared/Input'
import { Select } from '@/components/shared/Select'
import { Card } from '@/components/shared/Card'
import { carApi } from '@/services/api'
import { useSimulators } from '@/hooks/useSimulators'

export function CarForm() {
  const navigate = useNavigate()
  const [isLoading, setIsLoading] = useState(false)
  const [formError, setFormError] = useState<string | null>(null)

  const { data: simulatorsRes, isLoading: simulatorsLoading } = useSimulators()

  const {
    control,
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<CarFormData>({
    resolver: zodResolver(carSchema),
    defaultValues: {
      simulatorId: '' as unknown as number,
      fromDlc: false,
    },
  })

  const onSubmit = async (data: CarFormData) => {
    try {
      setIsLoading(true)
      setFormError(null)
      await carApi.create({
        brand: data.brand,
        name: data.name,
        hp: data.hp,
        torque: data.torque,
        drive: data.drive,
        simulatorId: data.simulatorId,
        fromDlc: data.fromDlc,
      })
      navigate('/cars')
    } catch (err) {
      setFormError(err instanceof Error ? err.message : 'Failed to create car')
    } finally {
      setIsLoading(false)
    }
  }

  const simulatorOptions = (simulatorsRes?.data ?? []).map((s) => ({
    value: s.id,
    label: s.name,
  }))

  return (
      <div>
        <div className="mb-6">
          <Button variant="ghost" onClick={() => navigate('/cars')} className="mr-4">
            <ArrowLeft className="w-4 h-4 mr-2" />
            Back to List
          </Button>
          <h1 className="text-2xl font-bold text-gray-900 dark:text-gray-100">
            Add New Car
          </h1>
        </div>

        <Card>
          <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
            {formError && (
                <p className="text-sm text-red-600 dark:text-red-400">{formError}</p>
            )}

            <Select
                label="Simulator"
                options={simulatorOptions}
                placeholder={simulatorsLoading ? 'Loading simulators…' : 'Select a simulator'}
                disabled={simulatorsLoading}
                {...register('simulatorId')}
                error={errors.simulatorId?.message}
            />

            <Input
                label="Name"
                placeholder="e.g., 488 GT3"
                {...register('name')}
                error={errors.name?.message}
            />
            <Input
                label="Brand"
                placeholder="e.g., Ferrari"
                {...register('brand')}
                error={errors.brand?.message}
            />
            <Input
                label="Horsepower (HP)"
                type="number"
                min={1}
                placeholder="e.g., 550"
                {...register('hp')}
                error={errors.hp?.message}
            />
            <Input
                label="Torque (Nm)"
                type="number"
                min={1}
                placeholder="e.g., 700"
                {...register('torque')}
                error={errors.torque?.message}
            />
            <Select
                label="Wheel Drive"
                options={[
                  { value: 'FRONT', label: 'Front wheel drive' },
                  { value: 'REAR', label: 'Rear wheel drive' },
                  { value: 'ALL', label: 'All wheel drive' },
                ]}
                {...register('drive')}
                error={errors.drive?.message}
            />

            {/* fromDlc — checkbox needs Controller because RHF can't register on <input type=checkbox> with a boolean default via register alone */}
            <Controller
                name="fromDlc"
                control={control}
                render={({ field }) => (
                    <label className="flex items-center gap-3 cursor-pointer select-none">
                      <input
                          type="checkbox"
                          checked={field.value}
                          onChange={(e) => field.onChange(e.target.checked)}
                          className="w-4 h-4 rounded border-gray-300 dark:border-gray-600 text-primary-600 focus:ring-primary-500"
                      />
                      <span className="text-sm text-gray-700 dark:text-gray-300">
                  This car comes from a DLC
                </span>
                    </label>
                )}
            />

            <div className="flex justify-end gap-2 pt-4">
              <Button type="submit" isLoading={isLoading} disabled={simulatorsLoading}>
                <Plus className="w-4 h-4 mr-2" />
                Create Car
              </Button>
            </div>
          </form>
        </Card>
      </div>
  )
}