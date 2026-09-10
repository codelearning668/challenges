import { Moon, Sun } from 'lucide-react'
import { useEffect } from 'react'
import { useThemeStore } from '@/stores/useThemeStore'

export function ThemeToggle() {
  const { theme, toggleTheme } = useThemeStore()

  // Apply the theme to the DOM whenever it changes
  useEffect(() => {
    const root = document.documentElement

    if (theme === 'dark') {
      root.classList.add('dark')
    } else {
      root.classList.remove('dark')
    }
  }, [theme])

  return (
    <button
      onClick={() => toggleTheme()}
      className="p-2 rounded-lg bg-gray-100 dark:bg-gray-800 hover:bg-gray-200 dark:hover:bg-gray-700 transition-colors"
      aria-label={`Switch to ${theme === 'dark' ? 'light' : 'dark'} mode`}
    >
      {theme === 'dark' ? (
        <Sun className="w-5 h-5 text-gray-200" />
      ) : (
        <Moon className="w-5 h-5 text-gray-700" />
      )}
    </button>
  )
}