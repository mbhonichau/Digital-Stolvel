import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { addGroupMember, createGroup, getGroup, joinGroup, joinGroupByInviteCode } from '@/api/groups';
import type { AddGroupMemberRequest, CreateGroupRequest, GroupResponse, JoinGroupRequest, ApiError } from '@/types';

export const GROUP_QUERY_KEYS = {
  all: ['groups'] as const,
  detail: (id: string) => ['groups', id] as const,
};

/**
 * Hook to fetch details for a single Stokvel group by ID.
 * Communicates strictly with backend GET /groups/{id}.
 */
export function useGroup(groupId?: string) {
  return useQuery<GroupResponse, ApiError>({
    queryKey: GROUP_QUERY_KEYS.detail(groupId || ''),
    queryFn: () => {
      if (!groupId) {
        throw new Error('Group ID is required to fetch group details');
      }
      return getGroup(groupId);
    },
    enabled: Boolean(groupId),
  });
}

/**
 * Mutation hook to create a new Stokvel group.
 * Communicates strictly with backend POST /groups.
 */
export function useCreateGroup() {
  const queryClient = useQueryClient();

  return useMutation<GroupResponse, ApiError, CreateGroupRequest>({
    mutationFn: (request: CreateGroupRequest) => createGroup(request),
    onSuccess: (newGroup) => {
      // Prime cache for the newly created group
      queryClient.setQueryData(GROUP_QUERY_KEYS.detail(newGroup.id), newGroup);
      queryClient.invalidateQueries({ queryKey: GROUP_QUERY_KEYS.all });
    },
  });
}

/**
 * Mutation hook to join a Stokvel group by group ID and credentials.
 * Communicates strictly with backend POST /groups/{id}/join.
 */
export function useJoinGroup() {
  const queryClient = useQueryClient();

  return useMutation<
    GroupResponse,
    ApiError,
    { groupId: string; request: JoinGroupRequest }
  >({
    mutationFn: ({ groupId, request }) => joinGroup(groupId, request),
    onSuccess: (updatedGroup) => {
      queryClient.setQueryData(GROUP_QUERY_KEYS.detail(updatedGroup.id), updatedGroup);
      queryClient.invalidateQueries({ queryKey: GROUP_QUERY_KEYS.all });
    },
  });
}

/** Adds a member after the backend verifies the caller is the group administrator. */
export function useAddGroupMember() {
  const queryClient = useQueryClient();
  return useMutation<GroupResponse, ApiError, { groupId: string; request: AddGroupMemberRequest }>({
    mutationFn: ({ groupId, request }) => addGroupMember(groupId, request),
    onSuccess: (updatedGroup) => {
      queryClient.setQueryData(GROUP_QUERY_KEYS.detail(updatedGroup.id), updatedGroup);
    },
  });
}

/**
 * Mutation hook to join a Stokvel group directly by invite code.
 * Communicates strictly with backend POST /groups/join.
 */
export function useJoinGroupByInviteCode() {
  const queryClient = useQueryClient();

  return useMutation<GroupResponse, ApiError, JoinGroupRequest>({
    mutationFn: (request: JoinGroupRequest) => joinGroupByInviteCode(request),
    onSuccess: (joinedGroup) => {
      queryClient.setQueryData(GROUP_QUERY_KEYS.detail(joinedGroup.id), joinedGroup);
      queryClient.invalidateQueries({ queryKey: GROUP_QUERY_KEYS.all });
    },
  });
}
