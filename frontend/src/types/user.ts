export interface UserRegistrationRequest {
  username: string
  password: string
}

export interface User {
  username: string
  authorities: string[]
}