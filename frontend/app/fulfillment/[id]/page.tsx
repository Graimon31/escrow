'use client';

import { useParams } from 'next/navigation';
import { useState } from 'react';
import { submitFulfillment } from '../../../lib/fulfillment';

function cookie(name: string) {
  const v = document.cookie.split('; ').find((r) => r.startsWith(`${name}=`))?.split('=')[1];
  return v ? decodeURIComponent(v) : '';
}

export default function FulfillmentPage() {
  const { id } = useParams<{ id: string }>();
  const [description, setDescription] = useState('Обязательство исполнено согласно договору.');
  const [result, setResult] = useState('');
  const [error, setError] = useState('');

  async function onSubmit() {
    setError('');
    try {
      const token = cookie('auth_token');
      await submitFulfillment(token, id, description);
      setResult('Исполнение отправлено.');
    } catch (e) {
      setError((e as Error).message);
    }
  }

  return (
    <main className="mx-auto max-w-2xl p-8">
      <h1 className="text-2xl font-semibold">Экран исполнения обязательства</h1>
      <p className="mt-2 text-sm text-slate-600">Сделка: {id}</p>
      <textarea className="mt-4 w-full rounded border p-3" rows={5} value={description} onChange={(e) => setDescription(e.target.value)} />
      <button onClick={onSubmit} className="mt-3 rounded bg-indigo-600 px-4 py-2 text-white">Заявить исполнение</button>
      {result && <p className="mt-3 text-emerald-700">{result}</p>}
      {error && <p className="mt-3 text-red-700">{error}</p>}
    </main>
  );
}
