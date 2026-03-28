const base = process.env.NEXT_PUBLIC_FULFILLMENT_SERVICE_URL || 'http://localhost:8084';

export async function submitFulfillment(token: string, dealId: string, description: string) {
  const payload = {
    dealId,
    description,
    documents: [
      { fileName: 'proof.pdf', contentType: 'application/pdf', sizeBytes: 1024 }
    ]
  };
  const res = await fetch(`${base}/api/v1/fulfillment/submit`, {
    method: 'POST',
    headers: { Authorization: `Bearer ${token}`, 'Content-Type': 'application/json' },
    body: JSON.stringify(payload)
  });
  if (!res.ok) throw new Error(await res.text());
  return res.json();
}

export async function getFulfillment(token: string, dealId: string) {
  const res = await fetch(`${base}/api/v1/fulfillment/${dealId}`, {
    headers: { Authorization: `Bearer ${token}` },
    cache: 'no-store'
  });
  if (!res.ok) return null;
  return res.json();
}
