export type DurationJson =
    | { seconds: number; nano: number; [k: string]: unknown }
    | string

export interface ParticipantDetailResponse {
  participantId: number
  participantName: string
  participantBestLapTime: DurationJson | null
}

export interface ChallengeSummaryResponse {
  challengeId: number
  challengeEndDate: string
  bestParticipantName: string | null
  bestLapTime: DurationJson | null
  trackCountry: string | null
  trackName: string
  carBrand: string
  carName: string
}

export interface ChallengeDetailResponse {
  challengeId: number
  challengeEndDate: string
  bestParticipantName: string | null
  bestLapTime: DurationJson | null
  trackId: number
  trackName: string
  trackCountry: string | null
  trackLengthKm: number | null
  carId: number
  carBrand: string
  carName: string
  carHorsePower: number | null
  carTorque: number | null
  participants: ParticipantDetailResponse[]
}

export interface SearchChallengesCriteria {
  endDate?: string
  bestParticipantName?: string
  trackName?: string
  trackCountry?: string
  carBrand?: string
  carName?: string
}

export interface CreateChallengeRequest {
  trackId: number
  carId: number
  /** yyyy-MM-dd */
  endDate: string
}

export interface UpdateChallengeEndDateRequest {
  endDate: string
}

/**
 * Backend accepts `m:ss.S` / `m:ss.SS` / `m:ss.SSS` on input.
 * Send `null` to clear a participant's lap time.
 */
export interface UpdateLapTimeRequest {
  participantName: string
  newLapTime: string | null
}