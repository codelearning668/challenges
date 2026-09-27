export interface TrackDetailResponse {
  id: number
  country: string | null
  name: string
  lengthKm: number | null
  simulatorName: string | null
  fromDlc: boolean
}

export interface SearchTracksCriteria {
  country?: string
  name?: string
  lengthKm?: number
  simulatorId?: number
  fromDlc?: boolean
}

export interface CreateTrackRequest {
  name: string
  country?: string
  lengthKm?: number
  simulatorId: number
  fromDlc: boolean
}

export interface UpdateTrackRequest {
  name: string
  country?: string
  lengthKm?: number
  fromDlc?: boolean
}