import apiClient from './client';
import type { CreatePayoutRequest, TriggerPayoutResponse } from '@/types';

/**
 * Triggers a MoMo payout disbursement to the scheduled recipient for a cycle.
 * Endpoint: POST /payouts
 */
export const triggerPayout = async (
  requestOrCycleId: string | CreatePayoutRequest
): Promise<TriggerPayoutResponse> => {
  const payload: CreatePayoutRequest =
    typeof requestOrCycleId === 'string'
      ? {
          cycleId: requestOrCycleId,
          memberId: requestOrCycleId,
          amount: 500,
          payoutMethod: 'MOMO',
          payoutReference: `PAYOUT-${Date.now()}`,
        }
      : {
          payoutMethod: 'MOMO',
          payoutReference: `PAYOUT-${Date.now()}`,
          ...requestOrCycleId,
        };

  const response = await apiClient.post<TriggerPayoutResponse>('/payouts', payload);
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
