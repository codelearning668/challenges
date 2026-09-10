import { format } from 'date-fns'
import type { DurationJson } from '@/types/challenge'

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

export const formatLapTime = (seconds: number): string => {
  const mins = Math.floor(seconds / 60)
  const secs = seconds % 60
  const ms = Math.floor((seconds * 1000) % 1000)
  return `${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}.${ms
      .toString()
      .padStart(3, '0')}`
}

export const formatNumber = (num: number): string => num.toLocaleString()

export const truncate = (str: string, maxLength: number): string => {
  if (str.length <= maxLength) return str
  return str.slice(0, maxLength - 3) + '...'
}

// ---------- Duration helpers ----------


export const durationToSeconds = (d: DurationJson | null | undefined): number | null => {
  if (d == null) return null

  if (typeof d === 'string') {
    // Parse ISO-8601 like "PT1H2M3.456S" or "PT92.5S"
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

export const formatDurationJson = (d: DurationJson | null | undefined): string => {
  const s = durationToSeconds(d)
  return s == null ? '—' : formatLapTime(s)
}

export const secondsToIsoDuration = (seconds: number): string => `PT${seconds}S`