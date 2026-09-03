import apiClient from './client';
import type { AddGroupMemberRequest, CreateGroupRequest, GroupResponse, JoinGroupRequest } from '@/types';

/**
 * Creates a new Stokvel group.
 * Endpoint: POST /groups
 */
export const createGroup = async (
  request: CreateGroupRequest
): Promise<GroupResponse> => {
  const response = await apiClient.post<GroupResponse>('/groups', request);
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
 * Joins an existing Stokvel group using an invite code or group ID.
 * Endpoint: POST /groups/{id}/join
 */
export const joinGroup = async (
  id: string,
  request: JoinGroupRequest
): Promise<GroupResponse> => {
  const response = await apiClient.post<GroupResponse>(`/groups/${id}/join`, request);
  return response.data;
};

/** Adds a member on behalf of the group creator. */
export const addGroupMember = async (
  id: string,
  request: AddGroupMemberRequest
): Promise<GroupResponse> => {
  const response = await apiClient.post<GroupResponse>(`/groups/${id}/members`, request);
  return response.data;
};

/**
 * Joins a Stokvel group directly by invite code when group ID is not yet resolved.
 * Endpoint: POST /groups/join
 */
export const joinGroupByInviteCode = async (
  request: JoinGroupRequest
): Promise<GroupResponse> => {
  const response = await apiClient.post<GroupResponse>('/groups/join', request);
  return response.data;
};
