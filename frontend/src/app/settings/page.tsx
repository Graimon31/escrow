'use client';

import ProtectedRoute from '@/components/protected-route';
import { useAuth } from '@/contexts/auth-context';

const ROLE_LABELS: Record<string, string> = {
  DEPOSITOR: 'Депонент',
  BENEFICIARY: 'Бенефициар',
  OPERATOR: 'Оператор',
  ADMINISTRATOR: 'Администратор',
};

export default function SettingsPage() {
  return (
    <ProtectedRoute>
      <SettingsContent />
    </ProtectedRoute>
  );
}

function SettingsContent() {
  const { user } = useAuth();
  if (!user) return null;

  return (
    <>
      <h1 className="mb-6 text-2xl font-bold text-gray-900">Настройки</h1>
      <div className="rounded-lg bg-white p-6 shadow-sm">
        <h2 className="mb-4 text-lg font-semibold text-gray-900">Профиль</h2>
        <dl className="grid gap-4 sm:grid-cols-2">
          <div>
            <dt className="text-sm font-medium text-gray-500">Полное имя</dt>
            <dd className="mt-1 text-sm text-gray-900">{user.fullName}</dd>
          </div>
          <div>
            <dt className="text-sm font-medium text-gray-500">Email</dt>
            <dd className="mt-1 text-sm text-gray-900">{user.email}</dd>
          </div>
          <div>
            <dt className="text-sm font-medium text-gray-500">Роль</dt>
            <dd className="mt-1 text-sm text-gray-900">{ROLE_LABELS[user.role] || user.role}</dd>
          </div>
          <div>
            <dt className="text-sm font-medium text-gray-500">ID пользователя</dt>
            <dd className="mt-1 text-sm font-mono text-gray-900">{user.id}</dd>
          </div>
        </dl>
      </div>
    </>
  );
}
