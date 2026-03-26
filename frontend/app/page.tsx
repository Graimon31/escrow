const cards = [
  'Активные сделки (active deals)',
  'Ожидают внесения средств (awaiting funding)',
  'Средства удерживаются (funds secured)',
  'Ожидает исполнение бенефициара (beneficiary fulfillment)',
  'Ожидает проверку депонента (depositor review)',
  'Споры / возвраты / раскрытия (disputes/refunds/releases)'
];

export default function Page(){
  return <main style={{maxWidth:1200,margin:'24px auto',padding:'0 16px'}}>
    <h1>Платформа счёта эскроу (Escrow-first platform)</h1>
    <p>Главная страница (dashboard) с ключевыми модулями MVP.</p>
    <section style={{display:'grid',gridTemplateColumns:'repeat(3,1fr)',gap:12}}>
      {cards.map(c => <div key={c} className="card">{c}</div>)}
    </section>
    <div className="card" style={{marginTop:12}}>
      <h3>Ключевые экраны</h3>
      <ul>
        <li>Создание и согласование сделки (deal/agreement)</li>
        <li>Счёт эскроу и внесение депозита (escrow account/funding)</li>
        <li>Исполнение обязательства (fulfillment)</li>
        <li>Проверка депонента (review)</li>
        <li>Раскрытие / возврат / закрытие (resolution)</li>
        <li>Споры (dispute)</li>
      </ul>
    </div>
  </main>
}
