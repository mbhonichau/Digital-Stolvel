import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { getContributions, getGroupCycles, triggerContribution, getContributionStatus } from '@/api/contributions';
import type {
  ContributionStatus,
  TriggerContributionRequest,
  ApiError,
  CycleResponse,
} from '@/types';

export const CONTRIBUTION_QUERY_KEYS = {
  all: ['contributions'] as const,
  byCycle: (cycleId: string) => ['contributions', 'cycle', cycleId] as const,
  detail: (id: string) => ['contributions', 'detail', id] as const,
  groupCycles: (groupId: string) => ['groups', groupId, 'cycles'] as const,
};

export function useGroupCycles(groupId?: string) {
  return useQuery<CycleResponse[], ApiError>({
    queryKey: CONTRIBUTION_QUERY_KEYS.groupCycles(groupId || ''),
    queryFn: () => {
      if (!groupId) throw new Error('Group ID is required to fetch cycles');
      return getGroupCycles(groupId);
    },
    enabled: Boolean(groupId),
  });
}

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
