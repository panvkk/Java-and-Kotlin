import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { api } from '../lib/api';
import type { LeaderboardItem } from '../types';
import { EmptyState, Skeleton, Status } from '../components/UI';
import { getUser } from '../lib/session';

export function Leaderboard() {
  const [items, setItems] = useState<LeaderboardItem[] | null>(null); const [error, setError] = useState('');
  useEffect(() => { api.leaderboard().then((data) => setItems(data.items || [])).catch((e) => setError(e.message)); }, []);
  return <div className="page"><section className="page-intro"><div><p className="kicker">топ · 10 позиций</p><h1>Кто держит<br /><span>дорогу.</span></h1><p>Публичная таблица балансов. Обновляй страницу, чтобы сверить позиции.</p></div><div className="rank-seal"><span>top</span><strong>10</strong><small>asphalt holders</small></div></section><section className="panel leaderboard-panel"><div className="panel-head"><div><h2>Лидерборд</h2><p>Наибольший баланс сейчас</p></div><span className="live-tag">live</span></div>{error && <Status tone="error">{error}</Status>}{items === null ? <div className="skeleton-list">{[1, 2, 3].map((n) => <Skeleton key={n} />)}</div> : items.length === 0 ? <EmptyState title="Пока пусто">Первый coin может занять первое место.</EmptyState> : <div className="table-wrap" role="region" aria-label="Таблица лидеров" tabIndex={0}><table><caption className="sr-only">Топ игроков Asphalt Clicker</caption><thead><tr><th scope="col">Место</th><th scope="col">Игрок</th><th scope="col">Баланс</th></tr></thead><tbody>{items.slice(0, 10).map((item, index) => <tr className={item.user_email === getUser()?.email ? 'current-row' : ''} key={item.user_email}><th scope="row">{String(index + 1).padStart(2, '0')}</th><td><Link className="leaderboard-player-link" to={`/profile/${encodeURIComponent(item.user_email)}`}><strong>{item.user_nickname}</strong><small>{item.user_email}</small></Link></td><td>{item.balance.toLocaleString('ru-RU')} <small>coin</small></td></tr>)}</tbody></table></div>}</section></div>;
}
