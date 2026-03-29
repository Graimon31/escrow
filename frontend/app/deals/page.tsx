'use client';

import { FormEvent, useEffect, useState } from 'react';
import Link from 'next/link';
import { createDeal, Deal, listDeals } from '../../lib/deals';

function readCookie(name: string): string {
  const value = document.cookie.split('; ').find((row) => row.startsWith(`${name}=`))?.split('=')[1];
  return value ? decodeURIComponent(value) : '';
}

export default function DealsPage() {
  const [deals, setDeals] = useState<Deal[]>([]);
  const [title, setTitle] = useState('Разработка прототипа');
  const [amount, setAmount] = useState('10000');
  const [currency, setCurrency] = useState('RUB');
  const [beneficiary, setBeneficiary] = useState('beneficiary');
  const [error, setError] = useState('');

  async function load() {
    const token = readCookie('auth_token');
    if (!token) return;
    setDeals(await listDeals(token));
  }

  useEffect(() => {
    load().catch((e) => setError((e as Error).message));
  }, []);

  async function onCreate(e: FormEvent) {
    e.preventDefault();
    setError('');
    try {
      const token = readCookie('auth_token');
      await createDeal(token, {
        title,
        amount: Number(amount),
        currency,
        beneficiaryUsername: beneficiary
      });
      await load();
    } catch (e) {
      setError((e as Error).message);
    }
  }

  return (
    <main className="mx-auto max-w-4xl p-8">
      <h1 className="text-2xl font-semibold">Сделки эскроу (Escrow Deals)</h1>

      <form onSubmit={onCreate} className="mt-6 grid gap-3 rounded border bg-white p-4 md:grid-cols-4">
        <input className="rounded border p-2" value={title} onChange={(e) => setTitle(e.target.value)} placeholder="Название сделки" />
        <input className="rounded border p-2" value={amount} onChange={(e) => setAmount(e.target.value)} placeholder="Сумма" />
        <input className="rounded border p-2" value={currency} onChange={(e) => setCurrency(e.target.value)} placeholder="Валюта" />
        <input className="rounded border p-2" value={beneficiary} onChange={(e) => setBeneficiary(e.target.value)} placeholder="Бенефициар" />
        <button className="rounded bg-blue-600 px-4 py-2 text-white md:col-span-4">Создать сделку</button>
      </form>

      {error && <p className="mt-4 text-red-700">{error}</p>}

      <div className="mt-6 space-y-3">
        {deals.map((deal) => (
          <Link key={deal.id} href={`/deals/${deal.id}`} className="block rounded border bg-white p-4 hover:bg-slate-50">
            <div className="font-medium">{deal.title}</div>
            <div className="text-sm text-slate-700">{deal.amount} {deal.currency}</div>
            <div className="text-sm">Статус: {deal.state}</div>
          </Link>
        ))}
      </div>
    </main>
  );
}
