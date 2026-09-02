import apiClient from './client';

/**
 * Health check endpoint service.
 * Endpoint: GET /actuator/health (fallback to /health)
 */
export const checkHealth = async (): Promise<{ status: string }> => {
  try {
    const response = await apiClient.get<{ status: string }>('/actuator/health');
    return response.data;
  } catch {
    const response = await apiClient.get<{ status: string }>('/health');
    return response.data;
  }
};
