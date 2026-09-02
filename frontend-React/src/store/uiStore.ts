import { create } from 'zustand';

export type MiniAppTab = 'overview' | 'contributions' | 'members' | 'history';

export interface UiNotification {
  id: string;
  message: string;
  type: 'info' | 'success' | 'warning' | 'error';
}

/**
 * Pure UI/Client State Store.
 *
 * STRICT ARCHITECTURAL BOUNDARY:
 * - Server-owned data (groups, members, contributions, payouts, history) belongs EXCLUSIVELY to TanStack Query.
 * - This Zustand store is reserved strictly for ephemeral UI state (e.g. active group selector, modal toggles, current tab).
 */
interface UiState {
  activeGroupId: string | null;
  activeTab: MiniAppTab;
  isCreateModalOpen: boolean;
  isJoinModalOpen: boolean;
  notification: UiNotification | null;

  // Actions
  setActiveGroupId: (id: string | null) => void;
  setActiveTab: (tab: MiniAppTab) => void;
  setCreateModalOpen: (isOpen: boolean) => void;
  setJoinModalOpen: (isOpen: boolean) => void;
  showNotification: (message: string, type?: UiNotification['type']) => void;
  clearNotification: () => void;
}

export const useUiStore = create<UiState>((set) => ({
  activeGroupId: null,
  activeTab: 'overview',
  isCreateModalOpen: false,
  isJoinModalOpen: false,
  notification: null,

  setActiveGroupId: (id: string | null) => set({ activeGroupId: id }),
  setActiveTab: (tab: MiniAppTab) => set({ activeTab: tab }),
  setCreateModalOpen: (isOpen: boolean) => set({ isCreateModalOpen: isOpen }),
  setJoinModalOpen: (isOpen: boolean) => set({ isJoinModalOpen: isOpen }),

  showNotification: (message: string, type: UiNotification['type'] = 'info') =>
    set({
      notification: {
        id: Date.now().toString(),
        message,
        type,
      },
    }),

  clearNotification: () => set({ notification: null }),
}));

export default useUiStore;
