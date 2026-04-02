import type { DealStatus } from '@/lib/api';

const STATUS_CONFIG: Record<DealStatus, { label: string; color: string }> = {
  DRAFT:                  { label: 'Черновик',             color: 'bg-gray-100 text-gray-700' },
  AWAITING_AGREEMENT:     { label: 'Ожидает согласия',    color: 'bg-yellow-100 text-yellow-800' },
  AGREED:                 { label: 'Согласована',          color: 'bg-blue-100 text-blue-700' },
  AWAITING_FUNDING:       { label: 'Ожидает оплаты',      color: 'bg-orange-100 text-orange-700' },
  FUNDING_PROCESSING:     { label: 'Обработка платежа',   color: 'bg-orange-100 text-orange-700' },
  FUNDED:                 { label: 'Оплачена',             color: 'bg-green-100 text-green-700' },
  AWAITING_FULFILLMENT:   { label: 'Ожидает исполнения',  color: 'bg-purple-100 text-purple-700' },
  AWAITING_REVIEW:        { label: 'Ожидает проверки',    color: 'bg-indigo-100 text-indigo-700' },
  RELEASING:              { label: 'Выпуск средств',      color: 'bg-green-100 text-green-700' },
  COMPLETED:              { label: 'Завершена',            color: 'bg-green-100 text-green-800' },
  REFUNDING:              { label: 'Возврат средств',     color: 'bg-red-100 text-red-700' },
  REFUNDED:               { label: 'Возвращена',           color: 'bg-red-100 text-red-800' },
  DISPUTED:               { label: 'Спор',                 color: 'bg-red-100 text-red-700' },
  CANCELLED:              { label: 'Отменена',             color: 'bg-gray-100 text-gray-600' },
  CLOSED:                 { label: 'Закрыта',              color: 'bg-gray-100 text-gray-600' },
};

export default function DealStatusBadge({ status }: { status: DealStatus }) {
  const config = STATUS_CONFIG[status] || { label: status, color: 'bg-gray-100 text-gray-700' };
  return (
    <span className={`inline-flex items-center rounded-full px-2.5 py-0.5 text-xs font-medium ${config.color}`}>
      {config.label}
    </span>
  );
}
