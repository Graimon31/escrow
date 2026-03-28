const base = process.env.NEXT_PUBLIC_DISPUTE_SERVICE_URL || 'http://localhost:8086';

export type DisputeCase = {
  id: string;
  dealId: string;
  status: 'OPEN' | 'RESOLVED';
  openedBy: string;
  reason: string;
  createdAt: string;
  resolvedAt?: string;
  resolvedBy?: string;
  resolutionComment?: string;
};

export async function openDispute(token: string, dealId: string, reason: string) {
  const res = await fetch(`${base}/api/v1/disputes/open`, {
    method: 'POST',
    headers: { Authorization: `Bearer ${token}`, 'Content-Type': 'application/json' },
    body: JSON.stringify({ dealId, reason })
  });
  if (!res.ok) throw new Error(await res.text());
  return res.json() as Promise<DisputeCase>;
}

export async function disputeHistory(token: string, dealId: string) {
  const res = await fetch(`${base}/api/v1/disputes/${dealId}`, {
    headers: { Authorization: `Bearer ${token}` },
    cache: 'no-store'
  });
  if (!res.ok) throw new Error('Не удалось получить историю спора');
  return res.json() as Promise<DisputeCase[]>;
}
