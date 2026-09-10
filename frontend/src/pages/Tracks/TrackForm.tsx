import { useState } from 'react'
import { ArrowLeft, Plus } from 'lucide-react'
import { useNavigate } from 'react-router-dom'
import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { trackSchema, type TrackFormData } from '@/utils/validators'
import { Button } from '@/components/shared/Button'
import { Input } from '@/components/shared/Input'
import { Card } from '@/components/shared/Card'
import { trackApi } from '@/services/api'

export function TrackForm() {
  const navigate = useNavigate()
  const [isLoading, setIsLoading] = useState(false)
  const [formError, setFormError] = useState<string | null>(null)

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<TrackFormData>({ resolver: zodResolver(trackSchema) })

  const onSubmit = async (data: TrackFormData) => {
    try {
      setIsLoading(true)
      setFormError(null)
      await trackApi.create({
        name: data.name,
        country: data.country,
        lengthKm: data.lengthKm,
      })
      navigate('/tracks')
    } catch (err) {
      setFormError(err instanceof Error ? err.message : 'Failed to create track')
    } finally {
      setIsLoading(false)
    }
  }

  return (
      <div>
        <div className="mb-6">
          <Button variant="ghost" onClick={() => navigate('/tracks')} className="mr-4">
            <ArrowLeft className="w-4 h-4 mr-2" />
            Back to List
          </Button>
          <h1 className="text-2xl font-bold text-gray-900 dark:text-gray-100">Add New Track</h1>
        </div>

        <Card>
          <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
            {formError && <p className="text-sm text-red-600 dark:text-red-400">{formError}</p>}

            <Input
                label="Name"
                placeholder="e.g., Monza Circuit"
                {...register('name')}
                error={errors.name?.message}
            />
            <Input
                label="Country"
                placeholder="e.g., Italy"
                {...register('country')}
                error={errors.country?.message}
            />
            <Input
                label="Length (km)"
                type="number"
                step="0.001"
                min={0}
                placeholder="e.g., 5.793"
                {...register('lengthKm')}
                error={errors.lengthKm?.message}
            />

            <div className="flex justify-end gap-2 pt-4">
              <Button type="submit" isLoading={isLoading}>
                <Plus className="w-4 h-4 mr-2" />
                Create Track
              </Button>
            </div>
          </form>
        </Card>
      </div>
  )
}