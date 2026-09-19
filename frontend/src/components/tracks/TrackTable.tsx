import { Pencil, Check, X } from 'lucide-react'
import { Card } from '@/components/shared/Card'
import { Table } from '@/components/shared/Table'
import { formatNumber } from '@/utils/format'
import type { TrackDetailResponse } from '@/types/track'

export interface TrackEditForm {
    name: string
    country: string
    lengthKm: string
}

const inlineInputCls =
    'w-full px-2 py-1 rounded-md border border-gray-300 dark:border-gray-600 bg-white dark:bg-gray-800 text-gray-900 dark:text-gray-100 text-sm focus:outline-none focus:ring-2 focus:ring-primary-500'

interface TrackTableProps {
    items: TrackDetailResponse[]
    isAdmin: boolean
    editingId: number | null
    form: TrackEditForm | null
    onFormChange: (form: TrackEditForm) => void
    onBeginEdit: (track: TrackDetailResponse) => void
    onCancelEdit: () => void
    onSave: (id: number) => void
    onRowClick: (track: TrackDetailResponse) => void
    isSaving: boolean
}

export function TrackTable({
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
                           }: TrackTableProps) {
    const isEditing = (id: number) => editingId === id

    return (
        <Card>
            <Table<TrackDetailResponse>
                columns={[
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
                        key: 'country',
                        label: 'Country',
                        render: (v, row) =>
                            isEditing(row.id) && form ? (
                                <input
                                    className={inlineInputCls}
                                    value={form.country}
                                    onChange={(e) => onFormChange({ ...form, country: e.target.value })}
                                />
                            ) : (
                                v ?? '—'
                            ),
                    },
                    {
                        key: 'lengthKm',
                        label: 'Length',
                        render: (v, row) =>
                            isEditing(row.id) && form ? (
                                <input
                                    className={inlineInputCls}
                                    type="number"
                                    step="0.001"
                                    min={0}
                                    value={form.lengthKm}
                                    onChange={(e) => onFormChange({ ...form, lengthKm: e.target.value })}
                                />
                            ) : v != null ? (
                                `${formatNumber(v)} km`
                            ) : (
                                '—'
                            ),
                    },
                    ...(isAdmin
                        ? [
                            {
                                key: 'actions',
                                label: '',
                                render: (_: unknown, row: TrackDetailResponse) =>
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
                emptyMessage="No tracks found"
            />
        </Card>
    )
}