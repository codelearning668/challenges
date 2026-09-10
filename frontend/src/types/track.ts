export interface TrackDetailResponse {
  id: number
  country: string | null
  name: string
  lengthKm: number | null
}

export interface SearchTracksCriteria {
  country?: string
  name?: string
  lengthKm?: number
}

export interface CreateTrackRequest {
  name: string
  country?: string
  lengthKm?: number
}