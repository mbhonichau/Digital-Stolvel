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
  description?: string;
  groupType?: 'ROTATING' | 'SAVINGS' | string;
  contributionAmount: number;
  contributionFrequency?: 'WEEKLY' | 'MONTHLY' | string;
  frequency?: 'weekly' | 'monthly' | string;
  maxMembers?: number;
  startDate?: string;
  creatorMemberId?: string;
}

export interface MemberSummary {
  id: string;
  memberId?: string;
  displayName: string;
  fullName?: string;
  msisdn: string;
  phoneNumber?: string;
  joinOrder?: number;
  payoutOrder?: number;
  role?: string;
  status?: string;
}

export interface GroupResponse {
  id: string;
  name: string;
  description?: string;
  groupType?: string;
  contributionAmount: number;
  contributionFrequency?: string;
  frequency?: 'weekly' | 'monthly' | string;
  maxMembers?: number;
  currentMemberCount?: number;
  startDate?: string;
  inviteCode?: string;
  createdAt?: string;
  members: MemberSummary[];
}

export interface JoinGroupRequest {
  inviteCode?: string;
  memberId?: string;
  msisdn?: string;
  displayName?: string;
  role?: string;
}

// ==========================================
// Contribution & Payout Contracts
// ==========================================

export interface ContributionStatus {
  id: string;
  cycleId?: string;
  memberId: string;
  displayName: string;
  memberName?: string;
  amount: number;
  status: 'pending' | 'paid' | 'failed' | 'PENDING' | 'SUCCESSFUL' | 'COMPLETED' | 'FAILED' | string;
  paidAt: string | null;
  momoReference: string | null;
  paymentReference?: string | null;
}

export interface TriggerContributionRequest {
  cycleId: string;
  memberId: string;
  amount?: number;
  paymentMethod?: 'MOMO' | 'EFT' | 'CASH' | 'CARD' | string;
  paymentReference?: string;
}

export interface CreatePayoutRequest {
  cycleId: string;
  memberId: string;
  amount: number;
  payoutMethod?: 'MOMO' | 'BANK_TRANSFER' | 'CASH' | string;
  scheduledDate?: string;
  payoutReference?: string;
}

export interface TriggerPayoutResponse {
  id: string;
  cycleId?: string;
  recipientMemberId?: string;
  memberId?: string;
  recipientName?: string;
  memberName?: string;
  amount: number;
  status: 'pending' | 'paid' | 'failed' | 'PENDING' | 'SUCCESSFUL' | 'COMPLETED' | 'FAILED' | string;
  paidAt?: string | null;
}

// ==========================================
// Cycle History Contracts
// ==========================================

export interface CycleHistory {
  cycleNumber: number;
  dueDate: string;
  totalContributed: number;
  payoutRecipient: string;
  status: 'open' | 'closed' | 'OPEN' | 'CLOSED' | 'active' | 'completed' | string;
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
