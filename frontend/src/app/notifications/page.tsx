'use client';

import { useEffect, useState, useCallback } from 'react';
import ProtectedRoute from '@/components/protected-route';
import {
  apiGetNotifications,
  apiMarkNotificationRead,
  apiMarkAllNotificationsRead,
  type NotificationResponse,
} from '@/lib/api';
import Link from 'next/link';

export default function NotificationsPage() {
  return (
    <ProtectedRoute>
      <NotificationsContent />
    </ProtectedRoute>
  );
}

const TYPE_ICONS: Record<string, string> = {
  DEAL_CREATED: '📄',
  DEAL_SUBMITTED: '📤',
  DEAL_AGREED: '🤝',
  DEAL_DECLINED: '❌',
  DEAL_FUNDED: '💰',
  DEAL_DELIVERED: '📦',
  DEAL_COMPLETED: '✅',
  DEAL_REFUNDED: '↩️',
  DEAL_DISPUTED: '⚠️',
  DISPUTE_RESOLVED: '⚖️',
  DEAL_CANCELLED: '🚫',
};

function NotificationsContent() {
  const [notifications, setNotifications] = useState<NotificationResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  const loadNotifications = useCallback(() => {
    apiGetNotifications()
      .then(setNotifications)
      .catch((e) => setError(e.message))
      .finally(() => setLoading(false));
  }, []);

  useEffect(() => { loadNotifications(); }, [loadNotifications]);

  const handleMarkRead = async (id: string) => {
    try {
      const updated = await apiMarkNotificationRead(id);
      setNotifications((prev) =>
        prev.map((n) => (n.id === updated.id ? updated : n))
      );
    } catch (e) {
      console.error('Failed to mark as read', e);
    }
  };

  const handleMarkAllRead = async () => {
    try {
      await apiMarkAllNotificationsRead();
      setNotifications((prev) => prev.map((n) => ({ ...n, read: true })));
    } catch (e) {
      console.error('Failed to mark all as read', e);
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
      <div className="rounded-lg bg-red-50 p-6 text-center text-sm text-red-700">
        {error}
      </div>
    );
  }

  const unreadCount = notifications.filter((n) => !n.read).length;

  return (
    <>
      <div className="mb-6 flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">Notifications</h1>
          {unreadCount > 0 && (
            <p className="mt-1 text-sm text-gray-500">{unreadCount} unread</p>
          )}
        </div>
        {unreadCount > 0 && (
          <button
            onClick={handleMarkAllRead}
            className="rounded-md border border-gray-300 px-4 py-2 text-sm font-medium text-gray-700 hover:bg-gray-50"
          >
            Mark all as read
          </button>
        )}
      </div>

      {notifications.length === 0 ? (
        <div className="rounded-lg bg-white p-12 text-center shadow-sm">
          <svg className="mx-auto mb-3 h-12 w-12 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={1.5}>
            <path strokeLinecap="round" strokeLinejoin="round" d="M14.857 17.082a23.848 23.848 0 0 0 5.454-1.31A8.967 8.967 0 0 1 18 9.75V9A6 6 0 0 0 6 9v.75a8.967 8.967 0 0 1-2.312 6.022c1.733.64 3.56 1.085 5.455 1.31m5.714 0a24.255 24.255 0 0 1-5.714 0m5.714 0a3 3 0 1 1-5.714 0" />
          </svg>
          <p className="text-sm text-gray-500">No notifications yet</p>
          <p className="mt-1 text-xs text-gray-400">You will be notified about deal updates here</p>
        </div>
      ) : (
        <div className="space-y-2">
          {notifications.map((n) => (
            <div
              key={n.id}
              className={`rounded-lg bg-white p-4 shadow-sm transition-colors ${
                n.read ? 'opacity-70' : 'border-l-4 border-blue-500'
              }`}
            >
              <div className="flex items-start gap-3">
                <span className="mt-0.5 text-xl">{TYPE_ICONS[n.type] || '🔔'}</span>
                <div className="min-w-0 flex-1">
                  <div className="flex items-center gap-2">
                    <h3 className={`text-sm ${n.read ? 'text-gray-700' : 'font-semibold text-gray-900'}`}>
                      {n.title}
                    </h3>
                    {!n.read && (
                      <span className="inline-block h-2 w-2 rounded-full bg-blue-500" />
                    )}
                  </div>
                  <p className="mt-0.5 text-sm text-gray-600">{n.message}</p>
                  <div className="mt-2 flex items-center gap-3">
                    <span className="text-xs text-gray-400">
                      {new Date(n.createdAt).toLocaleString()}
                    </span>
                    {n.dealId && (
                      <Link
                        href={`/deals/${n.dealId}`}
                        className="text-xs text-blue-600 hover:text-blue-800"
                      >
                        View deal
                      </Link>
                    )}
                    {!n.read && (
                      <button
                        onClick={() => handleMarkRead(n.id)}
                        className="text-xs text-gray-400 hover:text-gray-600"
                      >
                        Mark as read
                      </button>
                    )}
                  </div>
                </div>
              </div>
            </div>
          ))}
        </div>
      )}
    </>
  );
}
