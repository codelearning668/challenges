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
    const authorities = user?.authorities ?? []
    const hasRole = requiredRoles.some((role) => authorities.includes(role))
    if (!hasRole) {
      if (import.meta.env.DEV) {
        console.warn('ProtectedRoute: access denied', {
          requiredRoles,
          authorities,
          pathname: location.pathname,
        })
      }
      return <Navigate to="/" replace />
    }
  }

  return <>{children}</>
}