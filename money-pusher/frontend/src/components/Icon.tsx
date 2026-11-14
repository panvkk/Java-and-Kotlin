type Props = { name: 'arrow' | 'moon' | 'menu' | 'logout' | 'send' | 'user'; size?: number };
export function Icon({ name, size = 18 }: Props) {
  const paths = {
    arrow: <><path d="M5 12h13M13 6l6 6-6 6" /></>,
    moon: <path d="M20 15.5A8 8 0 0 1 8.5 4 8.5 8.5 0 1 0 20 15.5Z" />,
    menu: <><path d="M4 7h16M4 12h16M4 17h16" /></>,
    logout: <><path d="M10 5H5v14h5M14 8l4 4-4 4M18 12H9" /></>,
    send: <><path d="m20 4-17 7 7 2 2 7 8-16ZM10 13l10-9" /></>,
    user: <><circle cx="12" cy="8" r="3" /><path d="M5 20a7 7 0 0 1 14 0" /></>,
  }[name];
  return <svg aria-hidden="true" width={size} height={size} viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.8" strokeLinecap="round" strokeLinejoin="round">{paths}</svg>;
}
