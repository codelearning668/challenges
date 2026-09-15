import { useAuthStore } from '@/stores/useAuthStore'

export function usePermissions() {
  const user = useAuthStore((s) => s.user)
  const isAuthenticated = useAuthStore((s) => s.isAuthenticated)

  const authorities = user?.authorities ?? []
  const isAdmin = isAuthenticated && authorities.includes('ADMIN')
  const isParticipant = isAuthenticated && authorities.includes('PARTICIPANT')

  return {
    isAuthenticated,
    isAdmin,
    isParticipant,
    canCreateCar: isAdmin,
    canCreateTrack: isAdmin,
    canCreateChallenge: isAdmin,
    canRegisterForChallenge: isParticipant,
    canUpdateLapTime: isParticipant || isAdmin,
    canViewAdminOnly: isAdmin,
  }
}