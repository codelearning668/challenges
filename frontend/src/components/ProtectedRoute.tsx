import { useAuthStore } from '@/stores/useAuthStore'
import { Navigate, useLocation } from 'react-router-dom'

interface ProtectedRouteProps {
  children: React.ReactNode
  requiredRoles?: string[]
}

export function ProtectedRoute({ children, requiredRoles }: ProtectedRouteProps) {
  const isAuthenticated = useAuthStore((s) => s.isAuthenticated)
  const user = useAuthStore((s) => s.user)
  const location = useLocation()

  if (!isAuthenticated) {
    return <Navigate to="/login" state={{ from: location }} replace />
  }

  if (requiredRoles && requiredRoles.length > 0) {
    const hasRole = requiredRoles.some((role) => user?.authorities?.includes(role))
    if (!hasRole) {
      return <Navigate to="/" replace />
    }
  }

  return <>{children}</>
}