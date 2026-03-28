'use client';

import { FormEvent, useState } from 'react';
import { useRouter } from 'next/navigation';
import { login } from '../../lib/auth';

export default function LoginPage() {
  const [username, setUsername] = useState('admin');
  const [password, setPassword] = useState('admin123');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const router = useRouter();

  async function onSubmit(e: FormEvent) {
    e.preventDefault();
    setError('');
    setLoading(true);
    try {
      const result = await login(username, password);
      document.cookie = `auth_token=${result.accessToken}; path=/; max-age=7200`;
      document.cookie = `auth_role=${result.role}; path=/; max-age=7200`;
      document.cookie = `auth_name=${encodeURIComponent(result.displayName)}; path=/; max-age=7200`;
      router.push('/cabinet');
    } catch (err) {
      setError((err as Error).message);
    } finally {
      setLoading(false);
    }
  }

  return (
    <main className="mx-auto max-w-xl p-8">
      <h1 className="text-2xl font-semibold">Вход (Login)</h1>
      <p className="mt-2 text-sm text-slate-600">Тестовые логины: admin, operator, depositor, beneficiary.</p>
      <form onSubmit={onSubmit} className="mt-6 space-y-4 rounded-xl border bg-white p-6">
        <input className="w-full rounded border p-2" value={username} onChange={(e) => setUsername(e.target.value)} placeholder="Логин" />
        <input className="w-full rounded border p-2" type="password" value={password} onChange={(e) => setPassword(e.target.value)} placeholder="Пароль" />
        <button disabled={loading} className="rounded bg-blue-600 px-4 py-2 text-white disabled:opacity-50" type="submit">
          {loading ? 'Входим...' : 'Войти'}
        </button>
        {error && <p className="text-sm text-red-700">{error}</p>}
      </form>
    </main>
  );
}
