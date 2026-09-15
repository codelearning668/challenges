import { format } from 'date-fns'
import type { DurationJson, DurationPayload } from '@/types/challenge'

export const formatDate = (date: Date | string): string =>
    format(new Date(date), 'MMM dd, yyyy')

export const formatDateTime = (date: Date | string): string =>
    format(new Date(date), 'MMM dd, yyyy HH:mm')

export const formatDuration = (minutes: number): string => {
  const hours = Math.floor(minutes / 60)
  const mins = minutes % 60
  if (hours > 0) return `${hours}h ${mins}m`
  return `${mins}m`
}

export const formatNumber = (num: number): string => num.toLocaleString()

export const truncate = (str: string, maxLength: number): string => {
  if (str.length <= maxLength) return str
  return str.slice(0, maxLength - 3) + '...'
}

// ---------- lap-time parsing & formatting ----------

/**
 * Parse "mm:ss.mmm", "m:ss.mmm", or plain seconds into total seconds.
 * Returns NaN on unparseable input.
 *
 *   "1:22.555" → 82.555
 *   "0:45.2"   → 45.2
 *   "82.555"   → 82.555
 *   "1:22"     → 82
 *   "1:22,555" → 82.555   (comma tolerated)
 *   "1:75"     → NaN      (seconds ≥ 60 rejected)
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

/** Format total seconds as "MM:SS.mmm". */
export const formatLapTime = (seconds: number): string => {
  const mins = Math.floor(seconds / 60)
  const secs = Math.floor(seconds % 60)
  const ms = Math.round((seconds - Math.floor(seconds)) * 1000)
  return `${mins.toString().padStart(2, '0')}:${secs
      .toString()
      .padStart(2, '0')}.${ms.toString().padStart(3, '0')}`
}

/** Convert any DurationJson (POJO or ISO string) to total seconds. */
export const durationToSeconds = (d: DurationJson | null | undefined): number | null => {
  if (d == null) return null

  if (typeof d === 'string') {
    const m = /^PT(?:(\d+)H)?(?:(\d+)M)?(?:([\d.]+)S)?$/.exec(d)
    if (!m) return null
    const h = Number(m[1] ?? 0)
    const min = Number(m[2] ?? 0)
    const s = Number(m[3] ?? 0)
    return h * 3600 + min * 60 + s
  }

  const secs = typeof d.seconds === 'number' ? d.seconds : 0
  const nano = typeof d.nano === 'number' ? d.nano : 0
  return secs + nano / 1_000_000_000
}

/** Format a DurationJson as "MM:SS.mmm", or "—" when missing. */
export const formatDurationJson = (d: DurationJson | null | undefined): string => {
  const s = durationToSeconds(d)
  return s == null ? '—' : formatLapTime(s)
}

/**
 * Convert total seconds into the POJO the backend expects.
 *
 *   82.555 → { seconds: 82, nano: 555_000_000 }
 *   45     → { seconds: 45, nano: 0 }
 */
export const secondsToDurationPayload = (seconds: number): DurationPayload => {
  const whole = Math.floor(seconds)
  const nano = Math.round((seconds - whole) * 1_000_000_000)
  return { seconds: whole, nano }
}