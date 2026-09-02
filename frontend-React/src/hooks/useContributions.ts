import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { getContributions, triggerContribution, getContributionStatus } from '@/api/contributions';
import type {
  ContributionStatus,
  TriggerContributionRequest,
  ApiError,
} from '@/types';

export const CONTRIBUTION_QUERY_KEYS = {
  all: ['contributions'] as const,
  byCycle: (cycleId: string) => ['contributions', 'cycle', cycleId] as const,
  detail: (id: string) => ['contributions', 'detail', id] as const,
};

/**
 * Hook to retrieve all contribution records for a specific cycle.
 * Communicates strictly with backend GET /cycles/{id}/contributions.
 */
export function useContributions(cycleId?: string) {
  return useQuery<ContributionStatus[], ApiError>({
    queryKey: CONTRIBUTION_QUERY_KEYS.byCycle(cycleId || ''),
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
    queryKey: CONTRIBUTION_QUERY_KEYS.detail(contributionId || ''),
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
      // Invalidate cycle contributions to refresh live state from backend
      queryClient.invalidateQueries({
        queryKey: CONTRIBUTION_QUERY_KEYS.byCycle(variables.cycleId),
      });
      // Invalidate individual status
      queryClient.setQueryData(CONTRIBUTION_QUERY_KEYS.detail(result.id), result);
    },
  });
}
