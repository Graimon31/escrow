'use client';

import ProtectedRoute from '@/components/protected-route';

export default function DocumentsPage() {
  return (
    <ProtectedRoute>
      <h1 className="mb-6 text-2xl font-bold text-gray-900">Documents</h1>
      <div className="rounded-lg bg-white p-12 text-center shadow-sm">
        <svg className="mx-auto mb-3 h-12 w-12 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={1.5}>
          <path strokeLinecap="round" strokeLinejoin="round" d="M19.5 14.25v-2.625a3.375 3.375 0 0 0-3.375-3.375h-1.5A1.125 1.125 0 0 1 13.5 7.125v-1.5a3.375 3.375 0 0 0-3.375-3.375H8.25m2.25 0H5.625c-.621 0-1.125.504-1.125 1.125v17.25c0 .621.504 1.125 1.125 1.125h12.75c.621 0 1.125-.504 1.125-1.125V11.25a9 9 0 0 0-9-9Z" />
        </svg>
        <p className="text-sm text-gray-500">No documents yet</p>
        <p className="mt-1 text-xs text-gray-400">Documents from your deals will appear here</p>
      </div>
    </ProtectedRoute>
  );
}
