import { ImageOff } from 'lucide-react'
import { Modal } from './Modal'

export interface EntityDetailField {
    label: string
    value: React.ReactNode
}

interface EntityDetailDialogProps {
    isOpen: boolean
    onClose: () => void
    title: string
    imageUrl?: string
    fields: EntityDetailField[]
}

export function EntityDetailDialog({
                                       isOpen,
                                       onClose,
                                       title,
                                       imageUrl,
                                       fields,
                                   }: EntityDetailDialogProps) {
    return (
        <Modal isOpen={isOpen} onClose={onClose} title={title} size="lg">
            <div>
                {/* Image area */}
                <div className="w-full h-64 mb-6 rounded-lg bg-gradient-to-br from-gray-100 to-gray-200 dark:from-gray-700 dark:to-gray-800 border border-gray-200 dark:border-gray-700 flex items-center justify-center overflow-hidden">
                    {imageUrl ? (
                        <img src={imageUrl} alt={title} className="w-full h-full object-cover" />
                    ) : (
                        <div className="flex flex-col items-center justify-center text-gray-400 dark:text-gray-500">
                            <ImageOff className="w-12 h-12" />
                            <span className="text-xs mt-2">No image</span>
                        </div>
                    )}
                </div>

                {/* Fields */}
                <div className="grid grid-cols-1 sm:grid-cols-2 gap-x-6 gap-y-4">
                    {fields.map((field) => (
                        <div key={field.label}>
                            <p className="text-xs font-medium uppercase tracking-wide text-gray-500 dark:text-gray-400">
                                {field.label}
                            </p>
                            <p className="mt-1 text-sm font-medium text-gray-900 dark:text-gray-100 break-words">
                                {field.value ?? '—'}
                            </p>
                        </div>
                    ))}
                </div>
            </div>
        </Modal>
    )
}