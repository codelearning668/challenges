import { Outlet } from 'react-router-dom'
import { Header } from './Header'

export function MainLayout() {
  return (
    <div className="flex flex-col h-screen bg-gray-100 dark:bg-gray-900">
      <Header />
      <main className="flex-1 overflow-y-auto p-6">
        <Outlet />
      </main>
    </div>
  )
}