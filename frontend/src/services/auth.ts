import { useAuthStore } from '@/stores/useAuthStore'
import type { UserInfoResponse } from '@/types/user'

const stripRolePrefix = (authority: string): string =>
    authority.startsWith('ROLE_') ? authority.slice(5) : authority

const normalizeAuthorities = (roles: unknown): string[] => {
  if (!Array.isArray(roles)) return []
  return roles
      .filter((r): r is string => typeof r === 'string')
      .map(stripRolePrefix)
}

export const authApi = {
  login: async (username: string, password: string) => {
    const authorizationHeader = `Basic ${btoa(`${username}:${password}`)}`

    const response = await fetch('/api/users/info', {
      headers: { Authorization: authorizationHeader },
    })

    if (!response.ok) throw new Error('Authentication failed')

    const data: UserInfoResponse = await response.json()
    const authorities = normalizeAuthorities(data?.roles)

    if (import.meta.env.DEV) {
      // Dev-only: lets you confirm what the app actually stored after login.
      // Open DevTools → Console, look for "auth.login: stored".
      console.info('auth.login: stored', {
        username: data?.username ?? username,
        rawRoles: data?.roles,
        authorities,
      })
    }

    return {
      user: {
        username: data?.username ?? username,
        authorities,
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