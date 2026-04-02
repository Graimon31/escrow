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
  apiGetEscrowAccount,
  type DealResponse,
  type DealEventResponse,
  type DealStatus,
  type EscrowAccountResponse,
} from '@/lib/api';
import Link from 'next/link';

const EVENT_LABELS: Record<string, string> = {
  DEAL_CREATED: 'Сделка создана',
  DEAL_SUBMITTED: 'Отправлена на согласование',
  DEAL_AGREED: 'Согласована',
  DEAL_DECLINED: 'Отклонена',
  DEAL_FUNDED: 'Оплачена',
  DEAL_DELIVERED: 'Исполнение подтверждено',
  DEAL_COMPLETED: 'Завершена',
  DEAL_REFUNDED: 'Возврат средств',
  DEAL_DISPUTED: 'Открыт спор',
  DEAL_CANCELLED: 'Отменена',
  DISPUTE_RESOLVED_RELEASE: 'Спор разрешён: выплата',
  DISPUTE_RESOLVED_REFUND: 'Спор разрешён: возврат',
};

const ROLE_LABELS: Record<string, string> = {
  DEPOSITOR: 'Депонент',
  BENEFICIARY: 'Бенефициар',
  OPERATOR: 'Оператор',
  ADMINISTRATOR: 'Администратор',
};

const ESCROW_STATUS_LABELS: Record<string, string> = {
  NOT_CREATED: 'Не создан',
  OPENED: 'Открыт',
  FUNDS_DEPOSITING: 'Внесение средств',
  FUNDS_SECURED: 'Средства защищены',
  RELEASING: 'Выпуск средств',
  RELEASED_TO_BENEFICIARY: 'Выплачено бенефициару',
  REFUNDING: 'Возврат',
  REFUNDED_TO_DEPOSITOR: 'Возвращено депоненту',
  DISPUTED: 'Спор',
  CANCELLED: 'Отменён',
};

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
  const [escrowAccount, setEscrowAccount] = useState<EscrowAccountResponse | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [actionLoading, setActionLoading] = useState('');
  const [confirmAction, setConfirmAction] = useState<{ action: string; label: string; body?: object } | null>(null);

  const loadDeal = useCallback(() => {
    Promise.all([apiGetDeal(id), apiGetDealEvents(id)])
      .then(([d, e]) => {
        setDeal(d);
        setEvents(e);
        apiGetEscrowAccount(id).then(setEscrowAccount).catch(() => {});
      })
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
      apiGetEscrowAccount(id).then(setEscrowAccount).catch(() => {});
    } catch (e) {
      alert(e instanceof Error ? e.message : 'Ошибка выполнения');
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
        {error || 'Сделка не найдена'}
        <Link href="/deals" className="ml-2 underline">К списку сделок</Link>
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
        <Link href="/deals" className="text-sm text-gray-500 hover:text-gray-700">&larr; К списку сделок</Link>
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
            <h2 className="mb-4 text-lg font-semibold text-gray-900">Информация о сделке</h2>
            <dl className="grid gap-4 sm:grid-cols-2">
              <div>
                <dt className="text-sm font-medium text-gray-500">Сумма</dt>
                <dd className="mt-1 text-lg font-semibold text-gray-900">
                  {Number(deal.amount).toLocaleString('ru-RU')} {deal.currency}
                </dd>
              </div>
              <div>
                <dt className="text-sm font-medium text-gray-500">Статус</dt>
                <dd className="mt-1"><DealStatusBadge status={deal.status} /></dd>
              </div>
              <div>
                <dt className="text-sm font-medium text-gray-500">Депонент</dt>
                <dd className="mt-1 text-sm text-gray-900 font-mono">
                  {deal.depositorId.slice(0, 8)}...
                  {isDepositor && <span className="ml-1 text-blue-600">(Вы)</span>}
                </dd>
              </div>
              <div>
                <dt className="text-sm font-medium text-gray-500">Бенефициар</dt>
                <dd className="mt-1 text-sm text-gray-900 font-mono">
                  {deal.beneficiaryId.slice(0, 8)}...
                  {isBeneficiary && <span className="ml-1 text-blue-600">(Вы)</span>}
                </dd>
              </div>
              {deal.description && (
                <div className="sm:col-span-2">
                  <dt className="text-sm font-medium text-gray-500">Описание</dt>
                  <dd className="mt-1 text-sm text-gray-900">{deal.description}</dd>
                </div>
              )}
              <div>
                <dt className="text-sm font-medium text-gray-500">Дата создания</dt>
                <dd className="mt-1 text-sm text-gray-900">{new Date(deal.createdAt).toLocaleString('ru-RU')}</dd>
              </div>
            </dl>
          </div>

          {/* Escrow Account */}
          {escrowAccount && (
            <div className="rounded-lg border-2 border-amber-200 bg-amber-50 p-6 shadow-sm">
              <h2 className="mb-4 text-lg font-semibold text-amber-900">Эскроу-счёт</h2>
              <dl className="grid gap-4 sm:grid-cols-2">
                <div>
                  <dt className="text-sm font-medium text-amber-700">Удержано на эскроу</dt>
                  <dd className="mt-1 text-xl font-bold text-amber-900">
                    {Number(escrowAccount.amount).toLocaleString('ru-RU')} {escrowAccount.currency}
                  </dd>
                </div>
                <div>
                  <dt className="text-sm font-medium text-amber-700">Статус эскроу</dt>
                  <dd className="mt-1">
                    <span className={`inline-block rounded-full px-3 py-1 text-xs font-semibold ${
                      escrowAccount.status === 'FUNDS_SECURED' ? 'bg-green-100 text-green-800' :
                      escrowAccount.status === 'RELEASED_TO_BENEFICIARY' ? 'bg-blue-100 text-blue-800' :
                      escrowAccount.status === 'REFUNDED_TO_DEPOSITOR' ? 'bg-orange-100 text-orange-800' :
                      escrowAccount.status === 'DISPUTED' ? 'bg-red-100 text-red-800' :
                      'bg-gray-100 text-gray-800'
                    }`}>
                      {ESCROW_STATUS_LABELS[escrowAccount.status] || escrowAccount.status.replace(/_/g, ' ')}
                    </span>
                  </dd>
                </div>
                {escrowAccount.fundedAt && (
                  <div>
                    <dt className="text-sm font-medium text-amber-700">Дата оплаты</dt>
                    <dd className="mt-1 text-sm text-amber-900">{new Date(escrowAccount.fundedAt).toLocaleString('ru-RU')}</dd>
                  </div>
                )}
                {escrowAccount.releasedAt && (
                  <div>
                    <dt className="text-sm font-medium text-amber-700">Дата выплаты</dt>
                    <dd className="mt-1 text-sm text-amber-900">{new Date(escrowAccount.releasedAt).toLocaleString('ru-RU')}</dd>
                  </div>
                )}
                {escrowAccount.refundedAt && (
                  <div>
                    <dt className="text-sm font-medium text-amber-700">Дата возврата</dt>
                    <dd className="mt-1 text-sm text-amber-900">{new Date(escrowAccount.refundedAt).toLocaleString('ru-RU')}</dd>
                  </div>
                )}
              </dl>
            </div>
          )}

          {/* Timeline */}
          <div className="rounded-lg bg-white p-6 shadow-sm">
            <h2 className="mb-4 text-lg font-semibold text-gray-900">Хронология</h2>
            {events.length === 0 ? (
              <p className="text-sm text-gray-400">Событий пока нет</p>
            ) : (
              <ol className="relative border-l border-gray-200 ml-3">
                {events.map((ev) => (
                  <li key={ev.id} className="mb-6 ml-6">
                    <span className="absolute -left-2 flex h-4 w-4 items-center justify-center rounded-full bg-blue-100 ring-4 ring-white">
                      <span className="h-2 w-2 rounded-full bg-blue-600" />
                    </span>
                    <div className="flex items-baseline gap-2">
                      <span className="text-sm font-medium text-gray-900">
                        {EVENT_LABELS[ev.eventType] || ev.eventType.replace(/_/g, ' ')}
                      </span>
                      {ev.newStatus && <DealStatusBadge status={ev.newStatus} />}
                    </div>
                    <p className="mt-0.5 text-xs text-gray-500">
                      {new Date(ev.createdAt).toLocaleString('ru-RU')} &middot; {ROLE_LABELS[ev.actorRole] || ev.actorRole}
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
            <h2 className="mb-4 text-lg font-semibold text-gray-900">Действия</h2>
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
            <h3 className="text-lg font-semibold text-gray-900">Подтверждение</h3>
            <p className="mt-2 text-sm text-gray-600">
              Вы уверены, что хотите выполнить действие: <strong>{confirmAction.label.toLowerCase()}</strong>?
            </p>
            <div className="mt-4 flex gap-3 justify-end">
              <button
                onClick={() => setConfirmAction(null)}
                className="rounded-md border border-gray-300 px-4 py-2 text-sm font-medium text-gray-700 hover:bg-gray-50"
              >
                Отмена
              </button>
              <button
                onClick={() => executeAction(confirmAction.action, confirmAction.body)}
                disabled={!!actionLoading}
                className="rounded-md bg-blue-600 px-4 py-2 text-sm font-medium text-white hover:bg-blue-700 disabled:opacity-50"
              >
                {actionLoading ? 'Выполнение...' : 'Подтвердить'}
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
    return <p className="text-sm text-gray-400">Нет доступных действий для текущего статуса</p>;
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
          {actionLoading === a.action ? 'Выполнение...' : a.label}
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
        actions.push({ action: 'submit', label: 'Отправить на согласование', style: primary });
        actions.push({ action: 'cancel', label: 'Отменить сделку', style: danger });
      }
      break;
    case 'AWAITING_AGREEMENT':
      if (isBeneficiary) {
        actions.push({ action: 'agree', label: 'Принять сделку', style: success });
        actions.push({ action: 'decline', label: 'Отклонить сделку', style: danger });
      }
      if (isDepositor) {
        actions.push({ action: 'cancel', label: 'Отменить сделку', style: secondary });
      }
      break;
    case 'AWAITING_FUNDING':
      if (isDepositor) {
        actions.push({ action: 'fund', label: 'Внести средства', style: primary });
        actions.push({ action: 'cancel', label: 'Отменить сделку', style: secondary });
      }
      break;
    case 'FUNDING_PROCESSING':
      break;
    case 'AWAITING_FULFILLMENT':
      if (isBeneficiary) {
        actions.push({ action: 'deliver', label: 'Подтвердить исполнение', style: primary });
      }
      if (isDepositor || isBeneficiary) {
        actions.push({ action: 'dispute', label: 'Открыть спор', style: danger });
        actions.push({ action: 'cancel', label: 'Отменить сделку', style: secondary });
      }
      break;
    case 'AWAITING_REVIEW':
      if (isDepositor) {
        actions.push({ action: 'confirm', label: 'Подтвердить и выплатить', style: success });
        actions.push({ action: 'reject', label: 'Отклонить и вернуть', style: danger });
        actions.push({ action: 'dispute', label: 'Открыть спор', style: secondary });
      }
      break;
    case 'DISPUTED':
      if (isOperator) {
        actions.push({ action: 'resolve', label: 'Выплатить бенефициару', style: success, body: { resolution: 'RELEASE' } });
        actions.push({ action: 'resolve', label: 'Вернуть депоненту', style: danger, body: { resolution: 'REFUND' } });
      }
      break;
  }

  return actions;
}
