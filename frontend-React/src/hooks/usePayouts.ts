import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { triggerPayout, getPayoutStatus } from '@/api/payouts';
import { getGroupCycleHistory } from '@/api/contributions';
import type {
  CreatePayoutRequest,
  TriggerPayoutResponse,
  CycleHistory,
  ApiError,
} from '@/types';
import { QUERY_KEYS } from './keys';

export const PAYOUT_QUERY_KEYS = QUERY_KEYS.payouts;

/**
 * Hook to retrieve cycle and payout history for a Stokvel group.
 * Communicates strictly with backend GET /groups/{groupId}/history.
 */
export function useCycleHistory(groupId?: string) {
  return useQuery<CycleHistory[], ApiError>({
    queryKey: QUERY_KEYS.history.byGroup(groupId || ''),
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
    queryKey: QUERY_KEYS.payouts.detail(payoutId || ''),
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
 * Mutation hook to trigger a MoMo payout disbursement.
 * Communicates strictly with backend POST /payouts.
 */
export function useTriggerPayout() {
  const queryClient = useQueryClient();

  return useMutation<TriggerPayoutResponse, ApiError, string | CreatePayoutRequest>({
    mutationFn: (requestOrCycleId: string | CreatePayoutRequest) => triggerPayout(requestOrCycleId),
    onSuccess: (result) => {
      if (result && result.id) {
        queryClient.setQueryData(QUERY_KEYS.payouts.detail(result.id), result);
      }
      // TASK 2: After payout succeeds, invalidate all relevant queries across group, contributions, payouts, history, & cycle progress
      queryClient.invalidateQueries({ queryKey: QUERY_KEYS.payouts.all });
      queryClient.invalidateQueries({ queryKey: QUERY_KEYS.groups.all });
      queryClient.invalidateQueries({ queryKey: QUERY_KEYS.contributions.all });
      queryClient.invalidateQueries({ queryKey: QUERY_KEYS.history.all });
      queryClient.invalidateQueries({ queryKey: QUERY_KEYS.members.all });
    },
  });
}
