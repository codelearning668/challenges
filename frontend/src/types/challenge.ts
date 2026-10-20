export interface ParticipantDetailResponse {
  participantId: number
  participantName: string
  participantBestLapTime: string | null
}

export interface ChallengeSummaryResponse {
  challengeId: number
  challengeEndDate: string
  simulatorName: string | null
  bestParticipantName: string | null
  bestLapTime: string | null
  trackCountry: string | null
  trackName: string
  carBrand: string
  carName: string
}

export interface ChallengeDetailResponse {
  challengeId: number
  challengeEndDate: string
  simulatorName: string | null
  bestParticipantName: string | null
  bestLapTime: string | null
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
  conversation: ConversationEntryDetailResponse[]
}

export interface SearchChallengesCriteria {
  endDate?: string
  bestParticipantName?: string
  trackName?: string
  trackCountry?: string
  carBrand?: string
  carName?: string
  simulatorId?: number
  isActive?: boolean // NEW
}

export interface CreateChallengeRequest {
  trackId: number
  carId: number
  endDate: string
}

export interface UpdateChallengeEndDateRequest {
  endDate: string
}

export interface UpdateLapTimeRequest {
  participantName: string
  newLapTime: string | null
}

export interface ConversationEntryDetailResponse {
  entryId: number
  parentEntryId: number | null
  message: string
  participantName: string
  participantLapTimeAtPosting: string | null
  createdAt: string
}