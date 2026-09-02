import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { getContributions, triggerContribution, getContributionStatus } from '@/api/contributions';
import type {
  ContributionStatus,
  TriggerContributionRequest,
  ApiError,
} from '@/types';
import { QUERY_KEYS } from './keys';

export const CONTRIBUTION_QUERY_KEYS = QUERY_KEYS.contributions;

/**
 * Hook to retrieve all contribution records for a specific cycle.
 * Communicates strictly with backend GET /cycles/{id}/contributions.
 */
export function useContributions(cycleId?: string) {
  return useQuery<ContributionStatus[], ApiError>({
    queryKey: QUERY_KEYS.contributions.byCycle(cycleId || ''),
    queryFn: () => {
      if (!cycleId) {
        throw new Error('Cycle ID is required to fetch contributions');
      }
      return getContributions(cycleId);
    },
    enabled: Boolean(cycleId),
  });
}

/**
 * Hook to retrieve a single contribution's status by its ID.
 * Communicates strictly with backend GET /contributions/{id}.
 */
export function useContributionStatus(contributionId?: string) {
  return useQuery<ContributionStatus, ApiError>({
    queryKey: QUERY_KEYS.contributions.detail(contributionId || ''),
    queryFn: () => {
      if (!contributionId) {
        throw new Error('Contribution ID is required to fetch status');
      }
      return getContributionStatus(contributionId);
    },
    enabled: Boolean(contributionId),
  });
}

/**
 * Mutation hook to initiate a MoMo contribution payment.
 * Communicates strictly with backend POST /contributions.
 */
export function useTriggerContribution() {
  const queryClient = useQueryClient();

  return useMutation<ContributionStatus, ApiError, TriggerContributionRequest>({
    mutationFn: (request: TriggerContributionRequest) => triggerContribution(request),
    onSuccess: (result, variables) => {
      // 1. Invalidate all contributions queries for cycle & global collection
      queryClient.invalidateQueries({ queryKey: QUERY_KEYS.contributions.all });
      queryClient.invalidateQueries({ queryKey: QUERY_KEYS.contributions.byCycle(variables.cycleId) });
      
      // 2. Invalidate group and history data so totals reflect server state
      queryClient.invalidateQueries({ queryKey: QUERY_KEYS.groups.all });
      queryClient.invalidateQueries({ queryKey: QUERY_KEYS.history.all });

      // 3. Set query data for specific contribution detail
      if (result && result.id) {
        queryClient.setQueryData(QUERY_KEYS.contributions.detail(result.id), result);
      }
    },
  });
}
