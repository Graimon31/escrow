export type Deal = {
  id: string;
  title: string;
  amount: number;
  currency: string;
  depositorUsername: string;
  beneficiaryUsername: string;
  state: 'DRAFT' | 'AGREED' | 'ACCOUNT_OPENED' | 'AWAITING_FUNDING';
};

const baseUrl = process.env.NEXT_PUBLIC_DEAL_SERVICE_URL || 'http://localhost:8080';

export async function listDeals(token: string): Promise<Deal[]> {
  const res = await fetch(`${baseUrl}/api/v1/deals`, {
    headers: { Authorization: `Bearer ${token}` },
    cache: 'no-store'
  });
  if (!res.ok) throw new Error('Не удалось получить список сделок');
  return res.json();
}

export async function createDeal(token: string, payload: { title: string; amount: number; currency: string; beneficiaryUsername: string }) {
  const res = await fetch(`${baseUrl}/api/v1/deals`, {
    method: 'POST',
    headers: { Authorization: `Bearer ${token}`, 'Content-Type': 'application/json' },
    body: JSON.stringify(payload)
  });
  if (!res.ok) throw new Error(await res.text());
  return res.json() as Promise<Deal>;
}

export async function getDeal(token: string, id: string) {
  const res = await fetch(`${baseUrl}/api/v1/deals/${id}`, {
    headers: { Authorization: `Bearer ${token}` },
    cache: 'no-store'
  });
  if (!res.ok) throw new Error('Сделка не найдена');
  return res.json() as Promise<Deal>;
}

export async function agreeDeal(token: string, id: string) {
  const res = await fetch(`${baseUrl}/api/v1/deals/${id}/agree`, {
    method: 'POST',
    headers: { Authorization: `Bearer ${token}` }
  });
  if (!res.ok) throw new Error(await res.text());
  return res.json() as Promise<Deal>;
}

export async function openEscrowAccount(token: string, id: string) {
  const res = await fetch(`${baseUrl}/api/v1/deals/${id}/open-escrow-account`, {
    method: 'POST',
    headers: { Authorization: `Bearer ${token}` }
  });
  if (!res.ok) throw new Error(await res.text());
  return res.json() as Promise<Deal>;
}
