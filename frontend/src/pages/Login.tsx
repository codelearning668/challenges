import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { userSchema } from '@/utils/validators'
import { Button } from '@/components/shared/Button'
import { Input } from '@/components/shared/Input'
import { Card } from '@/components/shared/Card'
import { useAuth } from '@/services/auth'

export function Login() {
  const navigate = useNavigate()
  const login = useAuth()
  
  const [isLoading, setIsLoading] = useState(false)
  const [formError, setFormError] = useState<string | null>(null)
  
  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm({
    resolver: zodResolver(userSchema),
  })

  const onSubmit = async (data: any) => {
    try {
      setIsLoading(true)
      setFormError(null)
      await login.login(data.username, data.password)
      navigate('/')
    } catch {
      setFormError('Invalid username or password.')
    } finally {
      setIsLoading(false)
    }
  }

  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-50 dark:bg-gray-900 px-4">
      <Card className="w-full max-w-md">
        <div className="text-center mb-8">
          <div className="w-16 h-16 rounded-xl bg-primary-600 flex items-center justify-center mx-auto mb-4">
            <svg className="w-8 h-8 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M13 10V3L4 14h7v7l9-11h-7z" />
            </svg>
          </div>
          <h1 className="text-2xl font-bold text-gray-900 dark:text-gray-100">Welcome Back</h1>
          <p className="text-gray-500 dark:text-gray-400 mt-2">Sign in to your account</p>
        </div>

        <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
          {formError && <p role="alert" className="text-sm text-red-600">{formError}</p>}
          <Input
            label="Username"
            type="text"
            placeholder="Enter your username"
            {...register('username')}
            error={errors.username?.message}
          />

          <Input
            label="Password"
            type="password"
            placeholder="Enter your password"
            {...register('password')}
            error={errors.password?.message}
          />

          <Button type="submit" className="w-full" isLoading={isLoading}>
            Sign In
          </Button>
        </form>

        <div className="mt-6 text-center">
          <p className="text-sm text-gray-500 dark:text-gray-400">
            Don't have an account?{' '}
            <Link to="/register" className="text-primary-600 hover:text-primary-700 font-medium">
              Register here
            </Link>
          </p>
        </div>
      </Card>
    </div>
  )
}
