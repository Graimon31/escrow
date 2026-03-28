'use client';

import { useEffect, useState } from 'react';
import { useParams, useRouter } from 'next/navigation';
import { agreeDeal, Deal, getDeal, openEscrowAccount } from '../../../lib/deals';
import { depositFunds, FundingAudit, getFundingAudit } from '../../../lib/funding';

function readCookie(name: string): string {
  const value = document.cookie.split('; ').find((row) => row.startsWith(`${name}=`))?.split('=')[1];
  return value ? decodeURIComponent(value) : '';
}

export default function DealCardPage() {
  const params = useParams<{ id: string }>();
  const router = useRouter();
  const [deal, setDeal] = useState<Deal | null>(null);
  const [audit, setAudit] = useState<FundingAudit[]>([]);
  const [error, setError] = useState('');

  async function load() {
    const token = readCookie('auth_token');
    const data = await getDeal(token, params.id);
    setDeal(data);
    const auditData = await getFundingAudit(token, params.id).catch(() => []);
    setAudit(auditData);
  }

  useEffect(() => {
    load().catch((e) => setError((e as Error).message));
  }, [params.id]);

  async function onAgree() {
    try {
      const token = readCookie('auth_token');
      const data = await agreeDeal(token, params.id);
      setDeal(data);
    } catch (e) {
      setError((e as Error).message);
    }
  }

  async function onOpenAccount() {
    try {
      const token = readCookie('auth_token');
      const data = await openEscrowAccount(token, params.id);
      setDeal(data);
    } catch (e) {
      setError((e as Error).message);
    }
  }

  async function onDeposit() {
    try {
      const token = readCookie('auth_token');
      await depositFunds(token, params.id, deal?.amount ?? 0, deal?.currency ?? 'RUB');
      await load();
    } catch (e) {
      setError((e as Error).message);
    }
  }

  if (!deal) return <main className="p-8">Загрузка...</main>;

  return (
    <main className="mx-auto max-w-2xl p-8">
      <button className="mb-4 rounded border px-3 py-2" onClick={() => router.push('/deals')}>Назад к списку</button>
      <h1 className="text-2xl font-semibold">Карточка сделки</h1>
      <div className="mt-4 rounded border bg-white p-4">
        <p><b>ID:</b> {deal.id}</p>
        <p><b>Название:</b> {deal.title}</p>
        <p><b>Сумма:</b> {deal.amount} {deal.currency}</p>
        <p><b>Депонент:</b> {deal.depositorUsername}</p>
        <p><b>Бенефициар:</b> {deal.beneficiaryUsername}</p>
        <p><b>Статус:</b> {deal.state}</p>
      </div>
      <div className="mt-4 flex flex-wrap gap-2">
        <button onClick={onAgree} className="rounded bg-emerald-600 px-4 py-2 text-white">Согласовать (AGREED)</button>
        <button onClick={onOpenAccount} className="rounded bg-indigo-600 px-4 py-2 text-white">Открыть счёт эскроу</button>
        <button onClick={onDeposit} className="rounded bg-amber-600 px-4 py-2 text-white">Внести средства (mock)</button>
      </div>

      <section className="mt-6 rounded border bg-white p-4">
        <h2 className="text-lg font-semibold">Журнал фондирования (Audit Trail)</h2>
        <ul className="mt-2 space-y-2 text-sm">
          {audit.map((a) => (
            <li key={a.id} className="rounded bg-slate-50 p-2">
              <b>{a.eventType}</b> — {new Date(a.createdAt).toLocaleString('ru-RU')}
            </li>
          ))}
          {audit.length === 0 && <li className="text-slate-500">Пока событий нет.</li>}
        </ul>
      </section>

      {error && <p className="mt-4 text-red-700">{error}</p>}
    </main>
  );
}
