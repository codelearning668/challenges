import { create } from 'zustand'
import { persist } from 'zustand/middleware'

interface User {
    username: string
    authorities: string[]
}

interface AuthState {
    user: User | null
    isAuthenticated: boolean
    authorizationHeader: string | null
    login: (user: User, authorizationHeader: string) => void
    logout: () => void
    registerUser: (username: string, password: string) => Promise<void>
}

export const useAuthStore = create<AuthState>()(
    persist(
        (set) => ({
            user: null,
            isAuthenticated: false,
            authorizationHeader: null,

            login: (user, authorizationHeader) =>
                set({
                    user,
                    isAuthenticated: true,
                    authorizationHeader,
                }),

            logout: () =>
                set({
                    user: null,
                    isAuthenticated: false,
                    authorizationHeader: null,
                }),

            registerUser: async (username, password) => {
                // Unused — the register flow goes through `useAuth().register`.
                console.warn('registerUser called on the store; use useAuth().register instead.', {
                    username,
                    password,
                })
            },
        }),
        {
            name: 'auth',
            partialize: (state) => ({
                user: state.user,
                isAuthenticated: state.isAuthenticated,
                authorizationHeader: state.authorizationHeader,
            }),
        },
    ),
)