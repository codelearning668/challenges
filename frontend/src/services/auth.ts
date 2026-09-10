import { useAuthStore } from '@/stores/useAuthStore'

export const authApi = {
  login: async (username: string, password: string) => {
    const authorizationHeader = `Basic ${btoa(`${username}:${password}`)}`

    // The backend exposes no protected GET endpoint (all GETs are public)
    // and no /users/me endpoint, so credentials cannot be verified here and
    // authorities cannot be fetched. We accept the credentials optimistically
    // and let the first protected write (POST/PUT/DELETE) reveal a 401,
    // which the api.ts response interceptor turns into a logout.
    return {
      user: {
        username,
        // Unknown until a write is attempted; grant both so the UI is usable.
        // The backend still enforces real authorization on writes.
        authorities: ['ADMIN', 'PARTICIPANT'],
      },
      authorizationHeader,
    }
  },

  register: async (username: string, password: string) => {
    const response = await fetch('/api/users/register', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ username, password }),
    })
    if (!response.ok) throw new Error('Registration failed')
  },
}

export const useAuth = () => {
  const { login, logout } = useAuthStore()

  return {
    login: async (username: string, password: string) => {
      const { user, authorizationHeader } = await authApi.login(username, password)
      login(user, authorizationHeader)
      return user
    },

    register: async (username: string, password: string) => {
      await authApi.register(username, password)
      const { user, authorizationHeader } = await authApi.login(username, password)
      login(user, authorizationHeader)
    },

    logout,
  }
}