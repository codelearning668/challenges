import { useAuthStore } from '@/stores/useAuthStore'

export function usePermissions() {
  const { user, isAuthenticated } = useAuthStore()
  
  const isAdmin = user?.authorities?.includes('ADMIN')
  const isParticipant = user?.authorities?.includes('PARTICIPANT')
  
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