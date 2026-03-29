'use client';

import { useEffect, useState } from 'react';
import { useParams } from 'next/navigation';
import { reviewAction, reviewHistory } from '../../../lib/review';

function cookie(name: string) {
  const v = document.cookie.split('; ').find((r) => r.startsWith(`${name}=`))?.split('=')[1];
  return v ? decodeURIComponent(v) : '';
}

export default function ReviewPage() {
  const { id } = useParams<{ id: string }>();
  const [comment, setComment] = useState('Решение по проверке.');
  const [history, setHistory] = useState<any[]>([]);
  const [error, setError] = useState('');

  async function load() {
    const token = cookie('auth_token');
    const data = await reviewHistory(token, id);
    setHistory(data);
  }

  useEffect(() => { load().catch((e) => setError((e as Error).message)); }, [id]);

  async function act(action: 'ACCEPT' | 'REJECT' | 'CORRECTION' | 'DISPUTE') {
    try {
      const token = cookie('auth_token');
      await reviewAction(token, id, action, comment);
      await load();
    } catch (e) {
      setError((e as Error).message);
    }
  }

  return (
    <main className="mx-auto max-w-3xl p-8">
      <h1 className="text-2xl font-semibold">Экран проверки депонентом</h1>
      <p className="mt-2 text-sm text-slate-600">Сделка: {id}</p>
      <textarea className="mt-4 w-full rounded border p-3" rows={4} value={comment} onChange={(e) => setComment(e.target.value)} />
      <div className="mt-3 flex flex-wrap gap-2">
        <button onClick={() => act('ACCEPT')} className="rounded bg-emerald-600 px-4 py-2 text-white">Accept</button>
        <button onClick={() => act('REJECT')} className="rounded bg-rose-600 px-4 py-2 text-white">Reject</button>
        <button onClick={() => act('CORRECTION')} className="rounded bg-amber-600 px-4 py-2 text-white">Correction</button>
        <button onClick={() => act('DISPUTE')} className="rounded bg-purple-700 px-4 py-2 text-white">Dispute</button>
      </div>

      <h2 className="mt-6 text-lg font-semibold">История действий</h2>
      <ul className="mt-2 space-y-2 text-sm">
        {history.map((h) => (
          <li key={h.id} className="rounded border bg-white p-2">
            <b>{h.action}</b> — {h.comment}
          </li>
        ))}
        {history.length === 0 && <li className="text-slate-500">История пока пуста.</li>}
      </ul>

      {error && <p className="mt-4 text-red-700">{error}</p>}
    </main>
  );
}
