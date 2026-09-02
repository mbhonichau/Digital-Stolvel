/**
 * MTN MoMo Stokvel Mini App — Centralized API Contracts and Domain Types
 * Note: ZERO mock data or fake entities. These contracts directly mirror the Spring Boot backend REST API.
 */

// ==========================================
// User & Authentication Contracts
// ==========================================

export interface UserProfile {
  id: string;
  msisdn: string;
  name: string;
  email?: string;
  role?: string;
}

// ==========================================
// Stokvel Group Contracts
// ==========================================

export interface CreateGroupRequest {
  name: string;
  contributionAmount: number;
  frequency: 'weekly' | 'monthly';
  startDate: string;
}

export interface MemberSummary {
  id: string;
  displayName: string;
  msisdn: string;
  joinOrder: number;
}

export interface GroupResponse {
  id: string;
  name: string;
  contributionAmount: number;
  frequency: 'weekly' | 'monthly';
  startDate: string;
  inviteCode: string;
  createdAt: string;
  members: MemberSummary[];
}

export interface JoinGroupRequest {
  inviteCode: string;
  msisdn: string;
  displayName: string;
}

// ==========================================
// Contribution & Payout Contracts
// ==========================================

export interface ContributionStatus {
  id: string;
  memberId: string;
  displayName: string;
  amount: number;
  status: 'pending' | 'paid' | 'failed';
  paidAt: string | null;
  momoReference: string | null;
}

export interface TriggerContributionRequest {
  cycleId: string;
  memberId: string;
}

export interface TriggerPayoutResponse {
  id: string;
  recipientMemberId: string;
  recipientName: string;
  amount: number;
  status: 'pending' | 'paid' | 'failed';
}

// ==========================================
// Cycle History Contracts
// ==========================================

export interface CycleHistory {
  cycleNumber: number;
  dueDate: string;
  totalContributed: number;
  payoutRecipient: string;
  status: 'open' | 'closed';
}

// ==========================================
// Infrastructure & Envelope Contracts
// ==========================================

export interface ApiError {
  message: string;
  statusCode?: number;
  code?: string;
  details?: Record<string, string[] | string>;
  timestamp?: string;
}

export interface ApiResponse<T> {
  data: T;
  message?: string;
  success: boolean;
}

export interface PaginationParams {
  page?: number;
  size?: number;
  sort?: string;
}

export interface PaginatedResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}
