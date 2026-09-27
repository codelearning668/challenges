import { usePermissions } from '@/hooks/usePermissions'
import { ParticipantDashboard } from './dashboard/ParticipantDashboard'
import { AdminDashboard } from './dashboard/AdminDashboard'

export function Dashboard() {
    const { isAdmin } = usePermissions()
    return isAdmin ? <AdminDashboard /> : <ParticipantDashboard />
}