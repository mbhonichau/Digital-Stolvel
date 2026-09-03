import apiClient from './client';
import type {
  ContributionStatus,
  CycleResponse,
  TriggerContributionRequest,
  CycleHistory,
} from '@/types';

/** Retrieves the real cycle records for a group. */
export const getGroupCycles = async (groupId: string): Promise<CycleResponse[]> => {
  const response = await apiClient.get<CycleResponse[]>(`/groups/${groupId}/cycles`);
  return response.data;
};

/**
 * Retrieves contribution status records for a specific cycle.
 * Endpoint: GET /cycles/{id}/contributions
 */
export const getContributions = async (
  id: string
): Promise<ContributionStatus[]> => {
  const response = await apiClient.get<ContributionStatus[]>(`/cycles/${id}/contributions`);
  return response.data;
};

/**
 * Initiates a MoMo contribution payment for a member within an active cycle.
 * Endpoint: POST /contributions
 */
export const triggerContribution = async (
  request: TriggerContributionRequest
): Promise<ContributionStatus> => {
  const response = await apiClient.post<ContributionStatus>('/contributions', request);
  return response.data;
};

/**
 * Retrieves the status of a specific contribution by its ID.
 * Endpoint: GET /contributions/{id}
 */
export const getContributionStatus = async (
  id: string
): Promise<ContributionStatus> => {
  const response = await apiClient.get<ContributionStatus>(`/contributions/${id}`);
  return response.data;
};

/**
 * Retrieves cycle contribution and payout history for a group.
 * Endpoint: GET /groups/{id}/history (with fallback to /groups/{id}/cycles)
 */
export const getGroupCycleHistory = async (
  id: string
): Promise<CycleHistory[]> => {
  try {
    const response = await apiClient.get<CycleHistory[]>(`/groups/${id}/history`);
    return response.data;
  } catch (err) {
    // If backend uses /cycles route instead of /history
    const response = await apiClient.get<CycleHistory[]>(`/groups/${id}/cycles`);
    return response.data;
  }
};

/**
 * Alias for getGroupCycleHistory adhering directly to GET /groups/{id}/history.
 */
export const getGroupHistory = getGroupCycleHistory;
