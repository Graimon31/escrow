'use client';

import { useEffect, useState } from 'react';
import ProtectedRoute from '@/components/protected-route';
import DealStatusBadge from '@/components/deal-status-badge';
import { useAuth } from '@/contexts/auth-context';
import { apiListDeals, type DealResponse, type DealStatus } from '@/lib/api';
import Link from 'next/link';

const ALL_STATUSES: DealStatus[] = [
  'DRAFT', 'AWAITING_AGREEMENT', 'AWAITING_FUNDING', 'FUNDING_PROCESSING',
  'FUNDED', 'AWAITING_FULFILLMENT', 'AWAITING_REVIEW', 'RELEASING',
  'COMPLETED', 'REFUNDING', 'REFUNDED', 'DISPUTED', 'CANCELLED', 'CLOSED',
];

const STATUS_LABELS: Record<string, string> = {
  DRAFT: 'Черновик',
  AWAITING_AGREEMENT: 'Ожидает согласия',
  AWAITING_FUNDING: 'Ожидает оплаты',
  FUNDING_PROCESSING: 'Обработка платежа',
  FUNDED: 'Оплачена',
  AWAITING_FULFILLMENT: 'Ожидает исполнения',
  AWAITING_REVIEW: 'Ожидает проверки',
  RELEASING: 'Выпуск средств',
  COMPLETED: 'Завершена',
  REFUNDING: 'Возврат средств',
  REFUNDED: 'Возвращена',
  DISPUTED: 'Спор',
  CANCELLED: 'Отменена',
  CLOSED: 'Закрыта',
};

export default function DealsPage() {
  return (
    <ProtectedRoute>
      <DealsContent />
    </ProtectedRoute>
  );
}

function DealsContent() {
  const { user } = useAuth();
  const [deals, setDeals] = useState<DealResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [statusFilter, setStatusFilter] = useState('');
  const [roleFilter, setRoleFilter] = useState('');

  useEffect(() => {
    apiListDeals()
      .then(setDeals)
      .catch((e) => setError(e.message))
      .finally(() => setLoading(false));
  }, []);

  const filtered = deals.filter((d) => {
    if (statusFilter && d.status !== statusFilter) return false;
    if (roleFilter === 'depositor' && d.depositorId !== user?.id) return false;
    if (roleFilter === 'beneficiary' && d.beneficiaryId !== user?.id) return false;
    return true;
  });

  return (
    <>
      <div className="mb-6 flex items-center justify-between">
        <h1 className="text-2xl font-bold text-gray-900">Сделки</h1>
        <Link
          href="/deals/new"
          className="rounded-md bg-blue-600 px-4 py-2 text-sm font-medium text-white hover:bg-blue-700"
        >
          Создать сделку
        </Link>
      </div>

      {/* Filters */}
      <div className="mb-6 flex flex-wrap gap-3">
        <select
          value={statusFilter}
          onChange={(e) => setStatusFilter(e.target.value)}
          className="rounded-lg border border-gray-300 bg-white px-3 py-2 text-sm focus:border-blue-500 focus:outline-none focus:ring-1 focus:ring-blue-500"
        >
          <option value="">Все статусы</option>
          {ALL_STATUSES.map((s) => (
            <option key={s} value={s}>{STATUS_LABELS[s] || s}</option>
          ))}
        </select>
        <select
          value={roleFilter}
          onChange={(e) => setRoleFilter(e.target.value)}
          className="rounded-lg border border-gray-300 bg-white px-3 py-2 text-sm focus:border-blue-500 focus:outline-none focus:ring-1 focus:ring-blue-500"
        >
          <option value="">Все роли</option>
          <option value="depositor">Как депонент</option>
          <option value="beneficiary">Как бенефициар</option>
        </select>
      </div>

      {/* Content */}
      {loading ? (
        <div className="flex justify-center py-12">
          <div className="h-8 w-8 animate-spin rounded-full border-4 border-gray-300 border-t-blue-600" />
        </div>
      ) : error ? (
        <div className="rounded-lg bg-red-50 p-6 text-center text-sm text-red-700">
          {error}
          <button onClick={() => window.location.reload()} className="ml-2 underline">
            Повторить
          </button>
        </div>
      ) : filtered.length === 0 ? (
        <div className="rounded-lg bg-white p-12 text-center shadow-sm">
          <p className="text-sm text-gray-500">Сделки не найдены</p>
          <p className="mt-1 text-xs text-gray-400">
            {deals.length === 0 ? 'Создайте первую сделку, чтобы начать.' : 'Попробуйте изменить фильтры.'}
          </p>
        </div>
      ) : (
        <div className="overflow-hidden rounded-lg bg-white shadow-sm">
          <table className="min-w-full divide-y divide-gray-200">
            <thead className="bg-gray-50">
              <tr>
                <th className="px-6 py-3 text-left text-xs font-medium uppercase tracking-wider text-gray-500">Название</th>
                <th className="px-6 py-3 text-left text-xs font-medium uppercase tracking-wider text-gray-500">Статус</th>
                <th className="px-6 py-3 text-left text-xs font-medium uppercase tracking-wider text-gray-500">Сумма</th>
                <th className="px-6 py-3 text-left text-xs font-medium uppercase tracking-wider text-gray-500">Роль</th>
                <th className="px-6 py-3 text-left text-xs font-medium uppercase tracking-wider text-gray-500">Дата</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-gray-200">
              {filtered.map((deal) => (
                <tr key={deal.id} className="cursor-pointer hover:bg-gray-50">
                  <td className="px-6 py-4">
                    <Link href={`/deals/${deal.id}`} className="text-sm font-medium text-blue-600 hover:text-blue-500">
                      {deal.title}
                    </Link>
                    <p className="text-xs text-gray-400 font-mono mt-0.5">{deal.id.slice(0, 8)}...</p>
                  </td>
                  <td className="px-6 py-4">
                    <DealStatusBadge status={deal.status} />
                  </td>
                  <td className="whitespace-nowrap px-6 py-4 text-sm text-gray-900">
                    {Number(deal.amount).toLocaleString('ru-RU')} {deal.currency}
                  </td>
                  <td className="px-6 py-4 text-sm text-gray-500">
                    {deal.depositorId === user?.id ? 'Депонент' : 'Бенефициар'}
                  </td>
                  <td className="whitespace-nowrap px-6 py-4 text-sm text-gray-500">
                    {new Date(deal.createdAt).toLocaleDateString('ru-RU')}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </>
  );
}
