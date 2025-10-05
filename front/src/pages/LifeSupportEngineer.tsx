import { useState } from 'react';
import { Button } from '@/components/ui/button';
import { ModuleView } from '@/components/ModuleView';
import { ModuleID } from '@/types/module';
import { ArrowLeft } from 'lucide-react';
import { useNavigate } from 'react-router-dom';

export default function LifeSupportEngineer() {
  const navigate = useNavigate();
  const [selectedModule, setSelectedModule] = useState<ModuleID>('CMD');

  const modules: { id: ModuleID; name: string }[] = [
    { id: 'CMD', name: 'Command' },
    { id: 'LAB', name: 'Laboratory' },
    { id: 'COMM', name: 'Communication' },
  ];

  return (
    <div className="min-h-screen bg-background">
      <header className="border-b border-border bg-card">
        <div className="container mx-auto px-6 py-4">
          <div className="flex items-center justify-between">
            <div className="flex items-center gap-4">
              <Button variant="ghost" size="icon" onClick={() => navigate('/')}>
                <ArrowLeft className="w-5 h-5" />
              </Button>
              <div>
                <h1 className="text-2xl font-bold">Life Support Engineer Dashboard</h1>
                <p className="text-sm text-muted-foreground">All Modules Monitoring</p>
              </div>
            </div>
          </div>

          <div className="flex gap-3 mt-4">
            {modules.map((module) => (
              <Button
                key={module.id}
                variant={selectedModule === module.id ? 'default' : 'outline'}
                onClick={() => setSelectedModule(module.id)}
                className={
                  selectedModule === module.id
                    ? 'bg-primary text-primary-foreground'
                    : 'border-border hover:border-primary'
                }
              >
                {module.name}
              </Button>
            ))}
          </div>
        </div>
      </header>

      <main className="container mx-auto px-6 py-8">
        <ModuleView moduleID={selectedModule} />
      </main>
    </div>
  );
}
