import { useQuery } from '@tanstack/react-query';
import { apiClient } from '@/api/client';
import type { GroupResponse, ApiError } from '@/types';
import { QUERY_KEYS } from './keys';

/**
 * Hook to fetch all Stokvel groups from Spring Boot backend.
 * Endpoint: GET /groups
 */
export function useGroups() {
  return useQuery<GroupResponse[], ApiError>({
    queryKey: QUERY_KEYS.groups.all,
    queryFn: async () => {
      const response = await apiClient.get<GroupResponse[]>('/groups');
      return Array.isArray(response.data) ? response.data : [];
    },
  });
}

export default useGroups;
