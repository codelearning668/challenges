import { format } from 'date-fns'

// ---------- dates ----------

export const formatDate = (date: Date | string): string =>
    format(new Date(date), 'MMM dd, yyyy')

export const formatDateTime = (date: Date | string): string =>
    format(new Date(date), 'MMM dd, yyyy HH:mm')

/** Returns true when the challenge's end date is today or later. */
export const isChallengeActive = (endDate: string | null | undefined): boolean => {
  if (!endDate) return false
  const today = new Date()
  today.setHours(0, 0, 0, 0)
  const end = new Date(`${endDate}T00:00:00`)
  return end >= today
}

// ---------- numbers & text ----------

export const formatNumber = (num: number): string => num.toLocaleString()

export const truncate = (str: string, maxLength: number): string => {
  if (str.length <= maxLength) return str
  return str.slice(0, maxLength - 3) + '...'
}

export const formatDuration = (minutes: number): string => {
  const hours = Math.floor(minutes / 60)
  const mins = minutes % 60
  if (hours > 0) return `${hours}h ${mins}m`
  return `${mins}m`
}

// ---------- lap times ----------

/**
 * The backend already returns lap times in canonical "mm:ss.SSS" form,
 * so this is just a null-guard for display.
 */
export const formatDurationJson = (value: string | null | undefined): string =>
    value ?? '—'

/**
 * Parse "m:ss.S", "m:ss.SS", "m:ss.SSS", or plain seconds into total seconds.
 * Returns NaN on unparseable input.
 *
 *   "1:22.555" → 82.555
 *   "82.555"   → 82.555
 *   "1:22"     → 82
 *   "1:22,555" → 82.555   (comma tolerated)
 */
export const parseLapTime = (input: string): number => {
  const trimmed = input.trim().replace(',', '.')
  if (!trimmed) return NaN

  if (!trimmed.includes(':')) {
    const n = Number(trimmed)
    return Number.isFinite(n) ? n : NaN
  }

  const parts = trimmed.split(':')
  if (parts.length !== 2) return NaN
  const mins = Number(parts[0])
  const secs = Number(parts[1])
  if (!Number.isFinite(mins) || !Number.isFinite(secs)) return NaN
  if (mins < 0 || secs < 0 || secs >= 60) return NaN
  return mins * 60 + secs
}

/** Format total seconds as "mm:ss.SSS" (matches the backend's canonical output). */
export const formatLapTime = (seconds: number): string => {
  const mins = Math.floor(seconds / 60)
  const secs = Math.floor(seconds % 60)
  const ms = Math.round((seconds - Math.floor(seconds)) * 1000)
  return `${mins.toString().padStart(2, '0')}:${secs
      .toString()
      .padStart(2, '0')}.${ms.toString().padStart(3, '0')}`
}

// ---------- track length ----------

/** Track length with 3 decimals, e.g. "5.793 km". */
export const formatTrackLength = (km: number | null | undefined): string =>
    km == null ? '—' : `${km.toFixed(3)} km`

// ---------- gaps (F1-style) ----------

/**
 * Compute the gap between two lap-time strings, in seconds.
 * Returns null when either side is missing or unparseable.
 */
export const gapBetween = (
    a: string | null | undefined,
    b: string | null | undefined,
): number | null => {
  if (!a || !b) return null
  const sa = parseLapTime(a)
  const sb = parseLapTime(b)
  if (!Number.isFinite(sa) || !Number.isFinite(sb)) return null
  return sa - sb
}

/**
 * Format a lap-time gap F1-style.
 *
 *   null or ≤ 0 → "—"
 *   < 60s       → "+1.234"
 *   ≥ 60s       → "+1:23.456"
 */
export const formatGap = (gapSeconds: number | null | undefined): string => {
  if (gapSeconds == null || gapSeconds <= 0) return '—'
  if (gapSeconds < 60) return `+${gapSeconds.toFixed(3)}`

  const mins = Math.floor(gapSeconds / 60)
  const secs = gapSeconds - mins * 60
  return `+${mins}:${secs.toFixed(3).padStart(6, '0')}`
}