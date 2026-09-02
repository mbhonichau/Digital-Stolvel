import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { triggerPayout, getPayoutStatus } from '@/api/payouts';
import { getGroupCycleHistory } from '@/api/contributions';
import type {
  TriggerPayoutResponse,
  CycleHistory,
  ApiError,
} from '@/types';

export const PAYOUT_QUERY_KEYS = {
  all: ['payouts'] as const,
  detail: (id: string) => ['payouts', id] as const,
  history: (groupId: string) => ['history', 'group', groupId] as const,
};

/**
 * Hook to retrieve cycle and payout history for a Stokvel group.
 * Communicates strictly with backend GET /groups/{groupId}/cycles.
 */
export function useCycleHistory(groupId?: string) {
  return useQuery<CycleHistory[], ApiError>({
    queryKey: PAYOUT_QUERY_KEYS.history(groupId || ''),
    queryFn: () => {
      if (!groupId) {
        throw new Error('Group ID is required to fetch cycle history');
      }
      return getGroupCycleHistory(groupId);
    },
    enabled: Boolean(groupId),
  });
}

/**
 * Hook to retrieve the disbursement status of a payout by ID.
 * Communicates strictly with backend GET /payouts/{id}.
 */
export function usePayoutStatus(payoutId?: string) {
  return useQuery<TriggerPayoutResponse, ApiError>({
    queryKey: PAYOUT_QUERY_KEYS.detail(payoutId || ''),
    queryFn: () => {
      if (!payoutId) {
        throw new Error('Payout ID is required to fetch payout status');
      }
      return getPayoutStatus(payoutId);
    },
    enabled: Boolean(payoutId),
  });
}

/**
 * Mutation hook to trigger a MoMo payout disbursement for a cycle.
 * Communicates strictly with backend POST /cycles/{cycleId}/payout.
 */
export function useTriggerPayout() {
  const queryClient = useQueryClient();

  return useMutation<TriggerPayoutResponse, ApiError, string>({
    mutationFn: (cycleId: string) => triggerPayout(cycleId),
    onSuccess: (result) => {
      queryClient.setQueryData(PAYOUT_QUERY_KEYS.detail(result.id), result);
      queryClient.invalidateQueries({ queryKey: PAYOUT_QUERY_KEYS.all });
    },
  });
}
