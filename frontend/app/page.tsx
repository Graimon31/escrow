const kpi = [
  { title: 'Активные сделки', en: 'Active deals', value: 12, delta: '+2 за сутки' },
  { title: 'Ожидают пополнения', en: 'Awaiting funding', value: 4, delta: '1 просрочено' },
  { title: 'На проверке депонента', en: 'Awaiting depositor review', value: 3, delta: '2 требуют действия' },
  { title: 'Открытые споры', en: 'Open disputes', value: 1, delta: 'высокий приоритет' }
]

const queue = [
  { deal: 'ESC-2026-00041', stage: 'Ожидает внесения депозита (awaiting funding)', who: 'Депонент (depositor)', due: 'сегодня 18:00' },
  { deal: 'ESC-2026-00038', stage: 'Ожидает подтверждения основания (depositor review)', who: 'Депонент (depositor)', due: 'завтра 12:00' },
  { deal: 'ESC-2026-00035', stage: 'Спор (dispute)', who: 'Оператор (operator)', due: 'сегодня 16:30' }
]

const timeline = [
  'Сделка создана (deal draft created)',
  'Условия согласованы (agreement accepted)',
  'Счёт эскроу открыт (escrow account opened)',
  'Средства удерживаются (funds held in escrow)',
  'Исполнение заявлено (beneficiary fulfillment submitted)',
  'Проверка депонента (depositor review in progress)'
]

export default function Page() {
  return (
    <main className="page">
      <header className="header card">
        <div>
          <h1>Платформа счёта эскроу (Escrow-first platform)</h1>
          <p>Главная страница (dashboard) для оперативного контроля жизненного цикла сделки.</p>
        </div>
        <div className="actions">
          <button className="btn btn-primary">Создать сделку (Create deal)</button>
          <button className="btn btn-secondary">Открыть споры (Open disputes)</button>
        </div>
      </header>

      <section className="kpi-grid">
        {kpi.map((item) => (
          <article key={item.title} className="card kpi-card">
            <div className="kpi-title">{item.title} <span>({item.en})</span></div>
            <div className="kpi-value">{item.value}</div>
            <div className="kpi-delta">{item.delta}</div>
          </article>
        ))}
      </section>

      <section className="content-grid">
        <article className="card">
          <h2>Очередь действий (action queue)</h2>
          <table className="table">
            <thead>
              <tr>
                <th>Сделка</th><th>Этап</th><th>Кто выполняет</th><th>Срок</th><th></th>
              </tr>
            </thead>
            <tbody>
              {queue.map((row) => (
                <tr key={row.deal}>
                  <td>{row.deal}</td>
                  <td>{row.stage}</td>
                  <td>{row.who}</td>
                  <td>{row.due}</td>
                  <td><button className="btn btn-link">Открыть</button></td>
                </tr>
              ))}
            </tbody>
          </table>
        </article>

        <article className="card">
          <h2>Состояние сделки (deal state timeline)</h2>
          <ol className="timeline">
            {timeline.map((step, index) => (
              <li key={step} className={index < 5 ? 'done' : 'current'}>{step}</li>
            ))}
          </ol>
          <div className="status-badges">
            <span className="badge badge-green">FUNDS_SECURED</span>
            <span className="badge badge-blue">AWAITING_DEPOSITOR_REVIEW</span>
            <span className="badge badge-orange">DISPUTE: 1</span>
          </div>
        </article>
      </section>

      <section className="card modules">
        <h2>Модули продукта (product modules)</h2>
        <div className="module-grid">
          {[
            'Сделки (Deal / Agreement)', 'Счета эскроу (Escrow Account)', 'Внесение средств (Funding)',
            'Исполнение (Fulfillment)', 'Проверка депонента (Review)', 'Исход сделки (Resolution)',
            'Споры (Dispute)', 'Документы (Documents)', 'Уведомления (Notifications)',
            'Аудит (Audit)', 'Роли и доступ (RBAC)', 'Наблюдаемость (Observability)'
          ].map((m) => <div key={m} className="module-item">{m}</div>)}
        </div>
      </section>
    </main>
  )
}
