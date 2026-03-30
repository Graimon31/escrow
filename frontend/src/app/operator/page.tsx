'use client';

import { useEffect, useState } from 'react';
import ProtectedRoute from '@/components/protected-route';
import { useAuth } from '@/contexts/auth-context';
import {
  apiOperatorListDeals,
  apiOperatorGetStats,
  apiDealAction,
  type DealResponse,
} from '@/lib/api';
import DealStatusBadge from '@/components/deal-status-badge';
import Link from 'next/link';

export default function OperatorPage() {
  return (
    <ProtectedRoute>
      <OperatorContent />
    </ProtectedRoute>
  );
}

const STATUS_FILTERS = [
  { value: '', label: 'All Deals' },
  { value: 'DISPUTED', label: 'Disputed' },
  { value: 'AWAITING_REVIEW', label: 'Awaiting Review' },
  { value: 'AWAITING_FULFILLMENT', label: 'Awaiting Fulfillment' },
  { value: 'FUNDED', label: 'Funded' },
  { value: 'RELEASING', label: 'Releasing' },
  { value: 'REFUNDING', label: 'Refunding' },
];

function OperatorContent() {
  const { user } = useAuth();
  const [deals, setDeals] = useState<DealResponse[]>([]);
  const [stats, setStats] = useState<{ total: number; disputed: number; active: number; completed: number } | null>(null);
  const [statusFilter, setStatusFilter] = useState('');
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [resolveModal, setResolveModal] = useState<{ dealId: string; title: string } | null>(null);

  const isAllowed = user?.role === 'OPERATOR' || user?.role === 'ADMINISTRATOR';

  useEffect(() => {
    if (!isAllowed) return;
    setLoading(true);
    Promise.all([
      apiOperatorListDeals(statusFilter || undefined),
      apiOperatorGetStats(),
    ])
      .then(([d, s]) => { setDeals(d); setStats(s); })
      .catch((e) => setError(e.message))
      .finally(() => setLoading(false));
  }, [isAllowed, statusFilter]);

  if (!isAllowed) {
    return (
      <div className="rounded-lg bg-red-50 p-6 text-center text-sm text-red-700">
        Access denied. Operator or Administrator role required.
      </div>
    );
  }

  const handleResolve = async (dealId: string, resolution: string) => {
    try {
      await apiDealAction(dealId, 'resolve', { resolution });
      setResolveModal(null);
      // Refresh
      const [d, s] = await Promise.all([
        apiOperatorListDeals(statusFilter || undefined),
        apiOperatorGetStats(),
      ]);
      setDeals(d);
      setStats(s);
    } catch (e: unknown) {
      alert((e as Error).message);
    }
  };

  return (
    <>
      <h1 className="mb-6 text-2xl font-bold text-gray-900">Operator Panel</h1>

      {stats && (
        <div className="mb-6 grid grid-cols-2 gap-4 md:grid-cols-4">
          <StatCard label="Total Deals" value={stats.total} />
          <StatCard label="Active" value={stats.active} color="blue" />
          <StatCard label="Disputed" value={stats.disputed} color="red" />
          <StatCard label="Completed" value={stats.completed} color="green" />
        </div>
      )}

      <div className="mb-4 flex items-center gap-3">
        <label className="text-sm font-medium text-gray-700">Filter:</label>
        <select
          value={statusFilter}
          onChange={(e) => setStatusFilter(e.target.value)}
          className="rounded-md border border-gray-300 px-3 py-1.5 text-sm"
        >
          {STATUS_FILTERS.map((f) => (
            <option key={f.value} value={f.value}>{f.label}</option>
          ))}
        </select>
      </div>

      {loading ? (
        <div className="flex justify-center py-12">
          <div className="h-8 w-8 animate-spin rounded-full border-4 border-gray-300 border-t-blue-600" />
        </div>
      ) : error ? (
        <div className="rounded-lg bg-red-50 p-6 text-center text-sm text-red-700">{error}</div>
      ) : deals.length === 0 ? (
        <div className="rounded-lg bg-white p-12 text-center shadow-sm">
          <p className="text-sm text-gray-500">No deals found</p>
        </div>
      ) : (
        <div className="overflow-x-auto rounded-lg bg-white shadow-sm">
          <table className="min-w-full divide-y divide-gray-200">
            <thead className="bg-gray-50">
              <tr>
                <th className="px-4 py-3 text-left text-xs font-medium uppercase text-gray-500">Title</th>
                <th className="px-4 py-3 text-left text-xs font-medium uppercase text-gray-500">Amount</th>
                <th className="px-4 py-3 text-left text-xs font-medium uppercase text-gray-500">Status</th>
                <th className="px-4 py-3 text-left text-xs font-medium uppercase text-gray-500">Created</th>
                <th className="px-4 py-3 text-left text-xs font-medium uppercase text-gray-500">Actions</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-gray-200">
              {deals.map((deal) => (
                <tr key={deal.id} className="hover:bg-gray-50">
                  <td className="px-4 py-3">
                    <Link href={`/deals/${deal.id}`} className="text-sm font-medium text-blue-600 hover:text-blue-800">
                      {deal.title}
                    </Link>
                  </td>
                  <td className="px-4 py-3 text-sm text-gray-600">
                    {deal.amount.toLocaleString()} {deal.currency}
                  </td>
                  <td className="px-4 py-3"><DealStatusBadge status={deal.status} /></td>
                  <td className="px-4 py-3 text-sm text-gray-500">
                    {new Date(deal.createdAt).toLocaleDateString()}
                  </td>
                  <td className="px-4 py-3">
                    {deal.status === 'DISPUTED' && (
                      <button
                        onClick={() => setResolveModal({ dealId: deal.id, title: deal.title })}
                        className="rounded-md bg-orange-100 px-3 py-1 text-xs font-medium text-orange-700 hover:bg-orange-200"
                      >
                        Resolve Dispute
                      </button>
                    )}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

      {resolveModal && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50 p-4">
          <div className="w-full max-w-md rounded-lg bg-white p-6 shadow-lg">
            <h3 className="mb-2 text-lg font-semibold text-gray-900">Resolve Dispute</h3>
            <p className="mb-4 text-sm text-gray-600">Deal: {resolveModal.title}</p>
            <p className="mb-4 text-sm text-gray-600">Choose resolution:</p>
            <div className="flex gap-3">
              <button
                onClick={() => handleResolve(resolveModal.dealId, 'RELEASE')}
                className="flex-1 rounded-md bg-green-600 px-4 py-2 text-sm font-medium text-white hover:bg-green-700"
              >
                Release Funds to Beneficiary
              </button>
              <button
                onClick={() => handleResolve(resolveModal.dealId, 'REFUND')}
                className="flex-1 rounded-md bg-orange-600 px-4 py-2 text-sm font-medium text-white hover:bg-orange-700"
              >
                Refund to Depositor
              </button>
            </div>
            <button
              onClick={() => setResolveModal(null)}
              className="mt-3 w-full rounded-md border border-gray-300 px-4 py-2 text-sm text-gray-700 hover:bg-gray-50"
            >
              Cancel
            </button>
          </div>
        </div>
      )}
    </>
  );
}

function StatCard({ label, value, color }: { label: string; value: number; color?: string }) {
  const colorClasses = {
    blue: 'bg-blue-50 text-blue-700',
    red: 'bg-red-50 text-red-700',
    green: 'bg-green-50 text-green-700',
  };
  const cls = color ? colorClasses[color as keyof typeof colorClasses] : 'bg-gray-50 text-gray-700';
  return (
    <div className={`rounded-lg p-4 ${cls}`}>
      <p className="text-2xl font-bold">{value}</p>
      <p className="text-sm">{label}</p>
    </div>
  );
}
