import { FormEvent, useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import { Button, Field, Status } from '../components/UI';
import { Skeleton } from '../components/UI';
import { api } from '../lib/api';
import { saveUser } from '../lib/session';
import type { User } from '../types';
export function Profile({ user, onUserChange }: { user: User; onUserChange: (user: User) => void }) {
  const [nickname, setNickname] = useState(user.nickname); const [saved, setSaved] = useState(false);
  const submit = (event: FormEvent) => { event.preventDefault(); const next = { ...user, nickname: nickname.trim() || user.nickname }; saveUser(next); onUserChange(next); setNickname(next.nickname); setSaved(true); };
  return <div className="page"><section className="page-intro profile-intro"><div><p className="kicker">личный счёт</p><h1>Твой<br /><span>профиль.</span></h1><p>Никнейм можно менять локально — email и роль принадлежат системе.</p></div><div className="profile-avatar">{user.nickname.slice(0, 1).toUpperCase()}</div></section><section className="profile-grid"><div className="panel profile-card"><div className="panel-head"><div><h2>Данные игрока</h2><p>Синхронизировано с gateway</p></div><span className="role-tag">{user.role}</span></div><form className="profile-form" onSubmit={submit}><Field label="Никнейм"><input value={nickname} onChange={(e) => { setNickname(e.target.value); setSaved(false); }} minLength={2} required /></Field><div className="readonly-row"><span>Email</span><strong>{user.email}</strong></div><div className="readonly-row"><span>Роль</span><strong>{user.role}</strong></div><Button type="submit">Сохранить изменения</Button>{saved && <Status tone="success">Профиль обновлён.</Status>}</form></div><aside className="panel account-card"><p className="kicker">статус счёта</p><div className="account-number">AC / 001</div><div className="account-rule" /><p>Асфальтовый счёт активен. Все игровые операции проходят только через авторизованный gateway.</p><span className="live-tag">active</span></aside></section></div>;
}

export function UserProfile() {
  const { email = '' } = useParams();
  const decodedEmail = decodeURIComponent(email);
  const [user, setUser] = useState<User | null>(null);
  const [balance, setBalance] = useState<number | null>(null);
  const [error, setError] = useState('');
  useEffect(() => {
    let active = true;
    Promise.all([api.user(decodedEmail), api.balance(decodedEmail)])
      .then(([profile, currentBalance]) => {
        if (!active) return;
        setUser(profile);
        setBalance(currentBalance.user_balance);
      })
      .catch((cause) => { if (active) setError(cause instanceof Error ? cause.message : 'Профиль недоступен.'); });
    return () => { active = false; };
  }, [decodedEmail]);
  return <div className="page"><section className="page-intro profile-intro"><div><p className="kicker">публичный профиль</p>{error ? <><h1>Профиль<br /><span>недоступен.</span></h1><Status tone="error">{error}</Status></> : user ? <><h1>{user.nickname}<br /><span>на дороге.</span></h1><p>Открытый профиль игрока Asphalt Clicker.</p></> : <><Skeleton className="profile-title-skeleton" /><p>Загружаем данные игрока…</p></>}</div><div className="profile-avatar">{user ? user.nickname.slice(0, 1).toUpperCase() : '…'}</div></section>{user && <section className="profile-grid"><div className="panel profile-card"><div className="panel-head"><div><h2>Профиль игрока</h2><p>Данные из gateway</p></div><span className="role-tag">{user.role}</span></div><div className="readonly-row"><span>Никнейм</span><strong>{user.nickname}</strong></div><div className="readonly-row"><span>Email</span><strong>{user.email}</strong></div><div className="readonly-row"><span>Баланс</span><strong className="profile-balance">{balance === null ? '—' : `${balance.toLocaleString('ru-RU')} coin`}</strong></div></div><aside className="panel account-card"><p className="kicker">идентификатор игрока</p><div className="account-number">AC / {user.email.slice(0, 3).toUpperCase()}</div><div className="account-rule" /><p>Профиль доступен из таблицы лидеров и показывает только публичные данные.</p><span className="live-tag">public</span></aside></section>}</div>;
}
