import apiClient from './client';
import type { TriggerPayoutResponse } from '@/types';

/**
 * Triggers a MoMo payout disbursement to the scheduled recipient for a cycle.
 * Endpoint: POST /cycles/{cycleId}/payout
 */
export const triggerPayout = async (
  cycleId: string
): Promise<TriggerPayoutResponse> => {
  const response = await apiClient.post<TriggerPayoutResponse>(`/cycles/${cycleId}/payout`);
  return response.data;
};

/**
 * Retrieves the disbursement status of a payout by its ID.
 * Endpoint: GET /payouts/{id}
 */
export const getPayoutStatus = async (
  id: string
): Promise<TriggerPayoutResponse> => {
  const response = await apiClient.get<TriggerPayoutResponse>(`/payouts/${id}`);
  return response.data;
};
