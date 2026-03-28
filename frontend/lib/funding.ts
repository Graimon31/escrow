const fundingBase = process.env.NEXT_PUBLIC_FUNDING_SERVICE_URL || 'http://localhost:8083';

export type FundingAudit = {
  id: string;
  operationId: string;
  dealId: string;
  eventType: string;
  eventPayload: string;
  createdAt: string;
};

export async function depositFunds(token: string, dealId: string, amount: number, currency: string) {
  const idempotency = `${dealId}-${Date.now()}`;
  const res = await fetch(`${fundingBase}/api/v1/funding/deposit`, {
    method: 'POST',
    headers: {
      Authorization: `Bearer ${token}`,
      'Content-Type': 'application/json',
      'Idempotency-Key': idempotency
    },
    body: JSON.stringify({ dealId, amount, currency })
  });

  if (!res.ok) {
    throw new Error(await res.text());
  }

  return res.json();
}

export async function getFundingAudit(token: string, dealId: string): Promise<FundingAudit[]> {
  const res = await fetch(`${fundingBase}/api/v1/funding/audit/${dealId}`, {
    headers: { Authorization: `Bearer ${token}` },
    cache: 'no-store'
  });

  if (!res.ok) {
    throw new Error('Не удалось получить audit trail');
  }

  return res.json();
}
