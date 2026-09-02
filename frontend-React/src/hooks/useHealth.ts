import { useQuery } from '@tanstack/react-query';
import { checkHealth } from '@/api/health';
import type { ApiError } from '@/types';

/**
 * Hook to check backend health status.
 */
export function useHealthCheck(enabled = false) {
  return useQuery<{ status: string }, ApiError>({
    queryKey: ['backend-health'],
    queryFn: () => checkHealth(),
    retry: 1,
    refetchOnWindowFocus: false,
    enabled,
  });
}
