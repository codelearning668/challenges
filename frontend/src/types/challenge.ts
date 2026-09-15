/**
 * java.time.Duration on the wire is either:
 *   - an object { seconds, nano } if JavaTimeModule is not registered, OR
 *   - an ISO-8601 string like "PT1M22.555S" if it is.
 * We accept both and normalise in utils/format.ts.
 */
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

/** The backend serialises Duration as a POJO, not an ISO string. */
export interface DurationPayload {
  seconds: number
  nano: number
}

export interface UpdateLapTimeRequest {
  participantName: string
  newLapTime: DurationPayload
}