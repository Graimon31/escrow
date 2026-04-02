'use client';

import { useEffect, useState } from 'react';
import ProtectedRoute from '@/components/protected-route';
import { useAuth } from '@/contexts/auth-context';
import DealStatusBadge from '@/components/deal-status-badge';
import Link from 'next/link';
import {
  apiListDeals,
  apiGetDealEvents,
  apiGetEscrowAccount,
  apiGetDealDocuments,
  type DealResponse,
  type DealEventResponse,
  type EscrowAccountResponse,
  type DocumentResponse,
} from '@/lib/api';

// ── Deal Progress Stepper stages ────────────────────────────
const DEAL_STAGES = [
  { key: 'agreement', label: 'Согласование', statuses: ['DRAFT', 'AWAITING_AGREEMENT', 'AGREED'] },
  { key: 'funding', label: 'Внесение средств', statuses: ['AWAITING_FUNDING', 'FUNDING_PROCESSING'] },
  { key: 'fulfillment', label: 'Исполнение', statuses: ['FUNDED', 'AWAITING_FULFILLMENT'] },
  { key: 'review', label: 'Эскроу заблокирован', statuses: ['AWAITING_REVIEW'] },
  { key: 'release', label: 'Выплата средств', statuses: ['RELEASING', 'COMPLETED'] },
];

function getStageIndex(status: string): number {
  for (let i = 0; i < DEAL_STAGES.length; i++) {
    if (DEAL_STAGES[i].statuses.includes(status)) return i;
  }
  return -1;
}

const EVENT_LABELS: Record<string, string> = {
  DEAL_CREATED: 'Сделка создана',
  DEAL_SUBMITTED: 'Отправлена на согласование',
  DEAL_AGREED: 'Согласована',
  DEAL_DECLINED: 'Отклонена',
  DEAL_FUNDED: 'Средства внесены',
  DEAL_DELIVERED: 'Исполнение подтверждено',
  DEAL_COMPLETED: 'Завершена',
  DEAL_REFUNDED: 'Средства возвращены',
  DEAL_DISPUTED: 'Открыт спор',
  DEAL_CANCELLED: 'Отменена',
  DISPUTE_RESOLVED_RELEASE: 'Спор: выплата',
  DISPUTE_RESOLVED_REFUND: 'Спор: возврат',
};

export default function DashboardPage() {
  return (
    <ProtectedRoute>
      <DashboardContent />
    </ProtectedRoute>
  );
}

function DashboardContent() {
  const { user } = useAuth();
  const [deals, setDeals] = useState<DealResponse[]>([]);
  const [activeDeal, setActiveDeal] = useState<DealResponse | null>(null);
  const [events, setEvents] = useState<DealEventResponse[]>([]);
  const [escrow, setEscrow] = useState<EscrowAccountResponse | null>(null);
  const [documents, setDocuments] = useState<DocumentResponse[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (!user) return;
    apiListDeals()
      .then((allDeals) => {
        setDeals(allDeals);
        // Find active (non-terminal) deal to show in vault
        const active = allDeals.find((d) =>
          !['COMPLETED', 'CANCELLED', 'REFUNDED', 'CLOSED'].includes(d.status)
        ) || allDeals[0] || null;
        setActiveDeal(active);
        if (active) {
          apiGetDealEvents(active.id).then(setEvents).catch(() => {});
          apiGetEscrowAccount(active.id).then(setEscrow).catch(() => {});
          apiGetDealDocuments(active.id).then(setDocuments).catch(() => {});
        }
      })
      .catch(() => {})
      .finally(() => setLoading(false));
  }, [user]);

  if (!user) return null;

  if (loading) {
    return (
      <div className="flex justify-center py-12">
        <div className="h-8 w-8 animate-spin rounded-full border-4 border-gray-300 border-t-blue-600" />
      </div>
    );
  }

  const stageIndex = activeDeal ? getStageIndex(activeDeal.status) : -1;
  const isTerminal = activeDeal && ['COMPLETED', 'CANCELLED', 'REFUNDED', 'CLOSED'].includes(activeDeal.status);

  return (
    <div className="space-y-6">
      {/* Breadcrumb */}
      <div className="flex items-center gap-2 text-sm text-gray-500">
        <span>Главная</span>
        <span>&gt;</span>
        <span className="font-semibold text-gray-900">Ваш эскроу-дашборд</span>
      </div>

      <div className="grid gap-6 xl:grid-cols-3">
        {/* ── LEFT: Main content (2 cols) ───────────────────── */}
        <div className="space-y-6 xl:col-span-2">

          {/* ── Escrow Vault Widget ──────────────────────────── */}
          <div className="rounded-xl bg-white p-6 shadow-sm">
            <div className="flex items-start justify-between">
              <div>
                <h2 className="text-sm font-medium text-gray-500">Эскроу-хранилище</h2>
                <p className="mt-2 text-4xl font-bold tracking-tight text-gray-900">
                  {activeDeal
                    ? `${Number(activeDeal.amount).toLocaleString('ru-RU', { minimumFractionDigits: 2 })} ${activeDeal.currency === 'RUB' ? '₽' : activeDeal.currency}`
                    : '0,00 ₽'
                  }
                </p>
                {activeDeal && (
                  <p className="mt-1 text-xs text-gray-400">
                    Сделка: {activeDeal.title}
                  </p>
                )}
              </div>
              {/* Safe/Vault Icon */}
              <div className="flex h-20 w-20 items-center justify-center rounded-2xl bg-blue-50">
                <svg className="h-12 w-12 text-blue-500" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth={1.5}>
                  <rect x="2" y="4" width="20" height="16" rx="2" />
                  <circle cx="12" cy="12" r="3" />
                  <path d="M12 9v0.5M12 14.5v0.5M9 12h0.5M14.5 12h0.5" />
                  <rect x="18" y="9" width="2" height="6" rx="0.5" />
                </svg>
              </div>
            </div>

            {/* ── Progress Stepper ────────────────────────────── */}
            {activeDeal && !isTerminal && stageIndex >= 0 && (
              <div className="mt-8">
                <div className="flex items-center justify-between">
                  {DEAL_STAGES.map((stage, i) => {
                    const isCompleted = i < stageIndex;
                    const isCurrent = i === stageIndex;
                    return (
                      <div key={stage.key} className="flex flex-1 items-center">
                        {/* Node */}
                        <div className="flex flex-col items-center">
                          <div className={`flex h-8 w-8 items-center justify-center rounded-full border-2 text-xs font-bold transition-all ${
                            isCompleted
                              ? 'border-blue-600 bg-blue-600 text-white'
                              : isCurrent
                                ? 'border-blue-600 bg-white text-blue-600 ring-4 ring-blue-100'
                                : 'border-gray-300 bg-white text-gray-400'
                          }`}>
                            {isCompleted ? (
                              <svg className="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={3}>
                                <path strokeLinecap="round" strokeLinejoin="round" d="M5 13l4 4L19 7" />
                              </svg>
                            ) : (
                              <span className="h-2.5 w-2.5 rounded-full bg-current" />
                            )}
                          </div>
                          <span className={`mt-2 text-center text-[11px] font-medium leading-tight ${
                            isCurrent ? 'text-blue-700 font-semibold' : isCompleted ? 'text-gray-700' : 'text-gray-400'
                          }`}>
                            {stage.label}
                          </span>
                        </div>
                        {/* Connector line */}
                        {i < DEAL_STAGES.length - 1 && (
                          <div className={`mx-1 h-0.5 flex-1 ${
                            i < stageIndex ? 'bg-blue-600' : 'bg-gray-200'
                          }`} />
                        )}
                      </div>
                    );
                  })}
                </div>
              </div>
            )}

            {/* Terminal state badge */}
            {activeDeal && isTerminal && (
              <div className="mt-6 flex items-center gap-2">
                <DealStatusBadge status={activeDeal.status} />
                <span className="text-sm text-gray-500">Сделка завершена</span>
              </div>
            )}

            {!activeDeal && (
              <div className="mt-6 text-center">
                <p className="text-sm text-gray-400">Нет активных сделок</p>
                <Link href="/deals/new" className="mt-2 inline-block rounded-md bg-blue-600 px-4 py-2 text-sm font-medium text-white hover:bg-blue-700">
                  Создать сделку
                </Link>
              </div>
            )}
          </div>

          {/* ── Smart Contract Logic Block ───────────────────── */}
          {activeDeal && (
            <div className="rounded-xl bg-white shadow-sm overflow-hidden">
              <div className="border-b border-gray-200 px-6 py-4">
                <h2 className="text-base font-semibold text-gray-900">Условия эскроу-контракта</h2>
              </div>
              <div className="bg-gray-900 p-5 font-mono text-sm leading-relaxed">
                <p><span className="text-purple-400">contract</span> <span className="text-green-400">EscrowConditions</span> {'{'}</p>
                <p className="ml-6"><span className="text-gray-400">{'// Депонент вносит средства на эскроу'}</span></p>
                <p className="ml-6"><span className="text-blue-400">deposit</span> = <span className="text-yellow-300">{Number(activeDeal.amount).toLocaleString('ru-RU')}</span> <span className="text-orange-400">{activeDeal.currency}</span>;</p>
                <p className="ml-6"><span className="text-gray-400">{'// Бенефициар исполняет условия'}</span></p>
                <p className="ml-6"><span className="text-blue-400">fulfillment</span> = <span className="text-yellow-300">$Escrow</span>.<span className="text-green-400">awaitDelivery</span>();</p>
                <p className="ml-6"><span className="text-gray-400">{'// Депонент подтверждает — средства выплачиваются'}</span></p>
                <p className="ml-6"><span className="text-purple-400">return</span> <span className="text-yellow-300">$Escrow</span>.<span className="text-green-400">release</span>(<span className="text-orange-400">&quot;бенефициар&quot;</span>);</p>
                <p>{'}'}</p>
              </div>
            </div>
          )}

          {/* ── Participants Grid ────────────────────────────── */}
          {activeDeal && (
            <div className="grid gap-4 sm:grid-cols-2">
              {/* Buyer/Depositor */}
              <div className="rounded-xl bg-white p-5 shadow-sm">
                <h3 className="mb-3 text-base font-semibold text-gray-900">Участники сделки</h3>
                <div className="space-y-4">
                  <ParticipantCard
                    role="Покупатель"
                    roleLabel="Депонент"
                    userId={activeDeal.depositorId}
                    isCurrentUser={activeDeal.depositorId === user.id}
                    userName={activeDeal.depositorId === user.id ? user.fullName : undefined}
                    email={activeDeal.depositorId === user.id ? user.email : undefined}
                  />
                  <ParticipantCard
                    role="Продавец"
                    roleLabel="Бенефициар"
                    userId={activeDeal.beneficiaryId}
                    isCurrentUser={activeDeal.beneficiaryId === user.id}
                    userName={activeDeal.beneficiaryId === user.id ? user.fullName : undefined}
                    email={activeDeal.beneficiaryId === user.id ? user.email : undefined}
                  />
                </div>
              </div>

              {/* Recent Deals */}
              <div className="rounded-xl bg-white p-5 shadow-sm">
                <div className="flex items-center justify-between mb-3">
                  <h3 className="text-base font-semibold text-gray-900">Последние сделки</h3>
                  <Link href="/deals" className="text-xs font-medium text-blue-600 hover:text-blue-500">
                    Все сделки
                  </Link>
                </div>
                {deals.length === 0 ? (
                  <div className="flex flex-col items-center py-8 text-gray-400">
                    <svg className="mb-2 h-10 w-10" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={1.5}>
                      <path strokeLinecap="round" strokeLinejoin="round" d="M19.5 14.25v-2.625a3.375 3.375 0 0 0-3.375-3.375h-1.5A1.125 1.125 0 0 1 13.5 7.125v-1.5a3.375 3.375 0 0 0-3.375-3.375H8.25m2.25 0H5.625c-.621 0-1.125.504-1.125 1.125v17.25c0 .621.504 1.125 1.125 1.125h12.75c.621 0 1.125-.504 1.125-1.125V11.25a9 9 0 0 0-9-9Z" />
                    </svg>
                    <p className="text-sm">Сделок пока нет</p>
                  </div>
                ) : (
                  <div className="space-y-2">
                    {deals.slice(0, 5).map((d) => (
                      <Link
                        key={d.id}
                        href={`/deals/${d.id}`}
                        className="flex items-center justify-between rounded-lg px-3 py-2 text-sm hover:bg-gray-50 transition-colors"
                      >
                        <div className="min-w-0 flex-1">
                          <p className="truncate font-medium text-gray-900">{d.title}</p>
                          <p className="text-xs text-gray-400">{Number(d.amount).toLocaleString('ru-RU')} {d.currency}</p>
                        </div>
                        <DealStatusBadge status={d.status} />
                      </Link>
                    ))}
                  </div>
                )}
              </div>
            </div>
          )}
        </div>

        {/* ── RIGHT: Audit Trail & Action Center ────────────── */}
        <div className="space-y-6">
          {/* Action buttons */}
          {activeDeal && (
            <div className="rounded-xl bg-white p-5 shadow-sm">
              <h2 className="mb-4 text-base font-semibold text-gray-900">Центр действий</h2>
              <div className="flex gap-2">
                <Link
                  href={`/deals/${activeDeal.id}`}
                  className="flex-1 rounded-lg bg-blue-600 px-4 py-2.5 text-center text-sm font-medium text-white hover:bg-blue-700 transition-colors"
                >
                  Управление сделкой
                </Link>
                {!isTerminal && (
                  <Link
                    href={`/deals/${activeDeal.id}`}
                    className="rounded-lg border-2 border-red-300 px-4 py-2.5 text-center text-sm font-medium text-red-600 hover:bg-red-50 transition-colors"
                  >
                    Спор
                  </Link>
                )}
              </div>
            </div>
          )}

          {/* Audit Trail Timeline */}
          <div className="rounded-xl bg-white p-5 shadow-sm">
            <h2 className="mb-4 text-base font-semibold text-gray-900">Аудит и хронология</h2>

            {/* File attachments */}
            {documents.length > 0 && (
              <div className="mb-4 space-y-2">
                {documents.slice(0, 3).map((doc) => (
                  <a
                    key={doc.id}
                    href={`${process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080'}/api/documents/${doc.id}/download`}
                    className="flex items-center gap-3 rounded-lg border border-gray-200 p-3 hover:bg-gray-50 transition-colors"
                  >
                    <div className={`flex h-8 w-8 items-center justify-center rounded-lg ${
                      doc.fileName.endsWith('.pdf') ? 'bg-red-100 text-red-600' :
                      doc.fileName.endsWith('.doc') || doc.fileName.endsWith('.docx') ? 'bg-blue-100 text-blue-600' :
                      'bg-green-100 text-green-600'
                    }`}>
                      <svg className="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={1.5}>
                        <path strokeLinecap="round" strokeLinejoin="round" d="M19.5 14.25v-2.625a3.375 3.375 0 0 0-3.375-3.375h-1.5A1.125 1.125 0 0 1 13.5 7.125v-1.5a3.375 3.375 0 0 0-3.375-3.375H8.25m2.25 0H5.625c-.621 0-1.125.504-1.125 1.125v17.25c0 .621.504 1.125 1.125 1.125h12.75c.621 0 1.125-.504 1.125-1.125V11.25a9 9 0 0 0-9-9Z" />
                      </svg>
                    </div>
                    <div className="min-w-0 flex-1">
                      <p className="truncate text-sm font-medium text-gray-900">{doc.fileName}</p>
                      <p className="text-xs text-gray-400">{(doc.fileSize / 1024).toFixed(1)} КБ</p>
                    </div>
                    <svg className="h-4 w-4 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={1.5}>
                      <path strokeLinecap="round" strokeLinejoin="round" d="M8.25 4.5l7.5 7.5-7.5 7.5" />
                    </svg>
                  </a>
                ))}
              </div>
            )}

            {/* Event timeline */}
            {events.length === 0 ? (
              <p className="text-sm text-gray-400">Событий пока нет</p>
            ) : (
              <div className="space-y-3">
                {events.slice().reverse().slice(0, 8).map((ev) => (
                  <div key={ev.id} className="flex items-start gap-3">
                    <div className={`mt-1 h-2.5 w-2.5 flex-shrink-0 rounded-full ${
                      ev.eventType.includes('COMPLETED') ? 'bg-green-500' :
                      ev.eventType.includes('DISPUTED') ? 'bg-red-500' :
                      ev.eventType.includes('FUNDED') ? 'bg-blue-500' :
                      'bg-gray-300'
                    }`} />
                    <div className="min-w-0 flex-1">
                      <p className="text-sm text-gray-900">
                        {EVENT_LABELS[ev.eventType] || ev.eventType.replace(/_/g, ' ')}
                      </p>
                      <p className="text-xs text-gray-400">
                        {new Date(ev.createdAt).toLocaleString('ru-RU', {
                          day: 'numeric', month: 'short', hour: '2-digit', minute: '2-digit'
                        })}
                      </p>
                    </div>
                  </div>
                ))}
              </div>
            )}
          </div>

          {/* Escrow status card */}
          {escrow && (
            <div className="rounded-xl border-2 border-amber-200 bg-amber-50 p-5 shadow-sm">
              <h3 className="text-sm font-semibold text-amber-900">Статус эскроу-счёта</h3>
              <p className="mt-2 text-2xl font-bold text-amber-900">
                {Number(escrow.amount).toLocaleString('ru-RU', { minimumFractionDigits: 2 })} {escrow.currency === 'RUB' ? '₽' : escrow.currency}
              </p>
              <span className={`mt-2 inline-block rounded-full px-3 py-1 text-xs font-semibold ${
                escrow.status === 'FUNDS_SECURED' ? 'bg-green-100 text-green-800' :
                escrow.status === 'RELEASED_TO_BENEFICIARY' ? 'bg-blue-100 text-blue-800' :
                escrow.status === 'REFUNDED_TO_DEPOSITOR' ? 'bg-orange-100 text-orange-800' :
                'bg-gray-100 text-gray-800'
              }`}>
                {escrow.status === 'FUNDS_SECURED' ? 'Средства защищены' :
                 escrow.status === 'RELEASED_TO_BENEFICIARY' ? 'Выплачено' :
                 escrow.status === 'REFUNDED_TO_DEPOSITOR' ? 'Возвращено' :
                 escrow.status.replace(/_/g, ' ')}
              </span>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}

function ParticipantCard({
  role,
  roleLabel,
  userId,
  isCurrentUser,
  userName,
  email,
}: {
  role: string;
  roleLabel: string;
  userId: string;
  isCurrentUser: boolean;
  userName?: string;
  email?: string;
}) {
  return (
    <div className="flex items-center gap-3 rounded-lg border border-gray-200 p-3">
      <div className="flex h-10 w-10 items-center justify-center rounded-full bg-blue-100 text-sm font-bold text-blue-700">
        {userName ? userName.charAt(0).toUpperCase() : userId.charAt(0).toUpperCase()}
      </div>
      <div className="min-w-0 flex-1">
        <div className="flex items-center gap-2">
          <span className="text-sm font-semibold text-gray-900">{role}</span>
          {isCurrentUser && (
            <span className="rounded-full bg-green-100 px-2 py-0.5 text-[10px] font-medium text-green-700">Вы</span>
          )}
        </div>
        <p className="text-xs text-gray-500">
          {userName || userId.slice(0, 8) + '...'}, {roleLabel}
        </p>
        {email && <p className="text-xs text-gray-400">{email}</p>}
        <div className="mt-1 flex items-center gap-1">
          <span className="text-xs text-gray-500">{roleLabel}</span>
          <span className="inline-block h-1.5 w-1.5 rounded-full bg-green-500" />
          <span className="text-xs text-green-600">Активен</span>
        </div>
      </div>
    </div>
  );
}
