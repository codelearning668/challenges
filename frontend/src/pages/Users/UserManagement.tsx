import { useState } from 'react'
import { UserPlus } from 'lucide-react'
import { useMutation } from '@tanstack/react-query'
import { userApi } from '@/services/api'
import { Button } from '@/components/shared/Button'
import { Card } from '@/components/shared/Card'
import { Input } from '@/components/shared/Input'

export function UserManagement() {
  const [username, setUsername] = useState('')
  const [password, setPassword] = useState('')

  const mutation = useMutation({
    mutationFn: () => userApi.register(username, password),
    onSuccess: () => {
      setUsername('')
      setPassword('')
    },
  })

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault()
    if (username.length < 3 || password.length < 6) return
    mutation.mutate()
  }

  return (
      <div>
        <div className="mb-6">
          <h1 className="text-2xl font-bold text-gray-900 dark:text-gray-100">User Management</h1>
        </div>

        <Card>
          <h2 className="text-lg font-semibold text-gray-900 dark:text-gray-100 mb-4">
            Create New User
          </h2>
          <form onSubmit={handleSubmit} className="space-y-4">
            {mutation.isError && (
                <p className="text-sm text-red-600 dark:text-red-400">
                  {mutation.error instanceof Error ? mutation.error.message : 'Failed to create user'}
                </p>
            )}
            {mutation.isSuccess && (
                <p className="text-sm text-green-600 dark:text-green-400">User created.</p>
            )}

            <Input
                label="Username"
                placeholder="Enter username (3–100 chars)"
                value={username}
                onChange={(e) => setUsername(e.target.value)}
            />
            <Input
                label="Password"
                type="password"
                placeholder="Enter password (6–500 chars)"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
            />

            <div className="flex justify-end gap-2 pt-4">
              <Button type="submit" isLoading={mutation.isPending} disabled={username.length < 3 || password.length < 6}>
                <UserPlus className="w-4 h-4 mr-2" />
                Create User
              </Button>
            </div>
          </form>
        </Card>
      </div>
  )
}