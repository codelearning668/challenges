import axios, { AxiosError } from 'axios'
import { useAuthStore } from '@/stores/useAuthStore'
import { toast } from '@/stores/useToastStore'
import type {
  CarDetailResponse,
  CreateCarRequest,
  UpdateCarRequest,
  SearchCarsCriteria,
} from '@/types/car'
import type {
  TrackDetailResponse,
  CreateTrackRequest,
  UpdateTrackRequest,
  SearchTracksCriteria,
} from '@/types/track'
import type {
  ChallengeSummaryResponse,
  ChallengeDetailResponse,
  CreateChallengeRequest,
  UpdateChallengeEndDateRequest,
  UpdateLapTimeRequest,
  SearchChallengesCriteria,
} from '@/types/challenge'
import type { UserInfoResponse, UserRegistrationRequest } from '@/types/user'

const API_BASE = import.meta.env.DEV ? '/api' : (import.meta.env.VITE_API_URL || '/api')

declare module 'axios' {
  export interface AxiosRequestConfig {
    /** When true, no toast is shown for errors from this request. */
    silent?: boolean
  }
}

const api = axios.create({
  baseURL: API_BASE,
  headers: { 'Content-Type': 'application/json' },
  timeout: 10_000,
})

api.interceptors.request.use((config) => {
  const authorizationHeader = useAuthStore.getState().authorizationHeader
  if (authorizationHeader) config.headers.Authorization = authorizationHeader
  return config
})

function describeError(error: AxiosError): string {
  if (error.code === 'ECONNABORTED' || error.code === 'ETIMEDOUT') {
    return 'Request timed out. Check that the backend is running.'
  }
  if (!error.response) {
    return 'Network error. The backend is unreachable.'
  }

  const { status, data } = error.response
  const serverMessage =
      (data && typeof data === 'object' && ('message' in data || 'error' in data)
          ? ((data as { message?: string; error?: string }).message ??
              (data as { error?: string }).error)
          : null) ?? (typeof data === 'string' ? data : null)

  if (serverMessage) return String(serverMessage)

  switch (status) {
    case 400: return 'Invalid request.'
    case 401: return 'Not authenticated.'
    case 403: return 'You don’t have permission to do that.'
    case 404: return 'Not found.'
    case 409: return 'That conflicts with existing data.'
    case 500: return 'Server error.'
    default: return `Request failed (${status}).`
  }
}

api.interceptors.response.use(
    (response) => response,
    (error: AxiosError) => {
      const status = error.response?.status
      const url = error.config?.url ?? ''
      const silent = error.config?.silent === true

      if (status === 401) {
        useAuthStore.getState().logout()
      }

      // The Login page shows the credential failure inline, so don't also toast it.
      const isLoginProbe = url.endsWith('/users/info')

      if (!silent && !isLoginProbe) {
        toast.error(describeError(error))
      }

      return Promise.reject(error)
    },
)

export interface SearchParams {
  page?: number
  size?: number
  sortBy?: string
  sortOrder?: 'asc' | 'desc'
}

export const carApi = {
  search: (criteria: SearchCarsCriteria = {}) =>
      api.get<CarDetailResponse[]>('/cars', { params: criteria }),

  get: (carId: number) => api.get<CarDetailResponse>(`/cars/${carId}`),

  create: (body: CreateCarRequest) => api.post<string>('/cars', body),

  update: (carId: number, body: UpdateCarRequest) =>
      api.put<void>(`/cars/${carId}`, body),
}

export const trackApi = {
  search: (criteria: SearchTracksCriteria = {}) =>
      api.get<TrackDetailResponse[]>('/tracks', { params: criteria }),

  get: (trackId: number) => api.get<TrackDetailResponse>(`/tracks/${trackId}`),

  create: (body: CreateTrackRequest) => api.post<string>('/tracks', body),

  update: (trackId: number, body: UpdateTrackRequest) =>
      api.put<void>(`/tracks/${trackId}`, body),
}

export const challengeApi = {
  search: (criteria: SearchChallengesCriteria = {}) =>
      api.get<ChallengeSummaryResponse[]>('/challenges', { params: criteria }),

  get: (challengeId: number) =>
      api.get<ChallengeDetailResponse>(`/challenges/${challengeId}`),

  create: (body: CreateChallengeRequest) => api.post<string>('/challenges', body),

  updateEndDate: (challengeId: number, body: UpdateChallengeEndDateRequest) =>
      api.put<void>(`/challenges/${challengeId}`, body),

  delete: (challengeId: number) =>
      api.delete<void>(`/challenges/${challengeId}`),

  register: (challengeId: number) =>
      api.post<void>(`/challenges/${challengeId}/register`),

  updateLapTime: (challengeId: number, body: UpdateLapTimeRequest) =>
      api.put<void>(`/challenges/${challengeId}/participant`, body),
}

export const userApi = {
  register: async (username: string, password: string) => {
    const body: UserRegistrationRequest = { username, password }
    await api.post<void>('/users/register', body)
  },

  info: (authorizationHeader: string) =>
      api.get<UserInfoResponse>('/users/info', {
        headers: { Authorization: authorizationHeader },
      }),
}

export default api