export default function HomePage() {
  return (
    <main className="mx-auto max-w-3xl p-8">
      <h1 className="text-3xl font-semibold">Платформа эскроу (Escrow Platform)</h1>
      <p className="mt-4 text-base text-slate-700">
        Шаг 2: работает инфраструктурный каркас с frontend и backend health endpoint.
      </p>
      <ul className="mt-6 list-disc pl-6 text-slate-800">
        <li>
          Проверка backend: <a className="text-blue-700 underline" href="http://localhost:8080/api/v1/health">/api/v1/health</a>
        </li>
        <li>
          Проверка actuator: <a className="text-blue-700 underline" href="http://localhost:8080/actuator/health">/actuator/health</a>
        </li>
      </ul>
    </main>
  );
}
