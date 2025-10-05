import { useState, useEffect } from 'react';
import { ModuleID } from '@/types/module';
import { useData } from '@/contexts/DataContext';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';
import { Button } from '@/components/ui/button';
import { Badge } from '@/components/ui/badge';
import { Loader2, RefreshCw, AlertTriangle } from 'lucide-react';
import { EnvironmentTab } from './EnvironmentTab';
import { SymptomsTab } from './SymptomsTab';
import { EquipmentTab } from './EquipmentTab';

interface ModuleViewProps {
  moduleID: ModuleID;
  // when true, show an option to delete alerts on the server (privileged users)
  allowServerDelete?: boolean;
}

export const ModuleView = ({ moduleID, allowServerDelete = false }: ModuleViewProps) => {
  const { 
    state, 
    getModuleData, 
    updateModuleData, 
    getFindingsForModule, 
    runAnalysis, 
    refreshModuleData,
    clearFindings,
    reloadMockData
  } = useData();
  
  // add server delete method
  const { deleteMultipleFindings } = useData();
  
  const [activeTab, setActiveTab] = useState('environment');
  const data = getModuleData(moduleID);
  const findings = getFindingsForModule(moduleID);

  const moduleNames = {
    CMD: 'Command Module',
    LAB: 'Laboratory Module',
    COMM: 'Communication Module',
  };

  const handleSymptomsUpdate = async (symptoms: typeof data.symptoms) => {
    await updateModuleData(moduleID, { symptoms });
  };

  const handleRunAnalysis = async () => {
    try {
      await runAnalysis({
        moduleId: moduleID,
        analysisType: 'environmental',
        data: data.environment
      });
    } catch (error) {
      console.error('Analysis failed:', error);
    }
  };

  const handleRefresh = () => {
    // reload mock module data and clear findings for a true "refresh" reset
    reloadMockData();
  };

  const handleClearFindings = () => {
    clearFindings(moduleID);
  };

  // Auto-refresh data every 30 seconds
  useEffect(() => {
    const interval = setInterval(() => {
      refreshModuleData(moduleID);
    }, 30000);

    return () => clearInterval(interval);
  }, [moduleID, refreshModuleData]);

  return (
    <div className="space-y-6">
      <div className="flex items-start justify-between">
        <div className="border-l-4 border-primary pl-4">
          <div className="flex items-center gap-3">
            <h2 className="text-3xl font-bold text-foreground">{moduleNames[moduleID]}</h2>
            <Badge variant={state.connectionStatus === 'online' ? 'default' : 'destructive'}>
              {state.connectionStatus}
            </Badge>
            {state.isLoading && <Loader2 className="w-5 h-5 animate-spin" />}
          </div>
          <p className="text-muted-foreground">Module ID: {moduleID}</p>
          <p className="text-sm text-muted-foreground">
            Last updated: {new Date(state.lastUpdated).toLocaleTimeString()}
          </p>
        </div>

        <div className="flex gap-2">
          <Button 
            variant="outline" 
            size="sm" 
            onClick={handleRefresh}
            disabled={state.isLoading}
          >
            <RefreshCw className="w-4 h-4 mr-2" />
            Refresh
          </Button>
          <Button 
            variant="default" 
            size="sm" 
            onClick={handleRunAnalysis}
            disabled={state.isLoading}
          >
            Run Analysis
          </Button>
          {findings.length > 0 && (
            <Button 
              variant="destructive" 
              size="sm" 
              onClick={() => handleClearFindings()}
            >
              Clear Alerts ({findings.length})
            </Button>
          )}
        </div>
      </div>

      {findings.length > 0 && (
        <div className="bg-yellow-50 border border-yellow-200 rounded-lg p-4">
          <div className="flex items-center gap-2 mb-2">
            <AlertTriangle className="w-5 h-5 text-yellow-600" />
            <h3 className="font-semibold text-yellow-800">Active Findings</h3>
          </div>
          <div className="space-y-2">
            {findings.slice(0, 3).map((finding) => (
              <div key={finding.id} className="flex items-center justify-between">
                <span className="text-sm text-yellow-700">{finding.description}</span>
                <Badge variant={finding.priority === 'CRITICAL' ? 'destructive' : 'secondary'}>
                  {finding.priority}
                </Badge>
              </div>
            ))}
            {findings.length > 3 && (
              <p className="text-sm text-yellow-600">
                +{findings.length - 3} more findings...
              </p>
            )}
          </div>
        </div>
      )}

      <Tabs value={activeTab} onValueChange={setActiveTab} className="w-full">
        <TabsList className="grid w-full grid-cols-3 bg-secondary">
          <TabsTrigger value="environment">Environment</TabsTrigger>
          <TabsTrigger value="symptoms">Symptoms</TabsTrigger>
          <TabsTrigger value="equipment">Equipment</TabsTrigger>
        </TabsList>

        <TabsContent value="environment" className="mt-6">
          <EnvironmentTab data={data.environment} />
        </TabsContent>

        <TabsContent value="symptoms" className="mt-6">
          <SymptomsTab 
            data={data.symptoms} 
            onUpdate={handleSymptomsUpdate}
            readOnly={false} 
          />
        </TabsContent>

        <TabsContent value="equipment" className="mt-6">
          <EquipmentTab
            ventilation={data.ventilation}
            airFilter={data.airFilter}
            waterRecycling={data.waterRecycling}
          />
        </TabsContent>
      </Tabs>
    </div>
  );
};
