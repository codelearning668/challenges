export interface UserRegistrationRequest {
  username: string
  password: string
}

export interface UserInfoResponse {
  username: string
  roles: string[]
}

export interface User {
  username: string
  authorities: string[]
}

export type Authority = 'ADMIN' | 'PARTICIPANT'