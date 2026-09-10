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

export const userSchema = z.object({
  username: z.string().min(3).max(100),
  password: z.string().min(6).max(500),
})

export type CarFormData = z.infer<typeof carSchema>
export type TrackFormData = z.infer<typeof trackSchema>
export type ChallengeFormData = z.infer<typeof challengeSchema>
export type UserFormData = z.infer<typeof userSchema>