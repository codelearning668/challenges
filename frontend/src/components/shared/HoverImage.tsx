import { useState } from 'react'
import { createPortal } from 'react-dom'

interface HoverImageProps {
    src?: string
    alt: string
    children: React.ReactNode
}

export function HoverImage({ src, alt, children }: HoverImageProps) {
    const [isHovered, setIsHovered] = useState(false)
    const [coords, setCoords] = useState({ x: 0, y: 0 })

    // If there's no image (e.g. non-AC simulator), just render the plain text
    if (!src) return <>{children}</>

    return (
        <>
            <span
                onMouseEnter={(e) => {
                    setCoords({ x: e.clientX, y: e.clientY })
                    setIsHovered(true)
                }}
                onMouseMove={(e) => {
                    if (isHovered) setCoords({ x: e.clientX, y: e.clientY })
                }}
                onMouseLeave={() => setIsHovered(false)}
                className="cursor-pointer border-b border-dashed border-gray-400 dark:border-gray-500 hover:text-primary-600 dark:hover:text-primary-400 transition-colors"
            >
                {children}
            </span>
            {isHovered &&
                createPortal(
                    <div
                        className="fixed z-[100] pointer-events-none transform -translate-x-1/2 -translate-y-[calc(100%+12px)]"
                        style={{ left: coords.x, top: coords.y }}
                    >
                        {/* Fixed width and height so neither cars nor tracks can blow up */}
                        <div className="w-72 h-56 bg-black rounded-xl shadow-2xl overflow-hidden border border-gray-700 p-1 flex items-center justify-center">
                            <img
                                src={src}
                                alt={alt}
                                className="w-full h-full rounded-lg object-contain"
                            />
                        </div>
                    </div>,
                    document.body,
                )}
        </>
    )
}