import type { DealStatus } from '@/lib/api';

const STATUS_CONFIG: Record<DealStatus, { label: string; color: string }> = {
  DRAFT:                  { label: 'Draft',                color: 'bg-gray-100 text-gray-700' },
  AWAITING_AGREEMENT:     { label: 'Awaiting Agreement',   color: 'bg-yellow-100 text-yellow-800' },
  AGREED:                 { label: 'Agreed',               color: 'bg-blue-100 text-blue-700' },
  AWAITING_FUNDING:       { label: 'Awaiting Funding',     color: 'bg-orange-100 text-orange-700' },
  FUNDING_PROCESSING:     { label: 'Processing Payment',   color: 'bg-orange-100 text-orange-700' },
  FUNDED:                 { label: 'Funded',               color: 'bg-green-100 text-green-700' },
  AWAITING_FULFILLMENT:   { label: 'Awaiting Fulfillment', color: 'bg-purple-100 text-purple-700' },
  AWAITING_REVIEW:        { label: 'Awaiting Review',      color: 'bg-indigo-100 text-indigo-700' },
  RELEASING:              { label: 'Releasing Funds',      color: 'bg-green-100 text-green-700' },
  COMPLETED:              { label: 'Completed',            color: 'bg-green-100 text-green-800' },
  REFUNDING:              { label: 'Refunding',            color: 'bg-red-100 text-red-700' },
  REFUNDED:               { label: 'Refunded',             color: 'bg-red-100 text-red-800' },
  DISPUTED:               { label: 'Disputed',             color: 'bg-red-100 text-red-700' },
  CANCELLED:              { label: 'Cancelled',            color: 'bg-gray-100 text-gray-600' },
  CLOSED:                 { label: 'Closed',               color: 'bg-gray-100 text-gray-600' },
};

export default function DealStatusBadge({ status }: { status: DealStatus }) {
  const config = STATUS_CONFIG[status] || { label: status, color: 'bg-gray-100 text-gray-700' };
  return (
    <span className={`inline-flex items-center rounded-full px-2.5 py-0.5 text-xs font-medium ${config.color}`}>
      {config.label}
    </span>
  );
}
