import { format } from 'date-fns'
import type { DurationJson } from '@/types/challenge'

export const formatDate = (date: Date | string): string =>
    format(new Date(date), 'MMM dd, yyyy')

export const formatDateTime = (date: Date | string): string =>
    format(new Date(date), 'MMM dd, yyyy HH:mm')

export const formatNumber = (num: number): string => num.toLocaleString()

export const truncate = (str: string, maxLength: number): string => {
  if (str.length <= maxLength) return str
  return str.slice(0, maxLength - 3) + '...'
}

// ---------- lap-time parsing & formatting ----------

/**
 * Parse "m:ss.S", "m:ss.SS", "m:ss.SSS", or plain seconds into total seconds.
 * Returns NaN when unparseable.
 *
 *   "1:22.555" → 82.555
 *   "0:45.2"   → 45.2
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

/** Format total seconds as "mm:ss.SSS" (canonical output form). */
export const formatLapTime = (seconds: number): string => {
  const mins = Math.floor(seconds / 60)
  const secs = Math.floor(seconds % 60)
  const ms = Math.round((seconds - Math.floor(seconds)) * 1000)
  return `${mins.toString().padStart(2, '0')}:${secs
      .toString()
      .padStart(2, '0')}.${ms.toString().padStart(3, '0')}`
}

/** Convert any DurationJson to total seconds. */
export const durationToSeconds = (d: DurationJson | null | undefined): number | null => {
  if (d == null) return null

  // Backend now sends "mm:ss.SSS" as a plain string.
  if (typeof d === 'string') {
    const parsed = parseLapTime(d)
    return Number.isFinite(parsed) ? parsed : null
  }

  const secs = typeof d.seconds === 'number' ? d.seconds : 0
  const nano = typeof d.nano === 'number' ? d.nano : 0
  return secs + nano / 1_000_000_000
}

/** Format a DurationJson as "mm:ss.SSS", or "—" when missing. */
export const formatDurationJson = (d: DurationJson | null | undefined): string => {
  const s = durationToSeconds(d)
  return s == null ? '—' : formatLapTime(s)
}

/**
 * Convert total seconds into the string the backend expects.
 *   82.555 → "01:22.555"
 */
export const secondsToLapTimeString = (seconds: number): string =>
    formatLapTime(seconds)

/** Is this challenge still active? Compares yyyy-MM-dd end date to today (local). */
export const isChallengeActive = (endDate: string | null | undefined): boolean => {
  if (!endDate) return false
  const today = new Date()
  today.setHours(0, 0, 0, 0)
  const end = new Date(`${endDate}T00:00:00`)
  return end >= today
}


export const formatGap = (gapSeconds: number | null | undefined): string => {
  if (gapSeconds == null || gapSeconds <= 0) return '—'
  if (gapSeconds < 60) return `+${gapSeconds.toFixed(3)}`

  const mins = Math.floor(gapSeconds / 60)
  const secs = gapSeconds - mins * 60
  return `+${mins}:${secs.toFixed(3).padStart(6, '0')}`
}