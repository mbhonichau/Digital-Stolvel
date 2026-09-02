import { useQuery, UseQueryOptions, QueryKey } from '@tanstack/react-query';
import type { ApiError } from '@/types';

export interface PollingOptions<TData> {
  intervalMs?: number;
  enabled?: boolean;
  /**
   * Callback determining if polling should terminate based on real backend data.
   * e.g., (data) => data?.status === 'paid' || data?.status === 'failed'
   */
  shouldStopPolling?: (data: TData | undefined) => boolean;
}

/**
 * Polling abstraction to track asynchronous backend / MoMo status changes.
 *
 * Strictly executes real HTTP queries against the backend without local simulations.
 * Dynamically adjusts refetchInterval based on whether a terminal status has been reached.
 */
export function usePollingQuery<TData = unknown, TError = ApiError>(
  queryKey: QueryKey,
  queryFn: () => Promise<TData>,
  options: PollingOptions<TData> = {},
  additionalQueryOptions?: Omit<
    UseQueryOptions<TData, TError, TData, QueryKey>,
    'queryKey' | 'queryFn' | 'refetchInterval'
  >
) {
  const { intervalMs = 3000, enabled = true, shouldStopPolling } = options;

  return useQuery<TData, TError>({
    queryKey,
    queryFn,
    enabled,
    refetchInterval: (query) => {
      const data = query.state.data;
      if (!enabled) return false;
      if (shouldStopPolling && shouldStopPolling(data)) {
        return false; // Stop polling once terminal state is reported by the backend
      }
      return intervalMs; // Continue polling at specified interval
    },
    refetchIntervalInBackground: false,
    ...additionalQueryOptions,
  });
}

/**
 * Utility helper to determine if a generic MoMo transaction has reached terminal state.
 */
export function isTerminalStatus(status?: string | null): boolean {
  if (!status) return false;
  const s = status.toLowerCase();
  return s === 'paid' || s === 'failed' || s === 'completed' || s === 'cancelled';
}
