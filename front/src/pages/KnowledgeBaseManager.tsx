import { Button } from '@/components/ui/button';
import { ArrowLeft, BookOpen } from 'lucide-react';
import { useNavigate } from 'react-router-dom';

export default function KnowledgeBaseManager() {
  const navigate = useNavigate();

  return (
    <div className="min-h-screen bg-background">
      <header className="border-b border-border bg-card">
        <div className="container mx-auto px-6 py-4 flex items-center gap-4">
          <Button variant="ghost" size="icon" onClick={() => navigate('/')}>
            <ArrowLeft className="w-5 h-5" />
          </Button>
          <div>
            <h1 className="text-2xl font-bold">Knowledge Base Manager</h1>
            <p className="text-sm text-muted-foreground">Mission Documentation System</p>
          </div>
        </div>
      </header>

      <main className="container mx-auto px-6 py-8">
        <div className="max-w-4xl mx-auto">
          <div className="text-center space-y-6 p-12 bg-card border border-border rounded-lg">
            <div className="w-24 h-24 rounded-full bg-primary/10 flex items-center justify-center mx-auto">
              <BookOpen className="w-12 h-12 text-primary" />
            </div>
            <h2 className="text-4xl font-bold">Welcome to Knowledge Base</h2>
            <p className="text-xl text-muted-foreground max-w-2xl mx-auto">
              The mission documentation and knowledge management system is currently being configured.
              This interface will provide comprehensive access to mission protocols, procedures, and critical information.
            </p>
          </div>
        </div>
      </main>
    </div>
  );
}
