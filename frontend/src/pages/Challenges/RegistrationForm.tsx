import { ArrowLeft, UserPlus } from 'lucide-react'
import { useNavigate, useParams } from 'react-router-dom'
import { useMutation, useQueryClient } from '@tanstack/react-query'
import { Button } from '@/components/shared/Button'
import { Card } from '@/components/shared/Card'
import { challengeApi } from '@/services/api'
import { useAuthStore } from '@/stores/useAuthStore'

export function RegistrationForm() {
  const params = useParams()
  const navigate = useNavigate()
  const queryClient = useQueryClient()
  const user = useAuthStore((s) => s.user)
  const challengeId = Number(params.id)

  const mutation = useMutation({
    mutationFn: () => challengeApi.register(challengeId),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['challenge', challengeId] })
      navigate(`/challenges/${challengeId}`)
    },
  })

  if (!user) {
    return (
        <div className="text-center py-12">
          <h2 className="text-xl text-gray-900 dark:text-gray-100">Please log in first</h2>
          <p className="text-gray-500 dark:text-gray-400 mt-2">
            You need an account to register for a challenge.
          </p>
        </div>
    )
  }

  return (
      <div>
        <div className="mb-6">
          <Button variant="ghost" onClick={() => navigate('/challenges')} className="mr-4">
            <ArrowLeft className="w-4 h-4 mr-2" />
            Back to List
          </Button>
          <h1 className="text-2xl font-bold text-gray-900 dark:text-gray-100">
            Register for Challenge
          </h1>
        </div>

        <Card>
          <div className="bg-blue-50 dark:bg-blue-900/20 p-4 rounded-lg">
            <p className="text-sm text-gray-700 dark:text-gray-300">
              Register as <span className="font-semibold">{user.username}</span>?
            </p>
          </div>

          {mutation.isError && (
              <p className="mt-4 text-sm text-red-600 dark:text-red-400">
                {mutation.error instanceof Error ? mutation.error.message : 'Registration failed'}
              </p>
          )}

          <div className="flex justify-end gap-2 pt-4">
            <Button onClick={() => mutation.mutate()} isLoading={mutation.isPending}>
              <UserPlus className="w-4 h-4 mr-2" />
              Confirm Registration
            </Button>
          </div>
        </Card>
      </div>
  )
}