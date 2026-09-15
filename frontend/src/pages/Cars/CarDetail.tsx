import { useState } from 'react'
import { ArrowLeft, Pencil } from 'lucide-react'
import { useNavigate, useParams } from 'react-router-dom'
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { carApi } from '@/services/api'
import { Button } from '@/components/shared/Button'
import { Card } from '@/components/shared/Card'
import { Input } from '@/components/shared/Input'
import { Select } from '@/components/shared/Select'
import { formatNumber } from '@/utils/format'
import { usePermissions } from '@/hooks/usePermissions'
import { toast } from '@/stores/useToastStore'
import type { CarDetailResponse, WheelDrive } from '@/types/car'

interface FormState {
    brand: string
    name: string
    hp: string
    torque: string
    drive: WheelDrive | ''
}

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
    const queryClient = useQueryClient()
    const carId = Number(params.id)
    const { isAdmin } = usePermissions()

    const [editMode, setEditMode] = useState(false)
    const [form, setForm] = useState<FormState | null>(null)
    const [formError, setFormError] = useState<string | null>(null)

    const { data, isLoading, isError, error } = useQuery({
        queryKey: ['car', carId],
        queryFn: () => carApi.get(carId),
        enabled: Number.isFinite(carId),
    })

    const updateMutation = useMutation({
        mutationFn: (payload: FormState) =>
            carApi.update(carId, {
                brand: payload.brand.trim(),
                name: payload.name.trim(),
                hp: payload.hp === '' ? undefined : Number(payload.hp),
                torque: payload.torque === '' ? undefined : Number(payload.torque),
                drive: payload.drive === '' ? undefined : payload.drive,
            }),
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: ['car', carId] })
            queryClient.invalidateQueries({ queryKey: ['cars'] })
            toast.success('Car updated')
            setEditMode(false)
            setFormError(null)
        },
        onError: (err) => {
            setFormError(err instanceof Error ? err.message : 'Failed to update car')
        },
    })

    const beginEdit = (car: CarDetailResponse) => {
        setForm({
            brand: car.brand ?? '',
            name: car.name ?? '',
            hp: car.horsePower != null ? String(car.horsePower) : '',
            torque: car.torque != null ? String(car.torque) : '',
            drive: car.wheelDrive ?? '',
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
        if (!form.brand.trim() || !form.name.trim()) {
            setFormError('Brand and Name are required')
            return
        }
        updateMutation.mutate(form)
    }

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
                <div className="inline-flex items-center gap-2">
                    <h1 className="text-2xl font-bold text-gray-900 dark:text-gray-100">{car.name}</h1>
                    {isAdmin && !editMode && (
                        <button
                            type="button"
                            onClick={() => beginEdit(car)}
                            className="p-1.5 rounded-lg text-gray-500 dark:text-gray-400 hover:bg-gray-100 dark:hover:bg-gray-700"
                            aria-label="Edit car"
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
                                label="Brand"
                                value={form.brand}
                                onChange={(e) => setForm({ ...form, brand: e.target.value })}
                            />
                            <Input
                                label="Name"
                                value={form.name}
                                onChange={(e) => setForm({ ...form, name: e.target.value })}
                            />
                            <Input
                                label="Horsepower (HP)"
                                type="number"
                                min={0}
                                value={form.hp}
                                onChange={(e) => setForm({ ...form, hp: e.target.value })}
                            />
                            <Input
                                label="Torque (Nm)"
                                type="number"
                                min={0}
                                value={form.torque}
                                onChange={(e) => setForm({ ...form, torque: e.target.value })}
                            />
                            <Select
                                label="Wheel Drive"
                                value={form.drive}
                                options={[
                                    { value: 'FRONT', label: 'Front wheel drive' },
                                    { value: 'REAR', label: 'Rear wheel drive' },
                                    { value: 'ALL', label: 'All wheel drive' },
                                ]}
                                onChange={(e) =>
                                    setForm({ ...form, drive: e.target.value as WheelDrive | '' })
                                }
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
            )}
        </div>
    )
}