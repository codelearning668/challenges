import { Routes, Route, Navigate } from 'react-router-dom'
import { ProtectedRoute } from '@/components/ProtectedRoute'
import { MainLayout } from '@/components/layout/MainLayout'
import { Dashboard } from './pages/Dashboard'
import { Login } from './pages/Login'
import { Register } from './pages/Register'
import { CarList } from './pages/Cars/CarList'
import { CarDetail } from './pages/Cars/CarDetail'
import { CarForm } from './pages/Cars/CarForm'
import { TrackList } from './pages/Tracks/TrackList'
import { TrackDetail } from './pages/Tracks/TrackDetail'
import { TrackForm } from './pages/Tracks/TrackForm'
import { ChallengeList } from './pages/Challenges/ChallengeList'
import { ChallengeDetail } from './pages/Challenges/ChallengeDetail'
import { ChallengeForm } from './pages/Challenges/ChallengeForm'
import { RegistrationForm } from './pages/Challenges/RegistrationForm'
import { LapTimeUpdate } from './pages/Challenges/LapTimeUpdate'
import { UserManagement } from './pages/Users/UserManagement'

export default function App() {
    return (
        <Routes>
            <Route path="/login" element={<Login />} />
            <Route path="/register" element={<Register />} />

            {/* Everything under the layout requires auth */}
            <Route
                path="/"
                element={
                    <ProtectedRoute>
                        <MainLayout />
                    </ProtectedRoute>
                }
            >
                <Route index element={<Dashboard />} />

                <Route path="cars" element={<CarList />} />
                <Route path="cars/new" element={<CarForm />} />
                <Route path="cars/:id" element={<CarDetail />} />

                <Route path="tracks" element={<TrackList />} />
                <Route path="tracks/new" element={<TrackForm />} />
                <Route path="tracks/:id" element={<TrackDetail />} />

                <Route path="challenges" element={<ChallengeList />} />
                <Route path="challenges/new" element={<ChallengeForm />} />
                <Route path="challenges/:id" element={<ChallengeDetail />} />
                <Route path="challenges/:id/register" element={<RegistrationForm />} />
                <Route path="challenges/:id/lap-time" element={<LapTimeUpdate />} />

                <Route path="users" element={<UserManagement />} />
            </Route>

            {/* Any unmatched route: send to dashboard (which redirects to /login if needed) */}
            <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
    )
}