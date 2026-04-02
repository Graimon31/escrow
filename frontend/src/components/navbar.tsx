'use client';

import { useAuth } from '@/contexts/auth-context';
import { useRouter } from 'next/navigation';

const ROLE_LABELS: Record<string, string> = {
  DEPOSITOR: 'Depositor',
  BENEFICIARY: 'Beneficiary',
  OPERATOR: 'Operator',
  ADMINISTRATOR: 'Administrator',
};

export default function Navbar() {
  const { user, logout } = useAuth();
  const router = useRouter();

  const handleLogout = () => {
    logout();
    router.push('/login');
  };

  if (!user) return null;

  return (
    <nav className="border-b border-gray-200 bg-white">
      <div className="mx-auto flex h-16 max-w-7xl items-center justify-between px-4 sm:px-6 lg:px-8">
        <h1 className="text-xl font-bold text-gray-900">Escrow</h1>
        <div className="flex items-center gap-4">
          <div className="text-sm text-gray-600">
            <span className="font-medium text-gray-900">{user.fullName}</span>
            <span className="ml-2 rounded-full bg-blue-100 px-2 py-0.5 text-xs font-medium text-blue-800">
              {ROLE_LABELS[user.role] || user.role}
            </span>
          </div>
          <button
            onClick={handleLogout}
            className="rounded-md bg-gray-100 px-3 py-1.5 text-sm font-medium text-gray-700 hover:bg-gray-200"
          >
            Logout
          </button>
        </div>
      </div>
    </nav>
  );
}
