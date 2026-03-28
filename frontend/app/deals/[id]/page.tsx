'use client';

import { useEffect, useState } from 'react';
import { useParams, useRouter } from 'next/navigation';
import { agreeDeal, Deal, getDeal, openEscrowAccount } from '../../../lib/deals';

function readCookie(name: string): string {
  const value = document.cookie.split('; ').find((row) => row.startsWith(`${name}=`))?.split('=')[1];
  return value ? decodeURIComponent(value) : '';
}

export default function DealCardPage() {
  const params = useParams<{ id: string }>();
  const router = useRouter();
  const [deal, setDeal] = useState<Deal | null>(null);
  const [error, setError] = useState('');

  async function load() {
    const token = readCookie('auth_token');
    const data = await getDeal(token, params.id);
    setDeal(data);
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
      <div className="mt-4 flex gap-2">
        <button onClick={onAgree} className="rounded bg-emerald-600 px-4 py-2 text-white">Согласовать (AGREED)</button>
        <button onClick={onOpenAccount} className="rounded bg-indigo-600 px-4 py-2 text-white">Открыть счёт эскроу</button>
      </div>
      {error && <p className="mt-4 text-red-700">{error}</p>}
    </main>
  );
}
