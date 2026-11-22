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
    onImageError?: () => void
    fields: EntityDetailField[]
    /** CSS classes for the image (e.g. 'object-contain' or 'object-cover'). */
    imageClassName?: string
    /** Background color for the image container. */
    imageBgClass?: string
    /** Tailwind aspect ratio class for the container (e.g. 'aspect-video'). */
    imageAspect?: string
}

export function EntityDetailDialog({
                                       isOpen,
                                       onClose,
                                       title,
                                       imageUrl,
                                       onImageError,
                                       fields,
                                       imageClassName = 'object-contain',
                                       imageBgClass = 'bg-gray-100 dark:bg-gray-800',
                                       imageAspect = 'aspect-video',
                                   }: EntityDetailDialogProps) {
    return (
        <Modal isOpen={isOpen} onClose={onClose} title={title} size="lg">
            <div>
                {/* Image area */}
                <div className={`w-full mb-6 rounded-lg border border-gray-200 dark:border-gray-700 overflow-hidden ${imageAspect} ${imageBgClass}`}>
                    {imageUrl ? (
                        <img
                            src={imageUrl}
                            alt={title}
                            onError={onImageError}
                            className={`w-full h-full ${imageClassName}`}
                        />
                    ) : (
                        <div className="w-full h-full flex flex-col items-center justify-center text-gray-400 dark:text-gray-500">
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