import { useState } from 'react'
import { ArrowLeft, Plus } from 'lucide-react'
import { useNavigate } from 'react-router-dom'
import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { carSchema, type CarFormData } from '@/utils/validators'
import { Button } from '@/components/shared/Button'
import { Input } from '@/components/shared/Input'
import { Select } from '@/components/shared/Select'
import { Card } from '@/components/shared/Card'
import { carApi } from '@/services/api'

export function CarForm() {
  const navigate = useNavigate()
  const [isLoading, setIsLoading] = useState(false)
  const [formError, setFormError] = useState<string | null>(null)

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<CarFormData>({ resolver: zodResolver(carSchema) })

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
      })
      navigate('/cars')
    } catch (err) {
      setFormError(err instanceof Error ? err.message : 'Failed to create car')
    } finally {
      setIsLoading(false)
    }
  }

  return (
      <div>
        <div className="mb-6">
          <Button variant="ghost" onClick={() => navigate('/cars')} className="mr-4">
            <ArrowLeft className="w-4 h-4 mr-2" />
            Back to List
          </Button>
          <h1 className="text-2xl font-bold text-gray-900 dark:text-gray-100">Add New Car</h1>
        </div>

        <Card>
          <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
            {formError && <p className="text-sm text-red-600 dark:text-red-400">{formError}</p>}

            <Input label="Name" placeholder="e.g., 037" {...register('name')} error={errors.name?.message} />
            <Input label="Brand" placeholder="e.g., Lancia" {...register('brand')} error={errors.brand?.message} />
            <Input
                label="Horsepower (HP)"
                type="number"
                min={1}
                placeholder="e.g., 280"
                {...register('hp')}
                error={errors.hp?.message}
            />
            <Input
                label="Torque (Nm)"
                type="number"
                min={1}
                placeholder="e.g., 320"
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

            <div className="flex justify-end gap-2 pt-4">
              <Button type="submit" isLoading={isLoading}>
                <Plus className="w-4 h-4 mr-2" />
                Create Car
              </Button>
            </div>
          </form>
        </Card>
      </div>
  )
}