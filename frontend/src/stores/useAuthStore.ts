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
      
      login: (user, authorizationHeader) => set({ 
        user, 
        isAuthenticated: true,
        authorizationHeader,
      }),
      
      logout: () => set({ 
        user: null, 
        isAuthenticated: false,
        authorizationHeader: null,
      }),
      
      registerUser: async (username, password) => {
        console.log('Registering user:', username, password)
        const newUser = { username, authorities: ['PARTICIPANT'] }
        set({ user: newUser, isAuthenticated: true })
      },
    }),
    {
      name: 'auth',
      partialize: (state) => ({
        user: state.user,
        isAuthenticated: state.isAuthenticated,
        authorizationHeader: state.authorizationHeader,
      }),
    }
  )
)
