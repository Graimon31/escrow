'use client';

import { useEffect, useState } from 'react';
import { useParams } from 'next/navigation';
import { disputeHistory, openDispute, DisputeCase } from '../../../lib/dispute';

function cookie(name: string) {
  const v = document.cookie.split('; ').find((r) => r.startsWith(`${name}=`))?.split('=')[1];
  return v ? decodeURIComponent(v) : '';
}

export default function DisputePage() {
  const { id } = useParams<{ id: string }>();
  const [reason, setReason] = useState('Открываю спор по сделке.');
  const [history, setHistory] = useState<DisputeCase[]>([]);
  const [error, setError] = useState('');

  async function load() {
    const token = cookie('auth_token');
    const data = await disputeHistory(token, id);
    setHistory(data);
  }

  useEffect(() => { load().catch((e) => setError((e as Error).message)); }, [id]);

  async function onOpen() {
    try {
      const token = cookie('auth_token');
      await openDispute(token, id, reason);
      await load();
    } catch (e) {
      setError((e as Error).message);
    }
  }

  return (
    <main className="mx-auto max-w-3xl p-8">
      <h1 className="text-2xl font-semibold">Экран спора</h1>
      <p className="mt-2 text-sm text-slate-600">Сделка: {id}</p>
      <textarea className="mt-4 w-full rounded border p-3" rows={4} value={reason} onChange={(e) => setReason(e.target.value)} />
      <button onClick={onOpen} className="mt-3 rounded bg-purple-700 px-4 py-2 text-white">Открыть спор</button>

      <h2 className="mt-6 text-lg font-semibold">История споров</h2>
      <ul className="mt-2 space-y-2 text-sm">
        {history.map((h) => (
          <li key={h.id} className="rounded border bg-white p-2">
            <b>{h.status}</b> — {h.reason}
            {h.resolutionComment ? <div className="text-slate-600">Решение: {h.resolutionComment}</div> : null}
          </li>
        ))}
        {history.length === 0 && <li className="text-slate-500">Споры пока не открывались.</li>}
      </ul>

      {error && <p className="mt-4 text-red-700">{error}</p>}
    </main>
  );
}
