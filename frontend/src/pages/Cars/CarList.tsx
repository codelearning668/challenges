import { useState } from 'react'
import { keepPreviousData, useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { useNavigate } from 'react-router-dom'
import { carApi } from '@/services/api'
import { Card } from '@/components/shared/Card'
import { ListPageHeader } from '@/components/shared/ListPageHeader'
import { ListSearchBar } from '@/components/shared/ListSearchBar'
import { EntityDetailDialog } from '@/components/shared/EntityDetailDialog'
import { CarFilters, EMPTY_CAR_FILTERS, type CarFiltersState } from '@/components/cars/CarFilters'
import { CarTable, type CarEditForm } from '@/components/cars/CarTable'
import { CarGrid } from '@/components/cars/CarGrid'
import { formatNumber } from '@/utils/format'
import { usePermissions } from '@/hooks/usePermissions'
import { useDebouncedValue } from '@/hooks/useDebouncedValue'
import { useViewMode } from '@/hooks/useViewMode'
import { toast } from '@/stores/useToastStore'
import type { CarDetailResponse, SearchCarsCriteria } from '@/types/car'

export function CarList() {
    const navigate = useNavigate()
    const queryClient = useQueryClient()
    const { isAdmin } = usePermissions()
    const [viewMode, setViewMode] = useViewMode('cars', 'table')

    const [search, setSearch] = useState('')
    const [filters, setFilters] = useState<CarFiltersState>(EMPTY_CAR_FILTERS)
    const [showFilters, setShowFilters] = useState(false)

    const [editingId, setEditingId] = useState<number | null>(null)
    const [form, setForm] = useState<CarEditForm | null>(null)
    const [selectedCar, setSelectedCar] = useState<CarDetailResponse | null>(null)

    const dSearch = useDebouncedValue(search, 300)
    const dBrand = useDebouncedValue(filters.brand, 300)
    const dHp = useDebouncedValue(filters.horsePower, 300)
    const dTorque = useDebouncedValue(filters.torque, 300)

    const criteria: SearchCarsCriteria = {
        ...(dSearch.trim() ? { name: dSearch.trim() } : {}),
        ...(dBrand.trim() ? { brand: dBrand.trim() } : {}),
        ...(dHp.trim() && Number.isFinite(Number(dHp)) ? { horsePower: Number(dHp) } : {}),
        ...(dTorque.trim() && Number.isFinite(Number(dTorque)) ? { torque: Number(dTorque) } : {}),
        ...(filters.wheelDrive ? { wheelDrive: filters.wheelDrive } : {}),
    }
    const criteriaKey = JSON.stringify(criteria)

    const { data, isLoading, isFetching, isError, error } = useQuery({
        queryKey: ['cars', criteriaKey],
        queryFn: () => carApi.search(criteria),
        placeholderData: keepPreviousData,
        refetchOnMount: 'always',
    })

    const items: CarDetailResponse[] = [...(data?.data ?? [])].sort((a, b) =>
        `${a.brand} ${a.name}`.localeCompare(`${b.brand} ${b.name}`),
    )

    const updateMutation = useMutation({
        mutationFn: (vars: { id: number; body: CarEditForm }) =>
            carApi.update(vars.id, {
                brand: vars.body.brand.trim(),
                name: vars.body.name.trim(),
                hp: vars.body.hp === '' ? undefined : Number(vars.body.hp),
                torque: vars.body.torque === '' ? undefined : Number(vars.body.torque),
                drive: vars.body.drive === '' ? undefined : vars.body.drive,
            }),
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: ['cars'] })
            toast.success('Car updated')
            cancelEdit()
        },
    })

    const beginEdit = (car: CarDetailResponse) => {
        setEditingId(car.id)
        setForm({
            brand: car.brand ?? '',
            name: car.name ?? '',
            hp: car.horsePower != null ? String(car.horsePower) : '',
            torque: car.torque != null ? String(car.torque) : '',
            drive: car.wheelDrive ?? '',
        })
    }

    const cancelEdit = () => {
        setEditingId(null)
        setForm(null)
    }

    const save = (id: number) => {
        if (!form) return
        if (!form.brand.trim() || !form.name.trim()) {
            toast.error('Brand and Name are required')
            return
        }
        updateMutation.mutate({ id, body: form })
    }

    const openDetail = (car: CarDetailResponse) => {
        if (editingId !== null) return
        setSelectedCar(car)
    }

    const activeFilterCount = Object.values(filters).filter((v) => v !== '').length
    const isFiltering = dSearch.trim().length > 0 || activeFilterCount > 0
    const firstLoad = isLoading && !data

    return (
        <div>
            <ListPageHeader
                title="Cars"
                viewMode={viewMode}
                onViewModeChange={setViewMode}
                showAdd={isAdmin}
                onAdd={() => navigate('/cars/new')}
                addLabel="Add Car"
            />

            <div className="mb-4 space-y-3">
                <ListSearchBar
                    value={search}
                    onChange={setSearch}
                    placeholder="Search cars by name..."
                    showSpinner={isFetching && !firstLoad && isFiltering}
                    filtersOpen={showFilters}
                    activeFilterCount={activeFilterCount}
                    onToggleFilters={() => setShowFilters((v) => !v)}
                />
                <CarFilters
                    isOpen={showFilters}
                    filters={filters}
                    onChange={setFilters}
                    onClear={() => setFilters(EMPTY_CAR_FILTERS)}
                    activeCount={activeFilterCount}
                />
            </div>

            {isError ? (
                <Card>
                    <p className="text-center text-red-600 dark:text-red-400 py-8">
                        Failed to load cars: {error instanceof Error ? error.message : 'unknown error'}
                    </p>
                </Card>
            ) : firstLoad ? (
                <Card>
                    <p className="text-center text-gray-500 dark:text-gray-400 py-8">Loading...</p>
                </Card>
            ) : items.length === 0 ? (
                <Card>
                    <p className="text-center text-gray-500 dark:text-gray-400 py-8">No cars found</p>
                </Card>
            ) : viewMode === 'grid' ? (
                <CarGrid items={items} onItemClick={openDetail} />
            ) : (
                <CarTable
                    items={items}
                    isAdmin={isAdmin}
                    editingId={editingId}
                    form={form}
                    onFormChange={setForm}
                    onBeginEdit={beginEdit}
                    onCancelEdit={cancelEdit}
                    onSave={save}
                    onRowClick={openDetail}
                    isSaving={updateMutation.isPending}
                />
            )}

            <EntityDetailDialog
                isOpen={selectedCar !== null}
                onClose={() => setSelectedCar(null)}
                title={selectedCar ? `${selectedCar.brand} ${selectedCar.name}` : ''}
                fields={
                    selectedCar
                        ? [
                            { label: 'Brand', value: selectedCar.brand },
                            { label: 'Name', value: selectedCar.name },
                            {
                                label: 'Horsepower',
                                value: selectedCar.horsePower != null
                                    ? `${formatNumber(selectedCar.horsePower)} hp`
                                    : '—',
                            },
                            {
                                label: 'Torque',
                                value: selectedCar.torque != null
                                    ? `${formatNumber(selectedCar.torque)} Nm`
                                    : '—',
                            },
                            { label: 'Wheel Drive', value: selectedCar.wheelDrive ?? '—' },
                        ]
                        : []
                }
            />
        </div>
    )
}