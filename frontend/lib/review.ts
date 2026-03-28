const base = process.env.NEXT_PUBLIC_REVIEW_SERVICE_URL || 'http://localhost:8085';

export async function reviewAction(token: string, dealId: string, action: 'ACCEPT' | 'REJECT' | 'CORRECTION' | 'DISPUTE', comment: string) {
  const res = await fetch(`${base}/api/v1/review/action`, {
    method: 'POST',
    headers: { Authorization: `Bearer ${token}`, 'Content-Type': 'application/json' },
    body: JSON.stringify({ dealId, action, comment })
  });
  if (!res.ok) throw new Error(await res.text());
  return res.json();
}

export async function reviewHistory(token: string, dealId: string) {
  const res = await fetch(`${base}/api/v1/review/history/${dealId}`, {
    headers: { Authorization: `Bearer ${token}` },
    cache: 'no-store'
  });
  if (!res.ok) throw new Error('Не удалось получить историю проверки');
  return res.json();
}
