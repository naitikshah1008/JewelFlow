import { Button } from "../components/Button";
import { Card } from "../components/Card";

interface NotFoundPageProps {
  onNavigate: (path: string) => void;
}

export function NotFoundPage({ onNavigate }: NotFoundPageProps) {
  return (
    <Card title="Page Not Found">
      <div className="stack">
        <p>The requested page does not exist.</p>
        <div>
          <Button onClick={() => onNavigate("/")}>Go to Dashboard</Button>
        </div>
      </div>
    </Card>
  );
}
