import { z } from 'zod'

export const carSchema = z.object({
    brand: z.string().min(1, 'Brand is required').max(50),
    name: z.string().min(1, 'Name is required').max(100),
    hp: z.coerce.number().int().positive().optional(),
    torque: z.coerce.number().int().positive().optional(),
    drive: z.enum(['FRONT', 'REAR', 'ALL']).optional(),
    simulatorId: z.coerce.number().int().positive('Simulator is required'),
    fromDlc: z.boolean(),
})

export const trackSchema = z.object({
    name: z.string().min(1, 'Name is required').max(100),
    country: z.string().max(100).optional(),
    lengthKm: z.coerce.number().positive().optional(),
    simulatorId: z.coerce.number().int().positive('Simulator is required'),
    fromDlc: z.boolean(),
})

export const challengeSchema = z.object({
    trackId: z.coerce.number().int().positive('Track is required'),
    carId: z.coerce.number().int().positive('Car is required'),
    endDate: z
        .string()
        .min(1, 'End date is required')
        .refine(
            (val) => {
                const today = new Date()
                today.setHours(0, 0, 0, 0)
                return new Date(`${val}T00:00:00`).getTime() >= today.getTime()
            },
            { message: 'End date cannot be in the past' },
        ),
})

export const loginSchema = z.object({
    username: z.string().min(1, 'Username is required'),
    password: z.string().min(1, 'Password is required'),
})

export const registerSchema = z.object({
    username: z.string().min(1, 'Username is required').max(100),
    password: z.string().min(1, 'Password is required').max(500),
})

export const userSchema = loginSchema

export type CarFormData = z.infer<typeof carSchema>
export type TrackFormData = z.infer<typeof trackSchema>
export type ChallengeFormData = z.infer<typeof challengeSchema>
export type LoginFormData = z.infer<typeof loginSchema>
export type RegisterFormData = z.infer<typeof registerSchema>
export type UserFormData = z.infer<typeof userSchema>