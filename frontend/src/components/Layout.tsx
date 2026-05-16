import type { ReactNode } from "react";
import type { AuthSession } from "../types";
import { Button } from "./Button";

export interface NavItem {
  path: string;
  label: string;
}

interface LayoutProps {
  children: ReactNode;
  path: string;
  title: string;
  navItems: NavItem[];
  session: AuthSession;
  onNavigate: (path: string) => void;
  onLogout: () => void;
}

export function Layout({ children, path, title, navItems, session, onNavigate, onLogout }: LayoutProps) {
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
            <span>{session.username} / {session.role}</span>
          </div>
          <Button variant="secondary" type="button" onClick={onLogout}>
            Log Out
          </Button>
        </header>
        <main>{children}</main>
      </div>
    </div>
  );
}
