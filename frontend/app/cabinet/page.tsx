'use client';

import Link from 'next/link';

function readCookie(name: string): string {
  const value = document.cookie
    .split('; ')
    .find((row) => row.startsWith(`${name}=`))
    ?.split('=')[1];
  return value ? decodeURIComponent(value) : '';
}

export default function CabinetPage() {
  const role = typeof document !== 'undefined' ? readCookie('auth_role') : '';
  const name = typeof document !== 'undefined' ? readCookie('auth_name') : '';

  return (
    <main className="mx-auto max-w-3xl p-8">
      <h1 className="text-2xl font-semibold">Личный кабинет (Cabinet)</h1>
      <p className="mt-2">Пользователь: {name || 'не определён'}</p>
      <p className="mt-1">Роль: {role || 'не определена'}</p>

      <div className="mt-6 flex flex-wrap gap-3">
        <Link className="rounded border px-3 py-2" href="/cabinet/admin">Зона ADMIN</Link>
        <Link className="rounded border px-3 py-2" href="/cabinet/operator">Зона OPERATOR</Link>
        <Link className="rounded border px-3 py-2" href="/cabinet/depositor">Зона DEPOSITOR</Link>
        <Link className="rounded border px-3 py-2" href="/cabinet/beneficiary">Зона BENEFICIARY</Link>
      </div>
    </main>
  );
}
