import Link from 'next/link';

export default function HomePage() {
  return (
    <main className="mx-auto max-w-3xl p-8">
      <h1 className="text-3xl font-semibold">Платформа эскроу (Escrow Platform)</h1>
      <p className="mt-4 text-base text-slate-700">Шаг 3: реализован auth-service, JWT login и ролевая модель.</p>
      <div className="mt-6 flex gap-3">
        <Link className="rounded bg-blue-600 px-4 py-2 text-white" href="/login">Войти</Link>
        <a className="rounded border px-4 py-2" href="http://localhost:8081/swagger-ui.html">Swagger auth-service</a>
              <Link className="rounded border px-4 py-2" href="/deals">Список сделок</Link>
      </div>
    </main>
  );
}
