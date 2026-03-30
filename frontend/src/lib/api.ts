const API_BASE_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080';

export interface UserResponse {
  id: string;
  email: string;
  fullName: string;
  role: 'DEPOSITOR' | 'BENEFICIARY' | 'OPERATOR' | 'ADMINISTRATOR';
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
