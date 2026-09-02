import apiClient from './client';
import type {
  ContributionStatus,
  TriggerContributionRequest,
  CycleHistory,
} from '@/types';

/**
 * Retrieves contribution status records for a specific cycle.
 * Endpoint: GET /cycles/{id}/contributions
 */
export const getContributions = async (
  id: string
): Promise<ContributionStatus[]> => {
  const response = await apiClient.get<ContributionStatus[]>(`/cycles/${id}/contributions`);
  return Array.isArray(response.data) ? response.data : [];
};

/**
 * Initiates a MoMo contribution payment for a member within an active cycle.
 * Endpoint: POST /contributions
 */
export const triggerContribution = async (
  request: TriggerContributionRequest
): Promise<ContributionStatus> => {
  const payload = {
    cycleId: request.cycleId,
    memberId: request.memberId,
    amount: request.amount || 100,
    paymentMethod: request.paymentMethod || 'MOMO',
    paymentReference: request.paymentReference || `MOMO-${Date.now()}`,
  };
  const response = await apiClient.post<ContributionStatus>('/contributions', payload);
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
 * Endpoint: GET /groups/{id}/history
 */
export const getGroupCycleHistory = async (
  id: string
): Promise<CycleHistory[]> => {
  const response = await apiClient.get<CycleHistory[]>(`/groups/${id}/history`);
  return Array.isArray(response.data) ? response.data : [];
};

/**
 * Alias for getGroupCycleHistory adhering directly to GET /groups/{id}/history.
 */
export const getGroupHistory = getGroupCycleHistory;
