import { useState } from 'react'
import { ArrowLeft, Pencil } from 'lucide-react'
import { useNavigate, useParams } from 'react-router-dom'
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { trackApi } from '@/services/api'
import { Button } from '@/components/shared/Button'
import { Card } from '@/components/shared/Card'
import { Input } from '@/components/shared/Input'
import { formatNumber } from '@/utils/format'
import { usePermissions } from '@/hooks/usePermissions'
import { toast } from '@/stores/useToastStore'
import type { TrackDetailResponse } from '@/types/track'

interface FormState {
    name: string
    country: string
    lengthKm: string
}

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
    const queryClient = useQueryClient()
    const trackId = Number(params.id)
    const { isAdmin } = usePermissions()

    const [editMode, setEditMode] = useState(false)
    const [form, setForm] = useState<FormState | null>(null)
    const [formError, setFormError] = useState<string | null>(null)

    const { data, isLoading, isError, error } = useQuery({
        queryKey: ['track', trackId],
        queryFn: () => trackApi.get(trackId),
        enabled: Number.isFinite(trackId),
    })

    const updateMutation = useMutation({
        mutationFn: (payload: FormState) =>
            trackApi.update(trackId, {
                name: payload.name.trim(),
                country: payload.country.trim() || undefined,
                lengthKm: payload.lengthKm === '' ? undefined : Number(payload.lengthKm),
            }),
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: ['track', trackId] })
            queryClient.invalidateQueries({ queryKey: ['tracks'] })
            toast.success('Track updated')
            setEditMode(false)
            setFormError(null)
        },
        onError: (err) => {
            setFormError(err instanceof Error ? err.message : 'Failed to update track')
        },
    })

    const beginEdit = (track: TrackDetailResponse) => {
        setForm({
            name: track.name ?? '',
            country: track.country ?? '',
            lengthKm: track.lengthKm != null ? String(track.lengthKm) : '',
        })
        setFormError(null)
        setEditMode(true)
    }

    const cancelEdit = () => {
        setEditMode(false)
        setForm(null)
        setFormError(null)
    }

    const handleSave = (e: React.FormEvent) => {
        e.preventDefault()
        if (!form) return
        if (!form.name.trim()) {
            setFormError('Name is required')
            return
        }
        updateMutation.mutate(form)
    }

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
                <div className="inline-flex items-center gap-2">
                    <h1 className="text-2xl font-bold text-gray-900 dark:text-gray-100">{track.name}</h1>
                    {isAdmin && !editMode && (
                        <button
                            type="button"
                            onClick={() => beginEdit(track)}
                            className="p-1.5 rounded-lg text-gray-500 dark:text-gray-400 hover:bg-gray-100 dark:hover:bg-gray-700"
                            aria-label="Edit track"
                        >
                            <Pencil className="w-4 h-4" />
                        </button>
                    )}
                </div>
            </div>

            {editMode && form ? (
                <Card>
                    <form onSubmit={handleSave} className="space-y-4">
                        {formError && (
                            <p className="text-sm text-red-600 dark:text-red-400">{formError}</p>
                        )}

                        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                            <Input
                                label="Name"
                                value={form.name}
                                onChange={(e) => setForm({ ...form, name: e.target.value })}
                            />
                            <Input
                                label="Country"
                                value={form.country}
                                onChange={(e) => setForm({ ...form, country: e.target.value })}
                            />
                            <Input
                                label="Length (km)"
                                type="number"
                                step="0.001"
                                min={0}
                                value={form.lengthKm}
                                onChange={(e) => setForm({ ...form, lengthKm: e.target.value })}
                            />
                        </div>

                        <div className="flex justify-end gap-2 pt-4">
                            <Button type="submit" isLoading={updateMutation.isPending}>
                                Save Changes
                            </Button>
                            <Button type="button" variant="secondary" onClick={cancelEdit}>
                                Cancel
                            </Button>
                        </div>
                    </form>
                </Card>
            ) : (
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
            )}
        </div>
    )
}