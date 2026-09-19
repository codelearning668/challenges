import { AlertTriangle } from 'lucide-react'
import { Modal } from './Modal'
import { Button } from './Button'

export interface ConfirmDialogProps {
    isOpen: boolean
    title: string
    message: React.ReactNode
    confirmLabel?: string
    cancelLabel?: string
    /** 'danger' shows a warning icon + red confirm button. */
    variant?: 'default' | 'danger'
    onConfirm: () => void
    onCancel: () => void
}

export function ConfirmDialog({
                                  isOpen,
                                  title,
                                  message,
                                  confirmLabel = 'Confirm',
                                  cancelLabel = 'Cancel',
                                  variant = 'default',
                                  onConfirm,
                                  onCancel,
                              }: ConfirmDialogProps) {
    const isDanger = variant === 'danger'

    return (
        <Modal
            isOpen={isOpen}
            onClose={onCancel}
            title={title}
            size="sm"
            actions={
                <>
                    <Button variant="secondary" onClick={onCancel}>
                        {cancelLabel}
                    </Button>
                    <Button
                        variant={isDanger ? 'danger' : 'primary'}
                        onClick={onConfirm}
                    >
                        {confirmLabel}
                    </Button>
                </>
            }
        >
            <div className="flex items-start gap-3">
                {isDanger && (
                    <div className="shrink-0 w-10 h-10 rounded-full bg-red-50 dark:bg-red-900/30 flex items-center justify-center">
                        <AlertTriangle className="w-5 h-5 text-red-600 dark:text-red-400" />
                    </div>
                )}
                <p className="text-sm text-gray-700 dark:text-gray-300 leading-relaxed">
                    {message}
                </p>
            </div>
        </Modal>
    )
}