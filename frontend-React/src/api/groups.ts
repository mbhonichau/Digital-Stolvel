import apiClient from './client';
import type { CreateGroupRequest, GroupResponse, JoinGroupRequest, MemberSummary } from '@/types';

/**
 * Creates a new Stokvel group.
 * Endpoint: POST /groups
 */
export const createGroup = async (
  request: CreateGroupRequest
): Promise<GroupResponse> => {
  const payload = {
    name: request.name,
    description: request.description || `Stokvel group created for ${request.name}`,
    groupType: request.groupType || 'ROTATING',
    contributionAmount: request.contributionAmount,
    contributionFrequency: (request.contributionFrequency || request.frequency || 'MONTHLY').toUpperCase(),
    maxMembers: request.maxMembers || 12,
    creatorMemberId: request.creatorMemberId || null,
  };
  const response = await apiClient.post<GroupResponse>('/groups', payload);
  return response.data;
};

/**
 * Retrieves details for a specific Stokvel group by ID.
 * Endpoint: GET /groups/{id}
 */
export const getGroup = async (id: string): Promise<GroupResponse> => {
  const response = await apiClient.get<GroupResponse>(`/groups/${id}`);
  return response.data;
};

/**
 * Joins an existing Stokvel group using group ID and member request.
 * Endpoint: POST /groups/{id}/join
 */
export const joinGroup = async (
  id: string,
  request: JoinGroupRequest
): Promise<GroupResponse> => {
  await apiClient.post<MemberSummary>(`/groups/${id}/join`, request);
  // Refetch group details to return full updated GroupResponse
  return await getGroup(id);
};

/**
 * Joins a Stokvel group directly by invite code.
 * Endpoint: POST /groups/{id}/join (resolves group ID if needed)
 */
export const joinGroupByInviteCode = async (
  request: JoinGroupRequest
): Promise<GroupResponse> => {
  if (request.inviteCode) {
    // Search existing groups to locate matching invite code or group ID
    try {
      const allGroupsResponse = await apiClient.get<GroupResponse[]>('/groups');
      const groupsList = Array.isArray(allGroupsResponse.data) ? allGroupsResponse.data : [];
      const match = groupsList.find(
        (g) => g.inviteCode === request.inviteCode || g.id === request.inviteCode
      );
      if (match) {
        if (request.memberId) {
          return await joinGroup(match.id, request);
        }
        return match;
      }
    } catch (e) {
      // Fallback directly to join endpoint
    }
  }

  // Attempt direct post to /groups/join or /groups/{id}/join
  const targetId = request.inviteCode || 'default';
  const response = await apiClient.post<GroupResponse>(`/groups/${targetId}/join`, request);
  return response.data;
};
