import { ArrowLeft } from 'lucide-react'
import { useNavigate, useParams } from 'react-router-dom'
import { useQuery } from '@tanstack/react-query'
import { carApi } from '@/services/api'
import { Button } from '@/components/shared/Button'
import { Card } from '@/components/shared/Card'
import { formatNumber } from '@/utils/format'

function Field({ label, value }: { label: string; value: React.ReactNode }) {
  return (
      <div>
        <label className="text-sm font-medium text-gray-500 dark:text-gray-400">{label}</label>
        <p className="mt-1 text-gray-900 dark:text-gray-100">{value}</p>
      </div>
  )
}

export function CarDetail() {
  const params = useParams()
  const navigate = useNavigate()
  const carId = Number(params.id)

  const { data, isLoading, isError, error } = useQuery({
    queryKey: ['car', carId],
    queryFn: () => carApi.get(carId),
    enabled: Number.isFinite(carId),
  })

  if (isLoading) return <div className="text-center py-12">Loading...</div>
  if (isError || !data?.data) {
    return (
        <div className="text-center py-12 text-red-600 dark:text-red-400">
          Failed to load car: {error instanceof Error ? error.message : 'not found'}
        </div>
    )
  }

  const car = data.data

  return (
      <div>
        <div className="mb-6">
          <Button variant="ghost" onClick={() => navigate('/cars')} className="mr-4">
            <ArrowLeft className="w-4 h-4 mr-2" />
            Back to List
          </Button>
          <h1 className="text-2xl font-bold text-gray-900 dark:text-gray-100">{car.name}</h1>
        </div>

        <Card>
          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            <Field label="Name" value={car.name} />
            <Field label="Brand" value={car.brand} />
            <Field
                label="Horsepower"
                value={car.horsePower != null ? `${formatNumber(car.horsePower)} hp` : '—'}
            />
            <Field
                label="Torque"
                value={car.torque != null ? `${formatNumber(car.torque)} Nm` : '—'}
            />
            <Field label="Wheel Drive" value={car.wheelDrive ?? '—'} />
          </div>
        </Card>
      </div>
  )
}