const API_BASE_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080';

export interface UserResponse {
  id: string;
  email: string;
  fullName: string;
  role: 'DEPOSITOR' | 'BENEFICIARY' | 'OPERATOR' | 'ADMINISTRATOR';
  enabled: boolean;
}

export interface AuthResponse {
  accessToken: string;
  refreshToken: string;
  user: UserResponse;
}

export interface RegisterRequest {
  email: string;
  password: string;
  fullName: string;
  role: string;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface ApiError {
  error: string;
}

async function apiFetch<T>(
  path: string,
  options: RequestInit = {},
): Promise<T> {
  const headers: Record<string, string> = {
    'Content-Type': 'application/json',
    ...((options.headers as Record<string, string>) || {}),
  };

  const token =
    typeof window !== 'undefined' ? localStorage.getItem('accessToken') : null;
  if (token) {
    headers['Authorization'] = `Bearer ${token}`;
  }

  const res = await fetch(`${API_BASE_URL}${path}`, {
    ...options,
    headers,
  });

  if (!res.ok) {
    const body = await res.json().catch(() => ({ error: 'Request failed' }));
    throw new Error(body.error || `HTTP ${res.status}`);
  }

  return res.json();
}

export function apiRegister(data: RegisterRequest): Promise<AuthResponse> {
  return apiFetch('/api/auth/register', {
    method: 'POST',
    body: JSON.stringify(data),
  });
}

export function apiLogin(data: LoginRequest): Promise<AuthResponse> {
  return apiFetch('/api/auth/login', {
    method: 'POST',
    body: JSON.stringify(data),
  });
}

export function apiRefresh(refreshToken: string): Promise<AuthResponse> {
  return apiFetch('/api/auth/refresh', {
    method: 'POST',
    body: JSON.stringify({ refreshToken }),
  });
}

export function apiGetMe(): Promise<UserResponse> {
  return apiFetch('/api/auth/me');
}

// ── Deal types ───────────────────────────────────────────────

export type DealStatus =
  | 'DRAFT'
  | 'AWAITING_AGREEMENT'
  | 'AGREED'
  | 'AWAITING_FUNDING'
  | 'FUNDING_PROCESSING'
  | 'FUNDED'
  | 'AWAITING_FULFILLMENT'
  | 'AWAITING_REVIEW'
  | 'RELEASING'
  | 'COMPLETED'
  | 'REFUNDING'
  | 'REFUNDED'
  | 'DISPUTED'
  | 'CANCELLED'
  | 'CLOSED';

export interface DealResponse {
  id: string;
  title: string;
  description: string | null;
  amount: number;
  currency: string;
  depositorId: string;
  beneficiaryId: string;
  status: DealStatus;
  createdAt: string;
  updatedAt: string;
}

export interface DealEventResponse {
  id: string;
  eventType: string;
  actorId: string;
  actorRole: string;
  previousStatus: DealStatus | null;
  newStatus: DealStatus;
  createdAt: string;
}

export interface CreateDealRequest {
  title: string;
  description?: string;
  amount: number;
  currency?: string;
  beneficiaryId: string;
}

// ── Deal API ─────────────────────────────────────────────────

export function apiListDeals(): Promise<DealResponse[]> {
  return apiFetch('/api/deals');
}

export function apiGetDeal(id: string): Promise<DealResponse> {
  return apiFetch(`/api/deals/${id}`);
}

export function apiGetDealEvents(id: string): Promise<DealEventResponse[]> {
  return apiFetch(`/api/deals/${id}/events`);
}

export function apiCreateDeal(data: CreateDealRequest): Promise<DealResponse> {
  return apiFetch('/api/deals', {
    method: 'POST',
    body: JSON.stringify(data),
  });
}

export function apiDealAction(id: string, action: string, body?: object): Promise<DealResponse> {
  return apiFetch(`/api/deals/${id}/${action}`, {
    method: 'POST',
    body: body ? JSON.stringify(body) : undefined,
  });
}

// ── Notification types ──────────────────────────────────────

export interface NotificationResponse {
  id: string;
  userId: string;
  type: string;
  title: string;
  message: string;
  dealId: string | null;
  read: boolean;
  createdAt: string;
}

// ── Notification API ────────────────────────────────────────

export function apiGetNotifications(): Promise<NotificationResponse[]> {
  return apiFetch('/api/notifications');
}

export function apiGetUnreadCount(): Promise<{ count: number }> {
  return apiFetch('/api/notifications/unread-count');
}

export function apiMarkNotificationRead(id: string): Promise<NotificationResponse> {
  return apiFetch(`/api/notifications/${id}/read`, { method: 'PATCH' });
}

export function apiMarkAllNotificationsRead(): Promise<void> {
  return apiFetch('/api/notifications/read-all', { method: 'POST' });
}

// ── Admin API ───────────────────────────────────────────────

export function apiAdminListUsers(): Promise<UserResponse[]> {
  return apiFetch('/api/admin/users');
}

export function apiAdminChangeRole(userId: string, role: string): Promise<UserResponse> {
  return apiFetch(`/api/admin/users/${userId}/role`, {
    method: 'PATCH',
    body: JSON.stringify({ role }),
  });
}

export function apiAdminToggleUser(userId: string): Promise<UserResponse> {
  return apiFetch(`/api/admin/users/${userId}/toggle`, { method: 'PATCH' });
}

// ── Operator API ────────────────────────────────────────────

export function apiOperatorListDeals(status?: string): Promise<DealResponse[]> {
  const query = status ? `?status=${status}` : '';
  return apiFetch(`/api/operator/deals${query}`);
}

// ── Escrow Account API ──────────────────────────────────────

export interface EscrowAccountResponse {
  id: string;
  dealId: string;
  depositorId: string;
  beneficiaryId: string;
  amount: number;
  currency: string;
  status: string;
  createdAt: string;
  fundedAt: string | null;
  releasedAt: string | null;
  refundedAt: string | null;
}

export function apiGetEscrowAccount(dealId: string): Promise<EscrowAccountResponse> {
  return apiFetch(`/api/payments/escrow/${dealId}`);
}

export function apiGetLedgerEntries(dealId: string): Promise<unknown[]> {
  return apiFetch(`/api/payments/escrow/${dealId}/ledger`);
}

export function apiGetUserAccount(): Promise<{ id: string; userId: string; balance: number; currency: string }> {
  return apiFetch('/api/payments/account');
}

// ── Document API ────────────────────────────────────────────

export interface DocumentResponse {
  id: string;
  dealId: string;
  uploaderId: string;
  fileName: string;
  contentType: string;
  fileSize: number;
  documentType: string;
  createdAt: string;
}

export function apiGetDealDocuments(dealId: string): Promise<DocumentResponse[]> {
  return apiFetch(`/api/documents/deal/${dealId}`);
}

export function apiUploadDocument(dealId: string, file: File, documentType?: string): Promise<DocumentResponse> {
  const formData = new FormData();
  formData.append('file', file);
  formData.append('dealId', dealId);
  if (documentType) formData.append('documentType', documentType);

  const token = typeof window !== 'undefined' ? localStorage.getItem('accessToken') : null;
  return fetch(`${API_BASE_URL}/api/documents/upload?dealId=${dealId}${documentType ? '&documentType=' + documentType : ''}`, {
    method: 'POST',
    headers: token ? { Authorization: `Bearer ${token}` } : {},
    body: formData,
  }).then(async (res) => {
    if (!res.ok) {
      const body = await res.json().catch(() => ({ error: 'Upload failed' }));
      throw new Error(body.error || `HTTP ${res.status}`);
    }
    return res.json();
  });
}

// ── Operator API ────────────────────────────────────────────

export function apiOperatorGetStats(): Promise<{
  total: number;
  disputed: number;
  active: number;
  completed: number;
}> {
  return apiFetch('/api/operator/deals/stats');
}
