const base = process.env.NEXT_PUBLIC_RESOLUTION_SERVICE_URL || 'http://localhost:8087';

export type ResolutionOutcome = 'RELEASE' | 'REFUND';

export type ResolutionDecision = {
  id: string;
  dealId: string;
  outcome: ResolutionOutcome;
  actor: string;
  comment: string;
  createdAt: string;
};

export async function decideResolution(token: string, dealId: string, outcome: ResolutionOutcome, comment: string) {
  const res = await fetch(`${base}/api/v1/resolution/decide`, {
    method: 'POST',
    headers: { Authorization: `Bearer ${token}`, 'Content-Type': 'application/json' },
    body: JSON.stringify({ dealId, outcome, comment })
  });
  if (!res.ok) throw new Error(await res.text());
  return res.json() as Promise<ResolutionDecision>;
}

export async function resolutionHistory(token: string, dealId: string) {
  const res = await fetch(`${base}/api/v1/resolution/history/${dealId}`, {
    headers: { Authorization: `Bearer ${token}` },
    cache: 'no-store'
  });
  if (!res.ok) throw new Error('Не удалось получить историю resolution');
  return res.json() as Promise<ResolutionDecision[]>;
}
