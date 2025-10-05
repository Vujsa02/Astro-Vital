import { useState } from 'react';
import { Button } from '@/components/ui/button';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';
import { ModuleView } from '@/components/ModuleView';
import { SystemOverview } from '@/components/SystemOverview';
import { FindingsPanel } from '@/components/FindingsPanel';
import { ModuleID } from '@/types/module';
import { ArrowLeft, LayoutDashboard, Monitor, AlertTriangle } from 'lucide-react';
import { useNavigate } from 'react-router-dom';

export default function LifeSupportEngineer() {
  const navigate = useNavigate();
  const [selectedModule, setSelectedModule] = useState<ModuleID>('CMD');
  const [activeTab, setActiveTab] = useState('overview');

  const modules: { id: ModuleID; name: string }[] = [
    { id: 'CMD', name: 'Command' },
    { id: 'LAB', name: 'Laboratory' },
    { id: 'COMM', name: 'Communication' },
  ];

  const handleModuleSelect = (moduleId: ModuleID) => {
    setSelectedModule(moduleId);
    setActiveTab('modules');
  };

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
                <p className="text-sm text-muted-foreground">
                  {activeTab === 'overview' 
                    ? 'System-wide monitoring and control' 
                    : `${modules.find(m => m.id === selectedModule)?.name} Module`
                  }
                </p>
              </div>
            </div>
          </div>

          <Tabs value={activeTab} onValueChange={setActiveTab} className="mt-4">
            <TabsList className="grid w-full grid-cols-3 max-w-md">
              <TabsTrigger value="overview" className="flex items-center gap-2">
                <LayoutDashboard className="w-4 h-4" />
                Overview
              </TabsTrigger>
              <TabsTrigger value="modules" className="flex items-center gap-2">
                <Monitor className="w-4 h-4" />
                Modules
              </TabsTrigger>
              <TabsTrigger value="findings" className="flex items-center gap-2">
                <AlertTriangle className="w-4 h-4" />
                Findings
              </TabsTrigger>
            </TabsList>
          </Tabs>

          {activeTab === 'modules' && (
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
          )}
        </div>
      </header>

      <main className="container mx-auto px-6 py-8">
        <Tabs value={activeTab} onValueChange={setActiveTab}>
          <TabsContent value="overview" className="space-y-6">
            <SystemOverview onModuleSelect={handleModuleSelect} />
          </TabsContent>

          <TabsContent value="modules">
            <ModuleView moduleID={selectedModule} allowServerDelete />
          </TabsContent>

          <TabsContent value="findings">
            <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
              <FindingsPanel />
              <div className="space-y-6">
                {modules.map((module) => (
                  <FindingsPanel 
                    key={module.id}
                    moduleId={module.id}
                    maxHeight="300px"
                  />
                ))}
              </div>
            </div>
          </TabsContent>
        </Tabs>
      </main>
    </div>
  );
}
