import { useData } from '@/contexts/DataContext';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import { Button } from '@/components/ui/button';
import { StatusIndicator } from './StatusIndicator';
import { ModuleID } from '@/types/module';
import { Activity, Wifi, WifiOff, RefreshCw } from 'lucide-react';

interface SystemOverviewProps {
  onModuleSelect: (moduleId: ModuleID) => void;
}

export const SystemOverview = ({ onModuleSelect }: SystemOverviewProps) => {
  const { state, getFindingsForModule, refreshModuleData } = useData();

  const modules: { id: ModuleID; name: string }[] = [
    { id: 'CMD', name: 'Command' },
    { id: 'LAB', name: 'Laboratory' },
    { id: 'COMM', name: 'Communication' },
  ];

  const getModuleStatus = (moduleId: ModuleID): 'good' | 'warning' | 'critical' => {
    const findings = getFindingsForModule(moduleId);
    if (findings.some(f => f.priority === 'CRITICAL')) return 'critical';
    if (findings.some(f => f.priority === 'HIGH')) return 'critical';
    if (findings.some(f => f.priority === 'MEDIUM')) return 'warning';
    return 'good';
  };

  const getOverallSystemStatus = (): 'good' | 'warning' | 'critical' => {
    const allFindings = state.findings.filter(f => !f.resolved);
    if (allFindings.some(f => f.priority === 'CRITICAL')) return 'critical';
    if (allFindings.some(f => f.priority === 'HIGH')) return 'critical';
    if (allFindings.some(f => f.priority === 'MEDIUM')) return 'warning';
    return 'good';
  };

  const totalFindings = state.findings.filter(f => !f.resolved).length;
  const overallStatus = getOverallSystemStatus();

  return (
    <div className="space-y-6">
      {/* System Status Header */}
      <Card>
        <CardHeader>
          <div className="flex items-center justify-between">
            <CardTitle className="flex items-center gap-2">
              <Activity className="w-5 h-5" />
              System Overview
            </CardTitle>
            <div className="flex items-center gap-3">
              <div className="flex items-center gap-2">
                {state.connectionStatus === 'online' ? (
                  <Wifi className="w-4 h-4 text-green-500" />
                ) : (
                  <WifiOff className="w-4 h-4 text-red-500" />
                )}
                <Badge variant={state.connectionStatus === 'online' ? 'default' : 'destructive'}>
                  {state.connectionStatus}
                </Badge>
              </div>
              <Button
                variant="outline"
                size="sm"
                onClick={() => refreshModuleData()}
                disabled={state.isLoading}
              >
                <RefreshCw className="w-4 h-4 mr-2" />
                Refresh All
              </Button>
            </div>
          </div>
        </CardHeader>
        <CardContent>
          <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
            <StatusIndicator
              status={overallStatus}
              label="Overall Status"
              value={overallStatus === 'good' ? 'NOMINAL' : overallStatus === 'warning' ? 'CAUTION' : 'ALERT'}
              unit=""
            />
            <StatusIndicator
              status={totalFindings > 0 ? 'warning' : 'good'}
              label="Active Findings"
              value={totalFindings.toString()}
              unit="items"
            />
            <StatusIndicator
              status="good"
              label="Last Update"
              value={new Date(state.lastUpdated).toLocaleTimeString()}
              unit=""
            />
            <StatusIndicator
              status={state.isLoading ? 'warning' : 'good'}
              label="System Load"
              value={state.isLoading ? 'PROCESSING' : 'READY'}
              unit=""
            />
          </div>
        </CardContent>
      </Card>

      {/* Module Status Grid */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
        {modules.map((module) => {
          const moduleData = state.modules[module.id];
          const moduleFindings = getFindingsForModule(module.id);
          const status = getModuleStatus(module.id);

          return (
            <Card 
              key={module.id} 
              className="cursor-pointer hover:shadow-md transition-shadow"
              onClick={() => onModuleSelect(module.id)}
            >
              <CardHeader className="pb-3">
                <div className="flex items-center justify-between">
                  <CardTitle className="text-lg">{module.name}</CardTitle>
                  <Badge variant={status === 'good' ? 'default' : status === 'warning' ? 'secondary' : 'destructive'}>
                    {status.toUpperCase()}
                  </Badge>
                </div>
                <p className="text-sm text-muted-foreground">Module {module.id}</p>
              </CardHeader>
              <CardContent className="space-y-3">
                {/* Key Environmental Metrics */}
                      <div className="grid grid-cols-2 gap-2 text-sm">
                        <div>
                          <span className="text-muted-foreground">O₂:</span>
                          <span className="ml-1 font-mono">{moduleData?.environment?.o2Level != null ? `${moduleData.environment.o2Level.toFixed(1)}%` : 'N/A'}</span>
                        </div>
                        <div>
                          <span className="text-muted-foreground">Temp:</span>
                          <span className="ml-1 font-mono">{moduleData?.environment?.temperature != null ? `${moduleData.environment.temperature.toFixed(1)}°C` : 'N/A'}</span>
                        </div>
                        <div>
                          <span className="text-muted-foreground">CO₂:</span>
                          <span className="ml-1 font-mono">{moduleData?.environment?.co2Level != null ? `${(moduleData.environment.co2Level * 100).toFixed(2)}%` : 'N/A'}</span>
                        </div>
                        <div>
                          <span className="text-muted-foreground">Press:</span>
                          <span className="ml-1 font-mono">{moduleData?.environment?.pressure != null ? `${moduleData.environment.pressure.toFixed(1)} kPa` : 'N/A'}</span>
                        </div>
                      </div>

                {/* Findings Summary */}
                {moduleFindings.length > 0 ? (
                  <div className="bg-yellow-50 border border-yellow-200 rounded p-2">
                    <p className="text-sm text-yellow-800">
                      {moduleFindings.length} active finding{moduleFindings.length !== 1 ? 's' : ''}
                    </p>
                  </div>
                ) : (
                  <div className="bg-green-50 border border-green-200 rounded p-2">
                    <p className="text-sm text-green-800">All systems nominal</p>
                  </div>
                )}

                {/* Equipment Status */}
                <div className="flex justify-between text-xs text-muted-foreground">
                  <span>Ventilation: {moduleData.ventilation.degraded ? '⚠️' : '✅'}</span>
                  <span>Filter: {moduleData.airFilter.dirty ? '⚠️' : '✅'}</span>
                  <span>Water: {moduleData.waterRecycling.degraded ? '⚠️' : '✅'}</span>
                </div>
              </CardContent>
            </Card>
          );
        })}
      </div>
    </div>
  );
};