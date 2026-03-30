'use client';

import { useEffect, useState, useCallback } from 'react';
import { useParams } from 'next/navigation';
import ProtectedRoute from '@/components/protected-route';
import DealStatusBadge from '@/components/deal-status-badge';
import { useAuth } from '@/contexts/auth-context';
import {
  apiGetDeal,
  apiGetDealEvents,
  apiDealAction,
  type DealResponse,
  type DealEventResponse,
  type DealStatus,
} from '@/lib/api';
import Link from 'next/link';

export default function DealDetailPage() {
  return (
    <ProtectedRoute>
      <DealDetailContent />
    </ProtectedRoute>
  );
}

function DealDetailContent() {
  const { id } = useParams<{ id: string }>();
  const { user } = useAuth();
  const [deal, setDeal] = useState<DealResponse | null>(null);
  const [events, setEvents] = useState<DealEventResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [actionLoading, setActionLoading] = useState('');
  const [confirmAction, setConfirmAction] = useState<{ action: string; label: string; body?: object } | null>(null);

  const loadDeal = useCallback(() => {
    Promise.all([apiGetDeal(id), apiGetDealEvents(id)])
      .then(([d, e]) => { setDeal(d); setEvents(e); })
      .catch((e) => setError(e.message))
      .finally(() => setLoading(false));
  }, [id]);

  useEffect(() => { loadDeal(); }, [loadDeal]);

  const executeAction = async (action: string, body?: object) => {
    setActionLoading(action);
    setConfirmAction(null);
    try {
      const updated = await apiDealAction(id, action, body);
      setDeal(updated);
      const newEvents = await apiGetDealEvents(id);
      setEvents(newEvents);
    } catch (e) {
      alert(e instanceof Error ? e.message : 'Action failed');
    } finally {
      setActionLoading('');
    }
  };

  if (loading) {
    return (
      <div className="flex justify-center py-12">
        <div className="h-8 w-8 animate-spin rounded-full border-4 border-gray-300 border-t-blue-600" />
      </div>
    );
  }

  if (error || !deal) {
    return (
      <div className="rounded-lg bg-red-50 p-6 text-center text-sm text-red-700">
        {error || 'Deal not found'}
        <Link href="/deals" className="ml-2 underline">Back to deals</Link>
      </div>
    );
  }

  const isDepositor = deal.depositorId === user?.id;
  const isBeneficiary = deal.beneficiaryId === user?.id;
  const isOperator = user?.role === 'OPERATOR' || user?.role === 'ADMINISTRATOR';

  return (
    <>
      {/* Header */}
      <div className="mb-6">
        <Link href="/deals" className="text-sm text-gray-500 hover:text-gray-700">&larr; Back to deals</Link>
        <div className="mt-2 flex items-center gap-3">
          <h1 className="text-2xl font-bold text-gray-900">{deal.title}</h1>
          <DealStatusBadge status={deal.status} />
        </div>
        <p className="mt-1 text-sm font-mono text-gray-400">ID: {deal.id}</p>
      </div>

      <div className="grid gap-6 lg:grid-cols-3">
        {/* Left: Info + Timeline */}
        <div className="space-y-6 lg:col-span-2">
          {/* Deal Info */}
          <div className="rounded-lg bg-white p-6 shadow-sm">
            <h2 className="mb-4 text-lg font-semibold text-gray-900">Deal Information</h2>
            <dl className="grid gap-4 sm:grid-cols-2">
              <div>
                <dt className="text-sm font-medium text-gray-500">Amount</dt>
                <dd className="mt-1 text-lg font-semibold text-gray-900">
                  {Number(deal.amount).toLocaleString()} {deal.currency}
                </dd>
              </div>
              <div>
                <dt className="text-sm font-medium text-gray-500">Status</dt>
                <dd className="mt-1"><DealStatusBadge status={deal.status} /></dd>
              </div>
              <div>
                <dt className="text-sm font-medium text-gray-500">Depositor</dt>
                <dd className="mt-1 text-sm text-gray-900 font-mono">
                  {deal.depositorId.slice(0, 8)}...
                  {isDepositor && <span className="ml-1 text-blue-600">(You)</span>}
                </dd>
              </div>
              <div>
                <dt className="text-sm font-medium text-gray-500">Beneficiary</dt>
                <dd className="mt-1 text-sm text-gray-900 font-mono">
                  {deal.beneficiaryId.slice(0, 8)}...
                  {isBeneficiary && <span className="ml-1 text-blue-600">(You)</span>}
                </dd>
              </div>
              {deal.description && (
                <div className="sm:col-span-2">
                  <dt className="text-sm font-medium text-gray-500">Description</dt>
                  <dd className="mt-1 text-sm text-gray-900">{deal.description}</dd>
                </div>
              )}
              <div>
                <dt className="text-sm font-medium text-gray-500">Created</dt>
                <dd className="mt-1 text-sm text-gray-900">{new Date(deal.createdAt).toLocaleString()}</dd>
              </div>
            </dl>
          </div>

          {/* Timeline */}
          <div className="rounded-lg bg-white p-6 shadow-sm">
            <h2 className="mb-4 text-lg font-semibold text-gray-900">Timeline</h2>
            {events.length === 0 ? (
              <p className="text-sm text-gray-400">No events yet</p>
            ) : (
              <ol className="relative border-l border-gray-200 ml-3">
                {events.map((ev) => (
                  <li key={ev.id} className="mb-6 ml-6">
                    <span className="absolute -left-2 flex h-4 w-4 items-center justify-center rounded-full bg-blue-100 ring-4 ring-white">
                      <span className="h-2 w-2 rounded-full bg-blue-600" />
                    </span>
                    <div className="flex items-baseline gap-2">
                      <span className="text-sm font-medium text-gray-900">
                        {ev.eventType.replace(/_/g, ' ')}
                      </span>
                      {ev.newStatus && <DealStatusBadge status={ev.newStatus} />}
                    </div>
                    <p className="mt-0.5 text-xs text-gray-500">
                      {new Date(ev.createdAt).toLocaleString()} &middot; {ev.actorRole}
                    </p>
                  </li>
                ))}
              </ol>
            )}
          </div>
        </div>

        {/* Right: Action Panel */}
        <div className="space-y-6">
          <div className="rounded-lg bg-white p-6 shadow-sm">
            <h2 className="mb-4 text-lg font-semibold text-gray-900">Actions</h2>
            <ActionPanel
              deal={deal}
              isDepositor={isDepositor}
              isBeneficiary={isBeneficiary}
              isOperator={isOperator}
              actionLoading={actionLoading}
              onAction={(action, label, body) => setConfirmAction({ action, label, body })}
            />
          </div>
        </div>
      </div>

      {/* Confirmation Modal */}
      {confirmAction && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50">
          <div className="w-full max-w-sm rounded-lg bg-white p-6 shadow-xl">
            <h3 className="text-lg font-semibold text-gray-900">Confirm Action</h3>
            <p className="mt-2 text-sm text-gray-600">
              Are you sure you want to <strong>{confirmAction.label.toLowerCase()}</strong>?
            </p>
            <div className="mt-4 flex gap-3 justify-end">
              <button
                onClick={() => setConfirmAction(null)}
                className="rounded-md border border-gray-300 px-4 py-2 text-sm font-medium text-gray-700 hover:bg-gray-50"
              >
                Cancel
              </button>
              <button
                onClick={() => executeAction(confirmAction.action, confirmAction.body)}
                disabled={!!actionLoading}
                className="rounded-md bg-blue-600 px-4 py-2 text-sm font-medium text-white hover:bg-blue-700 disabled:opacity-50"
              >
                {actionLoading ? 'Processing...' : 'Confirm'}
              </button>
            </div>
          </div>
        </div>
      )}
    </>
  );
}

interface ActionPanelProps {
  deal: DealResponse;
  isDepositor: boolean;
  isBeneficiary: boolean;
  isOperator: boolean;
  actionLoading: string;
  onAction: (action: string, label: string, body?: object) => void;
}

function ActionPanel({ deal, isDepositor, isBeneficiary, isOperator, actionLoading, onAction }: ActionPanelProps) {
  const actions = getAvailableActions(deal.status, isDepositor, isBeneficiary, isOperator);

  if (actions.length === 0) {
    return <p className="text-sm text-gray-400">No actions available for current state</p>;
  }

  return (
    <div className="space-y-2">
      {actions.map((a, i) => (
        <button
          key={`${a.action}-${i}`}
          onClick={() => onAction(a.action, a.label, a.body)}
          disabled={!!actionLoading}
          className={`w-full rounded-md px-4 py-2 text-sm font-medium transition-colors disabled:opacity-50 ${a.style}`}
        >
          {actionLoading === a.action ? 'Processing...' : a.label}
        </button>
      ))}
    </div>
  );
}

interface ActionDef {
  action: string;
  label: string;
  style: string;
  body?: object;
}

function getAvailableActions(
  status: DealStatus,
  isDepositor: boolean,
  isBeneficiary: boolean,
  isOperator: boolean,
): ActionDef[] {
  const actions: ActionDef[] = [];
  const primary = 'bg-blue-600 text-white hover:bg-blue-700';
  const success = 'bg-green-600 text-white hover:bg-green-700';
  const danger = 'bg-red-600 text-white hover:bg-red-700';
  const secondary = 'border border-gray-300 bg-white text-gray-700 hover:bg-gray-50';

  switch (status) {
    case 'DRAFT':
      if (isDepositor) {
        actions.push({ action: 'submit', label: 'Submit for Agreement', style: primary });
        actions.push({ action: 'cancel', label: 'Cancel Deal', style: danger });
      }
      break;
    case 'AWAITING_AGREEMENT':
      if (isBeneficiary) {
        actions.push({ action: 'agree', label: 'Accept Deal', style: success });
        actions.push({ action: 'decline', label: 'Decline Deal', style: danger });
      }
      if (isDepositor) {
        actions.push({ action: 'cancel', label: 'Cancel Deal', style: secondary });
      }
      break;
    case 'AWAITING_FUNDING':
      if (isDepositor) {
        actions.push({ action: 'fund', label: 'Fund Deal', style: primary });
        actions.push({ action: 'cancel', label: 'Cancel Deal', style: secondary });
      }
      break;
    case 'FUNDING_PROCESSING':
      break;
    case 'AWAITING_FULFILLMENT':
      if (isBeneficiary) {
        actions.push({ action: 'deliver', label: 'Mark as Delivered', style: primary });
      }
      if (isDepositor || isBeneficiary) {
        actions.push({ action: 'dispute', label: 'Open Dispute', style: danger });
        actions.push({ action: 'cancel', label: 'Cancel Deal', style: secondary });
      }
      break;
    case 'AWAITING_REVIEW':
      if (isDepositor) {
        actions.push({ action: 'confirm', label: 'Confirm & Release Funds', style: success });
        actions.push({ action: 'reject', label: 'Reject & Refund', style: danger });
        actions.push({ action: 'dispute', label: 'Open Dispute', style: secondary });
      }
      break;
    case 'DISPUTED':
      if (isOperator) {
        actions.push({ action: 'resolve', label: 'Release to Beneficiary', style: success, body: { resolution: 'RELEASE' } });
        actions.push({ action: 'resolve', label: 'Refund to Depositor', style: danger, body: { resolution: 'REFUND' } });
      }
      break;
  }

  return actions;
}
