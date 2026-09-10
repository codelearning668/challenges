import axios from 'axios'
import { useAuthStore } from '@/stores/useAuthStore'
import type {
  CarDetailResponse,
  CreateCarRequest,
  SearchCarsCriteria,
} from '@/types/car'
import type {
  TrackDetailResponse,
  CreateTrackRequest,
  SearchTracksCriteria,
} from '@/types/track'
import type {
  ChallengeSummaryResponse,
  ChallengeDetailResponse,
  CreateChallengeRequest,
  UpdateLapTimeRequest,
  SearchChallengesCriteria,
} from '@/types/challenge'
import type { UserRegistrationRequest } from '@/types/user'

const API_BASE = import.meta.env.DEV ? '/api' : (import.meta.env.VITE_API_URL || '/api')

const api = axios.create({
  baseURL: API_BASE,
  headers: { 'Content-Type': 'application/json' },
  timeout: 10_000,
})

// Every GET is public on this backend; only writes need Basic auth.
api.interceptors.request.use((config) => {
  const method = (config.method ?? 'get').toLowerCase()
  if (method === 'get') return config

  const authorizationHeader = useAuthStore.getState().authorizationHeader
  if (authorizationHeader) config.headers.Authorization = authorizationHeader
  return config
})

api.interceptors.response.use(
    (response) => response,
    (error) => {
      const status = error.response?.status
      const method = (error.config?.method ?? '').toLowerCase()
      if (status === 401 && method !== 'get') {
        useAuthStore.getState().logout()
      }
      return Promise.reject(error)
    },
)

// ---------- cars ----------

export const carApi = {
  search: (criteria: SearchCarsCriteria = {}) =>
      api.get<CarDetailResponse[]>('/cars', { params: criteria }),

  get: (carId: number) => api.get<CarDetailResponse>(`/cars/${carId}`),

  /** Returns the created car's id as a string per the spec. */
  create: (body: CreateCarRequest) => api.post<string>('/cars', body),
}

// ---------- tracks ----------

export const trackApi = {
  search: (criteria: SearchTracksCriteria = {}) =>
      api.get<TrackDetailResponse[]>('/tracks', { params: criteria }),

  get: (trackId: number) => api.get<TrackDetailResponse>(`/tracks/${trackId}`),

  create: (body: CreateTrackRequest) => api.post<string>('/tracks', body),
}

// ---------- challenges ----------

export const challengeApi = {
  search: (criteria: SearchChallengesCriteria = {}) =>
      api.get<ChallengeSummaryResponse[]>('/challenges', { params: criteria }),

  get: (challengeId: number) =>
      api.get<ChallengeDetailResponse>(`/challenges/${challengeId}`),

  create: (body: CreateChallengeRequest) =>
      api.post<string>('/challenges', body),

  register: (challengeId: number) =>
      api.post<void>(`/challenges/${challengeId}/register`),

  updateLapTime: (challengeId: number, body: UpdateLapTimeRequest) =>
      api.put<void>(`/challenges/${challengeId}/participant`, body),
}

// ---------- users ----------

export const userApi = {
  register: async (username: string, password: string) => {
    const body: UserRegistrationRequest = { username, password }
    await api.post<void>('/users/register', body)
  },
}

export default api