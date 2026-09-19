import {
    createContext,
    useCallback,
    useContext,
    useRef,
    useState,
} from 'react'
import { ConfirmDialog } from './ConfirmDialog'

export interface ConfirmOptions {
    title: string
    message: React.ReactNode
    confirmLabel?: string
    cancelLabel?: string
    variant?: 'default' | 'danger'
}

type ConfirmFn = (options: ConfirmOptions) => Promise<boolean>

const ConfirmContext = createContext<ConfirmFn | null>(null)

interface ConfirmProviderProps {
    children: React.ReactNode
}

export function ConfirmProvider({ children }: ConfirmProviderProps) {
    const [options, setOptions] = useState<ConfirmOptions | null>(null)
    const resolverRef = useRef<((value: boolean) => void) | null>(null)

    const confirm = useCallback<ConfirmFn>((opts) => {
        return new Promise<boolean>((resolve) => {
            // If a dialog is already open, resolve the previous one as "cancelled"
            // so its caller doesn't hang forever.
            resolverRef.current?.(false)
            resolverRef.current = resolve
            setOptions(opts)
        })
    }, [])

    const resolveAndClose = (value: boolean) => {
        resolverRef.current?.(value)
        resolverRef.current = null
        setOptions(null)
    }

    return (
        <ConfirmContext.Provider value={confirm}>
            {children}
            <ConfirmDialog
                isOpen={options !== null}
                title={options?.title ?? ''}
                message={options?.message ?? ''}
                confirmLabel={options?.confirmLabel}
                cancelLabel={options?.cancelLabel}
                variant={options?.variant}
                onConfirm={() => resolveAndClose(true)}
                onCancel={() => resolveAndClose(false)}
            />
        </ConfirmContext.Provider>
    )
}

export function useConfirm(): ConfirmFn {
    const ctx = useContext(ConfirmContext)
    if (!ctx) {
        throw new Error('useConfirm must be used inside a <ConfirmProvider>')
    }
    return ctx
}