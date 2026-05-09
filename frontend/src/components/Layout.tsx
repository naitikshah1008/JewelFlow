import type { ReactNode } from "react";

export interface NavItem {
  path: string;
  label: string;
}

interface LayoutProps {
  children: ReactNode;
  path: string;
  title: string;
  navItems: NavItem[];
  onNavigate: (path: string) => void;
}

export function Layout({ children, path, title, navItems, onNavigate }: LayoutProps) {
  return (
    <div className="shell">
      <aside className="sidebar">
        <button className="brand" type="button" onClick={() => onNavigate("/")}>
          <span className="brand-mark">JF</span>
          <span>
            <strong>JewelFlow</strong>
            <small>Store dashboard</small>
          </span>
        </button>
        <nav className="nav">
          {navItems.map((item) => (
            <button
              key={item.path}
              type="button"
              className={path === item.path || path.startsWith(`${item.path}/`) ? "active" : ""}
              onClick={() => onNavigate(item.path)}
            >
              {item.label}
            </button>
          ))}
        </nav>
      </aside>
      <div className="workspace">
        <header className="topbar">
          <div>
            <h1>{title}</h1>
            <span>Local demo at http://localhost:8080</span>
          </div>
        </header>
        <main>{children}</main>
      </div>
    </div>
  );
}
