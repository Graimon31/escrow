'use client';

import { useEffect, useState } from 'react';
import ProtectedRoute from '@/components/protected-route';
import { useAuth } from '@/contexts/auth-context';
import {
  apiAdminListUsers,
  apiAdminChangeRole,
  apiAdminToggleUser,
  type UserResponse,
} from '@/lib/api';

export default function AdminPage() {
  return (
    <ProtectedRoute>
      <AdminContent />
    </ProtectedRoute>
  );
}

const ROLES = ['DEPOSITOR', 'BENEFICIARY', 'OPERATOR', 'ADMINISTRATOR'] as const;

const ROLE_LABELS: Record<string, string> = {
  DEPOSITOR: 'Депонент',
  BENEFICIARY: 'Бенефициар',
  OPERATOR: 'Оператор',
  ADMINISTRATOR: 'Администратор',
};

const ROLE_LABELS_PLURAL: Record<string, string> = {
  DEPOSITOR: 'Депонентов',
  BENEFICIARY: 'Бенефициаров',
  OPERATOR: 'Операторов',
  ADMINISTRATOR: 'Администраторов',
};

const ROLE_COLORS: Record<string, string> = {
  DEPOSITOR: 'bg-blue-100 text-blue-700',
  BENEFICIARY: 'bg-green-100 text-green-700',
  OPERATOR: 'bg-orange-100 text-orange-700',
  ADMINISTRATOR: 'bg-purple-100 text-purple-700',
};

function AdminContent() {
  const { user } = useAuth();
  const [users, setUsers] = useState<UserResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  const isAdmin = user?.role === 'ADMINISTRATOR';

  useEffect(() => {
    if (!isAdmin) return;
    apiAdminListUsers()
      .then(setUsers)
      .catch((e) => setError(e.message))
      .finally(() => setLoading(false));
  }, [isAdmin]);

  if (!isAdmin) {
    return (
      <div className="rounded-lg bg-red-50 p-6 text-center text-sm text-red-700">
        Доступ запрещён. Требуется роль администратора.
      </div>
    );
  }

  const handleRoleChange = async (userId: string, newRole: string) => {
    try {
      const updated = await apiAdminChangeRole(userId, newRole);
      setUsers((prev) => prev.map((u) => (u.id === updated.id ? updated : u)));
    } catch (e: unknown) {
      alert((e as Error).message);
    }
  };

  const handleToggle = async (userId: string) => {
    try {
      const updated = await apiAdminToggleUser(userId);
      setUsers((prev) => prev.map((u) => (u.id === updated.id ? updated : u)));
    } catch (e: unknown) {
      alert((e as Error).message);
    }
  };

  if (loading) {
    return (
      <div className="flex justify-center py-12">
        <div className="h-8 w-8 animate-spin rounded-full border-4 border-gray-300 border-t-blue-600" />
      </div>
    );
  }

  if (error) {
    return (
      <div className="rounded-lg bg-red-50 p-6 text-center text-sm text-red-700">{error}</div>
    );
  }

  return (
    <>
      <h1 className="mb-6 text-2xl font-bold text-gray-900">Администрирование</h1>

      <div className="mb-6 grid grid-cols-2 gap-4 md:grid-cols-5">
        <div className="rounded-lg bg-gray-50 p-4">
          <p className="text-2xl font-bold text-gray-700">{users.length}</p>
          <p className="text-sm text-gray-500">Всего пользователей</p>
        </div>
        {ROLES.map((role) => {
          const count = users.filter((u) => u.role === role).length;
          return (
            <div key={role} className={`rounded-lg p-4 ${ROLE_COLORS[role]}`}>
              <p className="text-2xl font-bold">{count}</p>
              <p className="text-sm">{ROLE_LABELS_PLURAL[role]}</p>
            </div>
          );
        })}
      </div>

      <div className="overflow-x-auto rounded-lg bg-white shadow-sm">
        <table className="min-w-full divide-y divide-gray-200">
          <thead className="bg-gray-50">
            <tr>
              <th className="px-4 py-3 text-left text-xs font-medium uppercase text-gray-500">Имя</th>
              <th className="px-4 py-3 text-left text-xs font-medium uppercase text-gray-500">Email</th>
              <th className="px-4 py-3 text-left text-xs font-medium uppercase text-gray-500">Роль</th>
              <th className="px-4 py-3 text-left text-xs font-medium uppercase text-gray-500">Статус</th>
              <th className="px-4 py-3 text-left text-xs font-medium uppercase text-gray-500">Действия</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-gray-200">
            {users.map((u) => (
              <tr key={u.id} className="hover:bg-gray-50">
                <td className="px-4 py-3 text-sm font-medium text-gray-900">{u.fullName}</td>
                <td className="px-4 py-3 text-sm text-gray-600">{u.email}</td>
                <td className="px-4 py-3">
                  <select
                    value={u.role}
                    onChange={(e) => handleRoleChange(u.id, e.target.value)}
                    disabled={u.id === user?.id}
                    className={`rounded-md border-0 px-2 py-1 text-xs font-medium ${ROLE_COLORS[u.role]} ${u.id === user?.id ? 'cursor-not-allowed opacity-50' : 'cursor-pointer'}`}
                  >
                    {ROLES.map((r) => (
                      <option key={r} value={r}>{ROLE_LABELS[r]}</option>
                    ))}
                  </select>
                </td>
                <td className="px-4 py-3">
                  <span className={`inline-block rounded-full px-2 py-0.5 text-xs font-medium ${u.enabled ? 'bg-green-100 text-green-700' : 'bg-red-100 text-red-700'}`}>
                    {u.enabled ? 'Активен' : 'Заблокирован'}
                  </span>
                </td>
                <td className="px-4 py-3">
                  <button
                    onClick={() => handleToggle(u.id)}
                    disabled={u.id === user?.id}
                    className={`rounded-md px-3 py-1 text-xs font-medium ${
                      u.id === user?.id
                        ? 'cursor-not-allowed bg-gray-100 text-gray-400'
                        : u.enabled
                          ? 'bg-red-100 text-red-700 hover:bg-red-200'
                          : 'bg-green-100 text-green-700 hover:bg-green-200'
                    }`}
                  >
                    {u.enabled ? 'Заблокировать' : 'Разблокировать'}
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </>
  );
}
