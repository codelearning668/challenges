import { ArrowLeft } from 'lucide-react'
import { useNavigate, useParams } from 'react-router-dom'
import { useQuery } from '@tanstack/react-query'
import { trackApi } from '@/services/api'
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

export function TrackDetail() {
  const params = useParams()
  const navigate = useNavigate()
  const trackId = Number(params.id)

  const { data, isLoading, isError, error } = useQuery({
    queryKey: ['track', trackId],
    queryFn: () => trackApi.get(trackId),
    enabled: Number.isFinite(trackId),
  })

  if (isLoading) return <div className="text-center py-12">Loading...</div>
  if (isError || !data?.data) {
    return (
        <div className="text-center py-12 text-red-600 dark:text-red-400">
          Failed to load track: {error instanceof Error ? error.message : 'not found'}
        </div>
    )
  }

  const track = data.data

  return (
      <div>
        <div className="mb-6">
          <Button variant="ghost" onClick={() => navigate('/tracks')} className="mr-4">
            <ArrowLeft className="w-4 h-4 mr-2" />
            Back to List
          </Button>
          <h1 className="text-2xl font-bold text-gray-900 dark:text-gray-100">{track.name}</h1>
        </div>

        <Card>
          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            <Field label="Name" value={track.name} />
            <Field label="Country" value={track.country ?? '—'} />
            <Field
                label="Length"
                value={track.lengthKm != null ? `${formatNumber(track.lengthKm)} km` : '—'}
            />
          </div>
        </Card>
      </div>
  )
}