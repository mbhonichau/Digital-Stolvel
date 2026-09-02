/**
 * Centralized, type-safe TanStack Query Keys for the MTN MoMo Stokvel Mini App.
 *
 * Ensures consistent cache invalidation and refetching behavior across all hooks and components.
 */
export const QUERY_KEYS = {
  groups: {
    all: ['groups'] as const,
    detail: (id: string) => ['groups', id] as const,
  },
  members: {
    all: ['members'] as const,
    byGroup: (groupId: string) => ['members', 'group', groupId] as const,
  },
  contributions: {
    all: ['contributions'] as const,
    byCycle: (cycleId: string) => ['contributions', 'cycle', cycleId] as const,
    detail: (id: string) => ['contributions', 'detail', id] as const,
  },
  payouts: {
    all: ['payouts'] as const,
    detail: (id: string) => ['payouts', id] as const,
  },
  history: {
    all: ['history'] as const,
    byGroup: (groupId: string) => ['history', 'group', groupId] as const,
  },
};

export default QUERY_KEYS;
