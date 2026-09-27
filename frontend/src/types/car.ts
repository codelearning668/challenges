export type WheelDrive = 'FRONT' | 'REAR' | 'ALL'

export interface CarDetailResponse {
  id: number
  brand: string
  name: string
  horsePower: number | null
  torque: number | null
  wheelDrive: WheelDrive | null
  simulatorName: string | null
  fromDlc: boolean
}

export interface SearchCarsCriteria {
  brand?: string
  name?: string
  horsePower?: number
  torque?: number
  wheelDrive?: WheelDrive
  simulatorId?: number
  fromDlc?: boolean
}

export interface CreateCarRequest {
  brand: string
  name: string
  hp?: number
  torque?: number
  drive?: WheelDrive
  simulatorId: number
  fromDlc: boolean
}

export interface UpdateCarRequest {
  brand: string
  name: string
  hp?: number
  torque?: number
  drive?: WheelDrive
  fromDlc?: boolean
}