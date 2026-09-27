import { useQuery } from '@tanstack/react-query'
import { simulatorApi } from '@/services/api'

export function useSimulators() {
    return useQuery({
        queryKey: ['simulators'],
        queryFn: () => simulatorApi.list(),
        // Simulators are reference data — almost never change.
        staleTime: 1000 * 60 * 10,
    })
}