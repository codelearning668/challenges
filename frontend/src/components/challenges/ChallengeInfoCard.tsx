import { Check, X } from 'lucide-react'
import { ModernDateInput } from '@/components/shared/ModernDateInput'
import { formatDate, formatDurationJson } from '@/utils/format'
import type { ChallengeDetailResponse } from '@/types/challenge'

interface ChallengeInfoCardProps {
    challenge: ChallengeDetailResponse
    editEndDate: boolean
    draftEndDate: string
    onDraftEndDateChange: (value: string) => void
    onSaveEndDate: () => void
    onCancelEditEndDate: () => void
    isSavingEndDate: boolean
}

function Field({ label, value }: { label: string; value: React.ReactNode }) {
    return (
        <div>
            <p className="text-xs font-medium uppercase tracking-wide text-gray-500 dark:text-gray-400">
                {label}
            </p>
            <p className="mt-0.5 text-sm font-medium text-gray-900 dark:text-gray-100">
                {value ?? '—'}
            </p>
        </div>
    )
}

function ColumnHeader({ children }: { children: React.ReactNode }) {
    return (
        <h3 className="text-xs font-semibold uppercase tracking-wide text-primary-600 dark:text-primary-400 mb-3">
            {children}
        </h3>
    )
}

export function ChallengeInfoCard({
                                      challenge: c,
                                      editEndDate,
                                      draftEndDate,
                                      onDraftEndDateChange,
                                      onSaveEndDate,
                                      onCancelEditEndDate,
                                      isSavingEndDate,
                                  }: ChallengeInfoCardProps) {
    return (
        <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
            {/* Column 1 — Result */}
            <div className="space-y-4">
                <ColumnHeader>Result</ColumnHeader>
                <Field label="Best Driver" value={c.bestParticipantName ?? '—'} />
                <Field label="Best Lap" value={formatDurationJson(c.bestLapTime)} />

                <div>
                    <p className="text-xs font-medium uppercase tracking-wide text-gray-500 dark:text-gray-400">
                        End Date
                    </p>
                    {editEndDate ? (
                        <div className="mt-1 flex items-center gap-1.5">
                            <div className="flex-1">
                                <ModernDateInput
                                    value={draftEndDate}
                                    onChange={onDraftEndDateChange}
                                    autoFocus
                                    compact
                                />
                            </div>
                            <button
                                type="button"
                                onClick={onSaveEndDate}
                                disabled={isSavingEndDate}
                                className="p-1.5 rounded-lg hover:bg-green-50 dark:hover:bg-green-900/20 disabled:opacity-50"
                                aria-label="Save end date"
                            >
                                <Check className="w-4 h-4 text-green-600 dark:text-green-400" />
                            </button>
                            <button
                                type="button"
                                onClick={onCancelEditEndDate}
                                className="p-1.5 rounded-lg hover:bg-gray-100 dark:hover:bg-gray-700"
                                aria-label="Cancel"
                            >
                                <X className="w-4 h-4 text-gray-500" />
                            </button>
                        </div>
                    ) : (
                        <p className="mt-0.5 text-sm font-medium text-gray-900 dark:text-gray-100">
                            {formatDate(c.challengeEndDate)}
                        </p>
                    )}
                </div>
            </div>

            {/* Column 2 — Car */}
            <div className="space-y-4 md:border-l md:border-gray-200 md:dark:border-gray-700 md:pl-6">
                <ColumnHeader>Car</ColumnHeader>
                <Field label="Car" value={`${c.carBrand} ${c.carName}`.trim()} />
                <Field
                    label="Horsepower"
                    value={c.carHorsePower != null ? `${c.carHorsePower} hp` : '—'}
                />
                <Field
                    label="Torque"
                    value={c.carTorque != null ? `${c.carTorque} Nm` : '—'}
                />
            </div>

            {/* Column 3 — Track */}
            <div className="space-y-4 md:border-l md:border-gray-200 md:dark:border-gray-700 md:pl-6">
                <ColumnHeader>Track</ColumnHeader>
                <Field
                    label="Track"
                    value={`${c.trackName}${c.trackCountry ? ` (${c.trackCountry})` : ''}`}
                />
                <Field
                    label="Track Length"
                    value={c.trackLengthKm != null ? `${c.trackLengthKm.toFixed(2)} km` : '—'}
                />
            </div>
        </div>
    )
}