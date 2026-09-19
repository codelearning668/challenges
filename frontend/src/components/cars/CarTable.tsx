import { Pencil, Check, X } from 'lucide-react'
import { Card } from '@/components/shared/Card'
import { Table } from '@/components/shared/Table'
import { formatNumber } from '@/utils/format'
import type { CarDetailResponse, WheelDrive } from '@/types/car'

export interface CarEditForm {
    brand: string
    name: string
    hp: string
    torque: string
    drive: WheelDrive | ''
}

export const inlineInputCls =
    'w-full px-2 py-1 rounded-md border border-gray-300 dark:border-gray-600 bg-white dark:bg-gray-800 text-gray-900 dark:text-gray-100 text-sm focus:outline-none focus:ring-2 focus:ring-primary-500'

interface CarTableProps {
    items: CarDetailResponse[]
    isAdmin: boolean
    editingId: number | null
    form: CarEditForm | null
    onFormChange: (form: CarEditForm) => void
    onBeginEdit: (car: CarDetailResponse) => void
    onCancelEdit: () => void
    onSave: (id: number) => void
    onRowClick: (car: CarDetailResponse) => void
    isSaving: boolean
}

export function CarTable({
                             items,
                             isAdmin,
                             editingId,
                             form,
                             onFormChange,
                             onBeginEdit,
                             onCancelEdit,
                             onSave,
                             onRowClick,
                             isSaving,
                         }: CarTableProps) {
    const isEditing = (id: number) => editingId === id

    return (
        <Card>
            <Table<CarDetailResponse>
                columns={[
                    {
                        key: 'brand',
                        label: 'Brand',
                        render: (v, row) =>
                            isEditing(row.id) && form ? (
                                <input
                                    className={inlineInputCls}
                                    value={form.brand}
                                    onChange={(e) => onFormChange({ ...form, brand: e.target.value })}
                                />
                            ) : (
                                v
                            ),
                    },
                    {
                        key: 'name',
                        label: 'Name',
                        render: (v, row) =>
                            isEditing(row.id) && form ? (
                                <input
                                    className={inlineInputCls}
                                    value={form.name}
                                    onChange={(e) => onFormChange({ ...form, name: e.target.value })}
                                />
                            ) : (
                                v
                            ),
                    },
                    {
                        key: 'horsePower',
                        label: 'Horsepower',
                        render: (v, row) =>
                            isEditing(row.id) && form ? (
                                <input
                                    className={inlineInputCls}
                                    type="number"
                                    min={0}
                                    value={form.hp}
                                    onChange={(e) => onFormChange({ ...form, hp: e.target.value })}
                                />
                            ) : v != null ? (
                                `${formatNumber(v)} hp`
                            ) : (
                                '—'
                            ),
                    },
                    {
                        key: 'torque',
                        label: 'Torque',
                        render: (v, row) =>
                            isEditing(row.id) && form ? (
                                <input
                                    className={inlineInputCls}
                                    type="number"
                                    min={0}
                                    value={form.torque}
                                    onChange={(e) => onFormChange({ ...form, torque: e.target.value })}
                                />
                            ) : v != null ? (
                                `${formatNumber(v)} Nm`
                            ) : (
                                '—'
                            ),
                    },
                    {
                        key: 'wheelDrive',
                        label: 'Drive',
                        render: (v, row) =>
                            isEditing(row.id) && form ? (
                                <select
                                    className={inlineInputCls}
                                    value={form.drive}
                                    onChange={(e) =>
                                        onFormChange({ ...form, drive: e.target.value as WheelDrive | '' })
                                    }
                                >
                                    <option value="">—</option>
                                    <option value="FRONT">FRONT</option>
                                    <option value="REAR">REAR</option>
                                    <option value="ALL">ALL</option>
                                </select>
                            ) : (
                                v ?? '—'
                            ),
                    },
                    ...(isAdmin
                        ? [
                            {
                                key: 'actions',
                                label: '',
                                render: (_: unknown, row: CarDetailResponse) =>
                                    isEditing(row.id) ? (
                                        <div className="flex items-center justify-end gap-1">
                                            <button
                                                type="button"
                                                onClick={() => onSave(row.id)}
                                                disabled={isSaving}
                                                className="p-1.5 rounded-lg hover:bg-green-50 dark:hover:bg-green-900/20 disabled:opacity-50"
                                                aria-label="Save"
                                            >
                                                <Check className="w-4 h-4 text-green-600 dark:text-green-400" />
                                            </button>
                                            <button
                                                type="button"
                                                onClick={onCancelEdit}
                                                className="p-1.5 rounded-lg hover:bg-gray-100 dark:hover:bg-gray-700"
                                                aria-label="Cancel"
                                            >
                                                <X className="w-4 h-4 text-gray-500" />
                                            </button>
                                        </div>
                                    ) : (
                                        <div className="flex items-center justify-end">
                                            <button
                                                type="button"
                                                onClick={(e) => {
                                                    e.stopPropagation()
                                                    onBeginEdit(row)
                                                }}
                                                className="p-1.5 rounded-lg hover:bg-gray-100 dark:hover:bg-gray-700"
                                                aria-label="Edit"
                                            >
                                                <Pencil className="w-4 h-4 text-gray-500" />
                                            </button>
                                        </div>
                                    ),
                            },
                        ]
                        : []),
                ]}
                rows={items}
                onRowClick={onRowClick}
                emptyMessage="No cars found"
            />
        </Card>
    )
}