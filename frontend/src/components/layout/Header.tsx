import { Home, Flag, Car, MapPin, LogIn, User } from 'lucide-react'
import { Link, useLocation } from 'react-router-dom'
import { useAuthStore } from '@/stores/useAuthStore'
import { ThemeToggle } from './ThemeToggle'

interface NavItem {
  to: string
  label: string
  icon: React.ElementType
}

const NAV: NavItem[] = [
  { to: '/', label: 'Dashboard', icon: Home },
  { to: '/challenges', label: 'Challenges', icon: Flag },
  { to: '/cars', label: 'Cars', icon: Car },
  { to: '/tracks', label: 'Tracks', icon: MapPin },
]

export function Header() {
  const { user, logout } = useAuthStore()
  const location = useLocation()

  const isActive = (path: string) =>
      path === '/' ? location.pathname === '/' : location.pathname.startsWith(path)

  return (
      <header className="bg-white dark:bg-gray-800 border-b border-gray-200 dark:border-gray-700 sticky top-0 z-30 shadow-sm">
        <div className="flex items-center justify-between px-4 py-3 md:px-6">
          {/* Left: Logo + nav */}
          <div className="flex items-center gap-6">
            <Link to="/" className="flex items-center gap-3 group" aria-label="Racing Challenges home">
              <div className="w-9 h-9 rounded-lg bg-primary-600 flex items-center justify-center group-hover:bg-primary-700 transition-colors shadow-sm">
                <svg className="w-5 h-5 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M13 10V3L4 14h7v7l9-11h-7z" />
                </svg>
              </div>
              <span className="text-lg font-bold text-gray-900 dark:text-gray-100 tracking-tight hidden lg:inline">
              Racing Challenges
            </span>
            </Link>

            <nav className="hidden md:flex items-center gap-1">
              {NAV.map(({ to, label, icon: Icon }) => {
                const active = isActive(to)
                return (
                    <Link
                        key={to}
                        to={to}
                        className={
                            'flex items-center gap-2 px-3 py-1.5 rounded-lg text-sm font-medium transition-colors ' +
                            (active
                                ? 'bg-primary-50 text-primary-700 dark:bg-primary-900/20 dark:text-primary-400'
                                : 'text-gray-700 dark:text-gray-300 hover:bg-gray-100 dark:hover:bg-gray-700')
                        }
                    >
                      <Icon className="w-4 h-4" />
                      {label}
                    </Link>
                )
              })}
            </nav>
          </div>

          {/* Right: user actions */}
          <div className="flex items-center gap-3">
            {user ? (
                <>
                  <div className="flex items-center gap-2 mr-2">
                    <div className="w-8 h-8 rounded-full bg-primary-100 dark:bg-primary-900/30 flex items-center justify-center">
                      <User className="w-4 h-4 text-primary-600 dark:text-primary-400" />
                    </div>
                    <span className="text-sm font-medium text-gray-700 dark:text-gray-300 hidden sm:block">
                  {user.username}
                </span>
                  </div>
                  <ThemeToggle />
                  <button
                      onClick={logout}
                      className="px-4 py-2 text-sm font-medium text-gray-700 dark:text-gray-300 hover:bg-gray-100 dark:hover:bg-gray-700 rounded-lg transition-colors"
                  >
                    Logout
                  </button>
                </>
            ) : (
                <>
                  <Link
                      to="/login"
                      className="flex items-center gap-2 px-4 py-2 text-sm font-medium text-white bg-primary-600 hover:bg-primary-700 rounded-lg transition-colors"
                  >
                    <LogIn className="w-4 h-4" />
                    Login
                  </Link>
                  <ThemeToggle />
                </>
            )}
          </div>
        </div>
      </header>
  )
}