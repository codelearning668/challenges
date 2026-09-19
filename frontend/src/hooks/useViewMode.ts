import { useEffect, useState } from 'react'

export type ViewMode = 'table' | 'grid'

const STORAGE_PREFIX = 'view-mode:'

export function useViewMode(key: string, defaultMode: ViewMode = 'table') {
    const storageKey = `${STORAGE_PREFIX}${key}`

    const [mode, setMode] = useState<ViewMode>(() => {
        if (typeof window === 'undefined') return defaultMode
        const stored = window.localStorage.getItem(storageKey)
        return stored === 'grid' || stored === 'table' ? stored : defaultMode
    })

    useEffect(() => {
        window.localStorage.setItem(storageKey, mode)
    }, [storageKey, mode])

    return [mode, setMode] as const
}