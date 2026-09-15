import { z } from 'zod'

export const carSchema = z.object({
  brand: z.string().min(1, 'Brand is required').max(50),
  name: z.string().min(1, 'Name is required').max(100),
  hp: z.coerce.number().int().positive().optional(),
  torque: z.coerce.number().int().positive().optional(),
  drive: z.enum(['FRONT', 'REAR', 'ALL']).optional(),
})

export const trackSchema = z.object({
  name: z.string().min(1, 'Name is required').max(100),
  country: z.string().max(100).optional(),
  lengthKm: z.coerce.number().positive().optional(),
})

export const challengeSchema = z.object({
  trackId: z.coerce.number().int().positive('Track is required'),
  carId: z.coerce.number().int().positive('Car is required'),
  endDate: z.string().min(1, 'End date is required'),
})

/** Login: no length checks — the backend is the authority. */
export const loginSchema = z.object({
  username: z.string().min(1, 'Username is required'),
  password: z.string().min(1, 'Password is required'),
})

/** Register: keep backend-aligned limits (username 100, password 500). */
export const registerSchema = z.object({
  username: z.string().min(1, 'Username is required').max(100),
  password: z.string().min(1, 'Password is required').max(500),
})

/** Kept for backward compatibility where a single schema was imported. */
export const userSchema = loginSchema

export type CarFormData = z.infer<typeof carSchema>
export type TrackFormData = z.infer<typeof trackSchema>
export type ChallengeFormData = z.infer<typeof challengeSchema>
export type LoginFormData = z.infer<typeof loginSchema>
export type RegisterFormData = z.infer<typeof registerSchema>
export type UserFormData = z.infer<typeof userSchema>