import { CalendarClock, Trash2, UserPlus, UserMinus, Timer } from 'lucide-react'
import { Button } from '@/components/shared/Button'

interface ChallengeActionsProps {
    isAdmin: boolean
    active: boolean
    editEndDate: boolean
    canRegister: boolean
    canQuit: boolean
    canEditLapTime: boolean
    onRegister: () => void
    onQuit: () => void
    onUpdateLapTime: () => void
    onBeginEditEndDate: () => void
    onDelete: () => void
    isRegistering: boolean
    isQuitting: boolean
    isDeleting: boolean
}

export function ChallengeActions({
                                     isAdmin,
                                     active,
                                     editEndDate,
                                     canRegister,
                                     canQuit,
                                     canEditLapTime,
                                     onRegister,
                                     onQuit,
                                     onUpdateLapTime,
                                     onBeginEditEndDate,
                                     onDelete,
                                     isRegistering,
                                     isQuitting,
                                     isDeleting,
                                 }: ChallengeActionsProps) {
    return (
        <div className="flex flex-wrap items-center justify-end gap-2 mt-6 pt-6 border-t border-gray-200 dark:border-gray-700">
            {/* Participant actions — left-most when visible */}
            {canRegister && (
                <Button onClick={onRegister} isLoading={isRegistering}>
                    <UserPlus className="w-4 h-4 mr-2" />
                    Register me
                </Button>
            )}
            {canQuit && (
                <Button variant="secondary" onClick={onQuit} isLoading={isQuitting}>
                    <UserMinus className="w-4 h-4 mr-2" />
                    Quit challenge
                </Button>
            )}
            {canEditLapTime && (
                <Button variant="secondary" onClick={onUpdateLapTime}>
                    <Timer className="w-4 h-4 mr-2" />
                    Update my lap time
                </Button>
            )}

            {/* Admin actions — right-most */}
            {isAdmin && active && !editEndDate && (
                <Button variant="secondary" onClick={onBeginEditEndDate}>
                    <CalendarClock className="w-4 h-4 mr-2" />
                    Update end date
                </Button>
            )}
            {isAdmin && active && (
                <Button variant="danger" onClick={onDelete} isLoading={isDeleting}>
                    <Trash2 className="w-4 h-4 mr-2" />
                    Delete challenge
                </Button>
            )}
        </div>
    )
}