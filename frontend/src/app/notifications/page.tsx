'use client';

import ProtectedRoute from '@/components/protected-route';

export default function NotificationsPage() {
  return (
    <ProtectedRoute>
      <h1 className="mb-6 text-2xl font-bold text-gray-900">Notifications</h1>
      <div className="rounded-lg bg-white p-12 text-center shadow-sm">
        <svg className="mx-auto mb-3 h-12 w-12 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={1.5}>
          <path strokeLinecap="round" strokeLinejoin="round" d="M14.857 17.082a23.848 23.848 0 0 0 5.454-1.31A8.967 8.967 0 0 1 18 9.75V9A6 6 0 0 0 6 9v.75a8.967 8.967 0 0 1-2.312 6.022c1.733.64 3.56 1.085 5.455 1.31m5.714 0a24.255 24.255 0 0 1-5.714 0m5.714 0a3 3 0 1 1-5.714 0" />
        </svg>
        <p className="text-sm text-gray-500">No notifications</p>
        <p className="mt-1 text-xs text-gray-400">You will be notified about deal updates here</p>
      </div>
    </ProtectedRoute>
  );
}
