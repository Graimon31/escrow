'use client';

import { useState, FormEvent } from 'react';
import { useRouter } from 'next/navigation';
import ProtectedRoute from '@/components/protected-route';
import { apiCreateDeal } from '@/lib/api';
import Link from 'next/link';

export default function CreateDealPage() {
  return (
    <ProtectedRoute>
      <CreateDealContent />
    </ProtectedRoute>
  );
}

function CreateDealContent() {
  const router = useRouter();
  const [title, setTitle] = useState('');
  const [description, setDescription] = useState('');
  const [amount, setAmount] = useState('');
  const [currency, setCurrency] = useState('RUB');
  const [beneficiaryId, setBeneficiaryId] = useState('');
  const [error, setError] = useState('');
  const [submitting, setSubmitting] = useState(false);

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();
    setError('');
    setSubmitting(true);
    try {
      const deal = await apiCreateDeal({
        title,
        description: description || undefined,
        amount: parseFloat(amount),
        currency,
        beneficiaryId,
      });
      router.push(`/deals/${deal.id}`);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Не удалось создать сделку');
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <>
      <div className="mb-6">
        <Link href="/deals" className="text-sm text-gray-500 hover:text-gray-700">&larr; К списку сделок</Link>
        <h1 className="mt-2 text-2xl font-bold text-gray-900">Создание новой сделки</h1>
      </div>

      <div className="mx-auto max-w-2xl">
        <form onSubmit={handleSubmit} className="rounded-lg bg-white p-6 shadow-sm space-y-5">
          {error && (
            <div className="rounded-md bg-red-50 p-3 text-sm text-red-700">{error}</div>
          )}

          <div>
            <label htmlFor="title" className="block text-sm font-medium text-gray-700">
              Название <span className="text-red-500">*</span>
            </label>
            <input
              id="title"
              type="text"
              required
              value={title}
              onChange={(e) => setTitle(e.target.value)}
              className="mt-1 block w-full rounded-md border border-gray-300 px-3 py-2 shadow-sm focus:border-blue-500 focus:outline-none focus:ring-1 focus:ring-blue-500"
              placeholder="Например: Разработка веб-сайта"
            />
          </div>

          <div>
            <label htmlFor="description" className="block text-sm font-medium text-gray-700">
              Описание
            </label>
            <textarea
              id="description"
              rows={3}
              value={description}
              onChange={(e) => setDescription(e.target.value)}
              className="mt-1 block w-full rounded-md border border-gray-300 px-3 py-2 shadow-sm focus:border-blue-500 focus:outline-none focus:ring-1 focus:ring-blue-500"
              placeholder="Опишите условия сделки..."
            />
          </div>

          <div className="grid gap-4 sm:grid-cols-2">
            <div>
              <label htmlFor="amount" className="block text-sm font-medium text-gray-700">
                Сумма <span className="text-red-500">*</span>
              </label>
              <input
                id="amount"
                type="number"
                required
                min="0.01"
                step="0.01"
                value={amount}
                onChange={(e) => setAmount(e.target.value)}
                className="mt-1 block w-full rounded-md border border-gray-300 px-3 py-2 shadow-sm focus:border-blue-500 focus:outline-none focus:ring-1 focus:ring-blue-500"
                placeholder="10000.00"
              />
            </div>
            <div>
              <label htmlFor="currency" className="block text-sm font-medium text-gray-700">
                Валюта
              </label>
              <select
                id="currency"
                value={currency}
                onChange={(e) => setCurrency(e.target.value)}
                className="mt-1 block w-full rounded-md border border-gray-300 px-3 py-2 shadow-sm focus:border-blue-500 focus:outline-none focus:ring-1 focus:ring-blue-500"
              >
                <option value="RUB">RUB</option>
                <option value="USD">USD</option>
                <option value="EUR">EUR</option>
              </select>
            </div>
          </div>

          <div>
            <label htmlFor="beneficiaryId" className="block text-sm font-medium text-gray-700">
              ID бенефициара <span className="text-red-500">*</span>
            </label>
            <input
              id="beneficiaryId"
              type="text"
              required
              value={beneficiaryId}
              onChange={(e) => setBeneficiaryId(e.target.value)}
              className="mt-1 block w-full rounded-md border border-gray-300 px-3 py-2 font-mono text-sm shadow-sm focus:border-blue-500 focus:outline-none focus:ring-1 focus:ring-blue-500"
              placeholder="UUID бенефициара"
            />
            <p className="mt-1 text-xs text-gray-400">
              Бенефициар должен быть зарегистрированным пользователем. Введите его ID.
            </p>
          </div>

          <div className="flex gap-3 pt-2">
            <button
              type="submit"
              disabled={submitting}
              className="rounded-md bg-blue-600 px-6 py-2 text-sm font-medium text-white hover:bg-blue-700 disabled:opacity-50"
            >
              {submitting ? 'Создание...' : 'Создать сделку'}
            </button>
            <Link
              href="/deals"
              className="rounded-md border border-gray-300 px-6 py-2 text-sm font-medium text-gray-700 hover:bg-gray-50"
            >
              Отмена
            </Link>
          </div>
        </form>
      </div>
    </>
  );
}
