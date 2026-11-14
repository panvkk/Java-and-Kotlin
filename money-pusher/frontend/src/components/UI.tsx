import type { ReactNode } from 'react';
export function Button({ children, loading, variant = 'primary', ...props }: React.ButtonHTMLAttributes<HTMLButtonElement> & { loading?: boolean; variant?: 'primary' | 'secondary' }) {
  return <button className={`button button-${variant}`} disabled={loading || props.disabled} {...props}>{loading ? 'Обработка…' : children}</button>;
}
export function Field({ label, hint, error, children }: { label: string; hint?: string; error?: string; children: ReactNode }) {
  return <div className="field"><label>{label}{children}</label>{hint && <small>{hint}</small>}{error && <span className="field-error" role="alert">{error}</span>}</div>;
}
export function Status({ children, tone = 'info' }: { children: ReactNode; tone?: 'info' | 'error' | 'success' }) { return <p className={`status status-${tone}`} role="status">{children}</p>; }
export function Skeleton({ className = '' }: { className?: string }) { return <div className={`skeleton ${className}`} aria-hidden="true" />; }
export function EmptyState({ title, children }: { title: string; children: ReactNode }) { return <div className="empty"><span className="empty-mark">—</span><h3>{title}</h3><p>{children}</p></div>; }
