import { create } from 'zustand'

export type ToastType = 'success' | 'error' | 'warning' | 'info'

export interface ToastItem {
    id: string
    type: ToastType
    message: string
    /** ms; 0 = never auto-dismiss */
    duration: number
}

interface ToastState {
    toasts: ToastItem[]
    push: (type: ToastType, message: string, duration?: number) => string
    dismiss: (id: string) => void
    clear: () => void
}

/** Change this one value to set the lifetime of every toast. */
const TOAST_DURATION_MS = 5000

export const useToastStore = create<ToastState>((set) => ({
    toasts: [],

    push: (type, message, duration) => {
        const id = `${Date.now()}-${Math.random().toString(36).slice(2, 8)}`
        set((state) => ({
            toasts: [
                ...state.toasts,
                { id, type, message, duration: duration ?? TOAST_DURATION_MS },
            ].slice(-5),
        }))
        return id
    },

    dismiss: (id) =>
        set((state) => ({ toasts: state.toasts.filter((t) => t.id !== id) })),

    clear: () => set({ toasts: [] }),
}))

export const toast = {
    success: (m: string, d?: number) => useToastStore.getState().push('success', m, d),
    error: (m: string, d?: number) => useToastStore.getState().push('error', m, d),
    warning: (m: string, d?: number) => useToastStore.getState().push('warning', m, d),
    info: (m: string, d?: number) => useToastStore.getState().push('info', m, d),
    dismiss: (id: string) => useToastStore.getState().dismiss(id),
    clear: () => useToastStore.getState().clear(),
}