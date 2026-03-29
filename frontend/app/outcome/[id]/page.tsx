'use client';

import { useEffect, useState } from 'react';
import { useParams } from 'next/navigation';
import { decideResolution, resolutionHistory, ResolutionDecision, ResolutionOutcome } from '../../../lib/resolution';
import { getDeal, Deal } from '../../../lib/deals';

function cookie(name: string) {
  const v = document.cookie.split('; ').find((r) => r.startsWith(`${name}=`))?.split('=')[1];
  return v ? decodeURIComponent(v) : '';
}

export default function OutcomePage() {
  const { id } = useParams<{ id: string }>();
  const [comment, setComment] = useState('Финальное решение по сделке.');
  const [deal, setDeal] = useState<Deal | null>(null);
  const [history, setHistory] = useState<ResolutionDecision[]>([]);
  const [error, setError] = useState('');

  async function load() {
    const token = cookie('auth_token');
    const [dealData, historyData] = await Promise.all([
      getDeal(token, id),
      resolutionHistory(token, id).catch(() => [])
    ]);
    setDeal(dealData);
    setHistory(historyData);
  }

  useEffect(() => { load().catch((e) => setError((e as Error).message)); }, [id]);

  async function decide(outcome: ResolutionOutcome) {
    try {
      const token = cookie('auth_token');
      await decideResolution(token, id, outcome, comment);
      await load();
    } catch (e) {
      setError((e as Error).message);
    }
  }

  return (
    <main className="mx-auto max-w-3xl p-8">
      <h1 className="text-2xl font-semibold">Экран финального исхода</h1>
      <p className="mt-2 text-sm text-slate-600">Сделка: {id}</p>
      <p className="mt-1 text-sm"><b>Текущий статус сделки:</b> {deal?.state ?? '...'}</p>

      <textarea className="mt-4 w-full rounded border p-3" rows={4} value={comment} onChange={(e) => setComment(e.target.value)} />
      <div className="mt-3 flex gap-2">
        <button onClick={() => decide('RELEASE')} className="rounded bg-emerald-600 px-4 py-2 text-white">Release</button>
        <button onClick={() => decide('REFUND')} className="rounded bg-rose-600 px-4 py-2 text-white">Refund</button>
      </div>

      <h2 className="mt-6 text-lg font-semibold">История финальных решений</h2>
      <ul className="mt-2 space-y-2 text-sm">
        {history.map((h) => (
          <li key={h.id} className="rounded border bg-white p-2">
            <b>{h.outcome}</b> — {h.comment}
          </li>
        ))}
        {history.length === 0 && <li className="text-slate-500">Финальные решения пока не приняты.</li>}
      </ul>

      {error && <p className="mt-4 text-red-700">{error}</p>}
    </main>
  );
}
