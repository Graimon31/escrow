'use client';

import { useEffect, useState, useRef } from 'react';
import ProtectedRoute from '@/components/protected-route';
import {
  apiListDeals,
  apiGetDealDocuments,
  apiUploadDocument,
  type DealResponse,
  type DocumentResponse,
} from '@/lib/api';

export default function DocumentsPage() {
  return (
    <ProtectedRoute>
      <DocumentsContent />
    </ProtectedRoute>
  );
}

function DocumentsContent() {
  const [deals, setDeals] = useState<DealResponse[]>([]);
  const [selectedDeal, setSelectedDeal] = useState<string>('');
  const [documents, setDocuments] = useState<DocumentResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [uploading, setUploading] = useState(false);
  const fileInputRef = useRef<HTMLInputElement>(null);

  useEffect(() => {
    apiListDeals()
      .then((d) => {
        setDeals(d);
        if (d.length > 0) {
          setSelectedDeal(d[0].id);
        }
      })
      .catch(() => {})
      .finally(() => setLoading(false));
  }, []);

  useEffect(() => {
    if (!selectedDeal) return;
    apiGetDealDocuments(selectedDeal)
      .then(setDocuments)
      .catch(() => setDocuments([]));
  }, [selectedDeal]);

  const handleUpload = async (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (!file || !selectedDeal) return;
    setUploading(true);
    try {
      await apiUploadDocument(selectedDeal, file);
      const docs = await apiGetDealDocuments(selectedDeal);
      setDocuments(docs);
    } catch (err) {
      alert(err instanceof Error ? err.message : 'Ошибка загрузки');
    } finally {
      setUploading(false);
      if (fileInputRef.current) fileInputRef.current.value = '';
    }
  };

  const formatFileSize = (bytes: number) => {
    if (bytes < 1024) return bytes + ' Б';
    if (bytes < 1024 * 1024) return (bytes / 1024).toFixed(1) + ' КБ';
    return (bytes / (1024 * 1024)).toFixed(1) + ' МБ';
  };

  if (loading) {
    return (
      <div className="flex justify-center py-12">
        <div className="h-8 w-8 animate-spin rounded-full border-4 border-gray-300 border-t-blue-600" />
      </div>
    );
  }

  return (
    <>
      <div className="mb-6 flex items-center justify-between">
        <h1 className="text-2xl font-bold text-gray-900">Документы</h1>
        {selectedDeal && (
          <button
            onClick={() => fileInputRef.current?.click()}
            disabled={uploading}
            className="rounded-md bg-blue-600 px-4 py-2 text-sm font-medium text-white hover:bg-blue-700 disabled:opacity-50"
          >
            {uploading ? 'Загрузка...' : 'Загрузить документ'}
          </button>
        )}
        <input ref={fileInputRef} type="file" className="hidden" onChange={handleUpload} />
      </div>

      {deals.length === 0 ? (
        <div className="rounded-lg bg-white p-12 text-center shadow-sm">
          <p className="text-sm text-gray-500">Сделок пока нет. Документы привязаны к сделкам.</p>
        </div>
      ) : (
        <>
          <div className="mb-4">
            <label className="text-sm font-medium text-gray-700">Выберите сделку: </label>
            <select
              value={selectedDeal}
              onChange={(e) => setSelectedDeal(e.target.value)}
              className="ml-2 rounded-md border border-gray-300 px-3 py-1.5 text-sm"
            >
              {deals.map((d) => (
                <option key={d.id} value={d.id}>
                  {d.title} ({d.status})
                </option>
              ))}
            </select>
          </div>

          {documents.length === 0 ? (
            <div className="rounded-lg bg-white p-12 text-center shadow-sm">
              <svg className="mx-auto mb-3 h-12 w-12 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={1.5}>
                <path strokeLinecap="round" strokeLinejoin="round" d="M19.5 14.25v-2.625a3.375 3.375 0 0 0-3.375-3.375h-1.5A1.125 1.125 0 0 1 13.5 7.125v-1.5a3.375 3.375 0 0 0-3.375-3.375H8.25m2.25 0H5.625c-.621 0-1.125.504-1.125 1.125v17.25c0 .621.504 1.125 1.125 1.125h12.75c.621 0 1.125-.504 1.125-1.125V11.25a9 9 0 0 0-9-9Z" />
              </svg>
              <p className="text-sm text-gray-500">Нет документов для этой сделки</p>
              <p className="mt-1 text-xs text-gray-400">Загрузите документ с помощью кнопки выше</p>
            </div>
          ) : (
            <div className="overflow-x-auto rounded-lg bg-white shadow-sm">
              <table className="min-w-full divide-y divide-gray-200">
                <thead className="bg-gray-50">
                  <tr>
                    <th className="px-4 py-3 text-left text-xs font-medium uppercase text-gray-500">Имя файла</th>
                    <th className="px-4 py-3 text-left text-xs font-medium uppercase text-gray-500">Тип</th>
                    <th className="px-4 py-3 text-left text-xs font-medium uppercase text-gray-500">Размер</th>
                    <th className="px-4 py-3 text-left text-xs font-medium uppercase text-gray-500">Загружен</th>
                    <th className="px-4 py-3 text-left text-xs font-medium uppercase text-gray-500">Действия</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-gray-200">
                  {documents.map((doc) => (
                    <tr key={doc.id} className="hover:bg-gray-50">
                      <td className="px-4 py-3 text-sm font-medium text-gray-900">{doc.fileName}</td>
                      <td className="px-4 py-3 text-sm text-gray-600">
                        <span className="rounded bg-gray-100 px-2 py-0.5 text-xs">{doc.documentType}</span>
                      </td>
                      <td className="px-4 py-3 text-sm text-gray-600">{formatFileSize(doc.fileSize)}</td>
                      <td className="px-4 py-3 text-sm text-gray-500">{new Date(doc.createdAt).toLocaleString('ru-RU')}</td>
                      <td className="px-4 py-3">
                        <a
                          href={`${process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080'}/api/documents/${doc.id}/download`}
                          className="text-sm text-blue-600 hover:text-blue-800"
                        >
                          Скачать
                        </a>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </>
      )}
    </>
  );
}
