export type WheelDrive = 'FRONT' | 'REAR' | 'ALL'

export interface CarDetailResponse {
  id: number
  brand: string
  name: string
  horsePower: number | null
  torque: number | null
  wheelDrive: WheelDrive | null
}

export interface SearchCarsCriteria {
  brand?: string
  name?: string
  horsePower?: number
  torque?: number
  wheelDrive?: WheelDrive
}

export interface CreateCarRequest {
  brand: string
  name: string
  hp?: number
  torque?: number
  drive?: WheelDrive
}