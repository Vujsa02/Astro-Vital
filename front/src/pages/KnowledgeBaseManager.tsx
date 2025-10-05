import { Button } from '@/components/ui/button';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { FindingsPanel } from '@/components/FindingsPanel';
import { Badge } from '@/components/ui/badge';
import { ArrowLeft, BookOpen, Activity, AlertTriangle } from 'lucide-react';
import { useNavigate } from 'react-router-dom';
import { useData } from '@/contexts/DataContext';
import { useState, useEffect } from 'react';
import { dataService } from '@/services/dataService';
import { HealthMetricsRequest } from '@/types/api';

export default function KnowledgeBaseManager() {
  const navigate = useNavigate();
  const { checkHealthMetrics, state, updateModuleData, refreshModuleData } = useData();
  const [isRunningHealthCheck, setIsRunningHealthCheck] = useState(false);
  const [thresholds, setThresholds] = useState<any[]>([]);
  const [editingId, setEditingId] = useState<string | null>(null);
  const [newThreshold, setNewThreshold] = useState<any>(null);

  // Always fetch defaults from backend on mount (do not use localStorage)
  useEffect(() => {
    const load = async () => {

      const SESSION_KEY = 'astro-vital-session-id';
      if (!sessionStorage.getItem(SESSION_KEY)) {
        // New session: set a session marker and remove stored thresholds so
        // they will be fetched from the backend
        try {
          sessionStorage.setItem(SESSION_KEY, Date.now().toString());
          // No need to remove thresholds from localStorage
        } catch (e) {
          console.warn('Unable to set session marker or remove thresholds from localStorage', e);
        }
      }

      // if user has locally saved thresholds, use those (this keeps data across reloads)

      try {
        const API_BASE = import.meta.env.VITE_API_URL || 'http://localhost:8080/api';
        const resp = await fetch(`${API_BASE}/api/environmental-templates/default-thresholds`);
        if (!resp.ok) throw new Error(`HTTP ${resp.status}`);
        const body = await resp.json();

        // map module names: COMMAND -> CMD, HABITAT -> COMM
        const mapped = (body || []).map((t: any, idx: number) => ({
          ...t,
          id: t.id || `thr-${Date.now()}-${idx}`,
          moduleId: (t.moduleId === 'COMMAND') ? 'CMD' : (t.moduleId === 'HABITAT' ? 'COMM' : t.moduleId)
        }));

        setThresholds(mapped);
      } catch (err) {
        console.warn('Failed to load default thresholds', err);
        setThresholds([]);
      }
    };

    load();
  }, []);

  const persistThresholds = (list: any[]) => {
    // Keep thresholds only in-memory; do NOT persist to localStorage.
    setThresholds(list);
  };
  const startAddNew = () => {
    setNewThreshold({ parameter: '', operator: '>', threshold: 0, duration: '0s', moduleId: 'CMD', alarmType: '', priority: 'LOW', description: '' });
    setEditingId(null);
  };

  const saveNew = () => {
    if (!newThreshold) return;
    const item = { ...newThreshold, id: `thr-${Date.now()}` };
    persistThresholds([item, ...thresholds]);
    setNewThreshold(null);
  };

  const startEdit = (id: string) => {
    setEditingId(id);
  };

  const saveEdit = (id: string, updated: any) => {
    const list = thresholds.map(t => t.id === id ? { ...t, ...updated } : t);
    persistThresholds(list);
    setEditingId(null);
  };

  const deleteThreshold = (id: string) => {
    const list = thresholds.filter(t => t.id !== id);
    persistThresholds(list);
  };

  // Sample health metrics data
  const sampleHealthData: HealthMetricsRequest = {
    environment: {
      moduleID: "CMD",
      o2Level: 18.8,
      co2Level: 1200,
      temperature: 21.0,
      dewPoint: 10.0
    },
    vitals: {
      spo2: 88
    },
    crewSymptoms: {
      shortnessOfBreath: true,
      dizziness: true,
      eyeIrritation: false
    },
    ventilationStatus: {
      moduleID: "CMD",
      degraded: true
    }
  };

  const handleHealthMetricsCheck = async () => {
    setIsRunningHealthCheck(true);
    try {
      // update local module data so UI and storage reflect the test
      await updateModuleData('CMD', {
        environment: {
          moduleID: 'CMD',
          o2Level: sampleHealthData.environment.o2Level,
          co2Level: sampleHealthData.environment.co2Level,
          temperature: sampleHealthData.environment.temperature,
          dewPoint: sampleHealthData.environment.dewPoint,
          // keep other fields unchanged by not setting them here
        } as any,
        ventilation: {
          degraded: sampleHealthData.ventilationStatus.degraded
        } as any
        ,
        symptoms: {
          ...sampleHealthData.crewSymptoms,
          crewMemberID: 'CM-001'
        } as any
      });
      await checkHealthMetrics(sampleHealthData);
    } catch (error) {
      console.error('Health metrics check failed:', error);
    } finally {
      setIsRunningHealthCheck(false);
    }
  };

  // Equipment sample payload
  const equipmentSample = {
    environment: {
      moduleID: 'CMD',
      temperature: 22.5,
      dewPoint: 12.0,
      o2Level: 20.9,
      co2Level: 1200
    },
    ventilationStatus: {
      moduleID: 'CMD',
      degraded: true
    },
    airFilter: {
      moduleID: 'CMD',
      dirty: true,
      efficiency: 0.35
    }
  };

  // Environmental monitoring sample payload
  const environmentalSample = {
    environments: [
      {
        moduleID: 'CMD',
        temperature: 22.5,
        dewPoint: 12.0,
        o2Level: 20.9,
        co2Level: 1200
      },
      {
        moduleID: 'LAB',
        temperature: 21.0,
        dewPoint: 11.0,
        o2Level: 20.9,
        co2Level: 500
      }
    ],
    condensationDataList: [
      { moduleID: 'CMD', location: 'WATER_LINES', surfaceTemperature: 13.0 },
      { moduleID: 'LAB', location: 'WATER_LINES', surfaceTemperature: 10.5 },
      { moduleID: 'CMD', location: 'WALLS', surfaceTemperature: 9.0 },
      { moduleID: 'LAB', location: 'WALLS', surfaceTemperature: 11.5 },
      { moduleID: 'CMD', location: 'PANELS', surfaceTemperature: 9.0 },
      { moduleID: 'LAB', location: 'PANELS', surfaceTemperature: 13.5 }
    ],
    humidityEvents: [
      { timestamp: 1758644709000, humidity: 40.0, moduleId: 'LAB' },
      { timestamp: 1758644709000, humidity: 40.0, moduleId: 'CMD' },
      { timestamp: 1758666343000, humidity: 85.0, moduleId: 'LAB' },
      { timestamp: 1758666343000, humidity: 55.0, moduleId: 'CMD' }
    ],
    waterRecyclings: [
      { moduleID: 'CMD', degraded: false, efficiency: 0.6 },
      { moduleID: 'LAB', degraded: false, efficiency: 0.95 }
    ],
    ventilationStatuses: [
      { moduleID: 'CMD', degraded: false },
      { moduleID: 'LAB', degraded: true }
    ]
  };

  const handleEquipmentCheck = async () => {
    try {
      // Update module data locally
      await updateModuleData('CMD', {
        environment: equipmentSample.environment as any,
        ventilation: equipmentSample.ventilationStatus as any,
        airFilter: equipmentSample.airFilter as any
      });

      // Send to equipment-maintenance endpoint
      const api = await import('@/services/apiClient').then(m => m.apiClient);
      await api.checkEquipmentMaintenance(equipmentSample);

      // Refresh findings from server and update local store & UI
      try {
        const resp = await api.fetchAllFindingsFromServer();
        if (resp.success && resp.data) {
          const allFindings = Object.values(resp.data).flat();
          // normalize timestamps and descriptions similar to DataContext
          const normalized = allFindings.map((f: any, idx: number) => ({
            ...f,
            id: f.id != null ? f.id : `srv-${Date.now()}-${idx}`,
            description: f.details || f.description || f.type,
            timestamp: typeof f.timestamp === 'string' ? new Date(f.timestamp).getTime() : (f.timestamp as number)
          }));

          dataService.setFindings(normalized as any);
          // Ask the DataContext to refresh state from dataService
          refreshModuleData();
        }
      } catch (err) {
        console.warn('Failed to refresh findings after equipment check', err);
      }
    } catch (err) {
      console.error('Equipment check failed', err);
    }
  };

  // Get findings for CMD module (since that's what we're testing with)
  const cmdFindings = state.findings.filter(finding => 
    finding.moduleId === 'CMD' && !finding.resolved
  );

  const labFindings = state.findings.filter(finding =>
    finding.moduleId === 'LAB' && !finding.resolved
  );

  const commFindings = state.findings.filter(finding =>
    (finding.moduleId === 'COMM' || finding.moduleId === 'COM') && !finding.resolved
  );

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
        <div className="max-w-6xl mx-auto space-y-8">
          {/* Environmental Thresholds Editor */}
          <div className="space-y-6">
            <Card>
              <CardHeader>
                <div className="flex items-center justify-between">
                  <div>
                    <CardTitle className="text-2xl font-bold flex items-center gap-2">
                      <AlertTriangle className="w-6 h-6 text-amber-500" />
                      Environmental Thresholds
                    </CardTitle>
                    <p className="text-muted-foreground mt-2">
                      Manage environmental monitoring thresholds for all modules. 
                      <br />
                      <span className="text-xs">Fetched from server • Module mapping: COMMAND→CMD, HABITAT→COMM</span>
                    </p>
                  </div>
                  <Button onClick={startAddNew} size="sm" className="bg-green-600 text-gray-100 hover:bg-green-700">
                    Add New Threshold
                  </Button>
                </div>
              </CardHeader>
              <CardContent>

                <div className="grid grid-cols-1 lg:grid-cols-2 gap-4">
                {newThreshold && (
                  <div className="lg:col-span-2 p-4 bg-gray-900 border border-gray-600 rounded-lg">
                    <h3 className="font-semibold text-white mb-3">Add New Threshold</h3>
                    <div className="grid grid-cols-1 md:grid-cols-3 gap-3">
                      <div>
                        <label className="block text-sm font-medium mb-1 text-white">Parameter</label>
                        <input 
                          className="w-full p-2 border border-gray-600 rounded-md bg-black text-white" 
                          placeholder="e.g. temperature, o2Level" 
                          value={newThreshold.parameter} 
                          onChange={(e) => setNewThreshold({...newThreshold, parameter: e.target.value})} 
                        />
                      </div>
                      <div>
                        <label className="block text-sm font-medium mb-1 text-white">Operator</label>
                        <select 
                          className="w-full p-2 border border-gray-600 rounded-md bg-black text-white" 
                          value={newThreshold.operator} 
                          onChange={(e) => setNewThreshold({...newThreshold, operator: e.target.value})}
                        >
                          <option value=">">Greater than (&gt;)</option>
                          <option value="<">Less than (&lt;)</option>
                          <option value=">=">Greater or equal (&gt;=)</option>
                          <option value="<=">Less or equal (&lt;=)</option>
                        </select>
                      </div>
                      <div>
                        <label className="block text-sm font-medium mb-1 text-white">Threshold Value</label>
                        <input 
                          className="w-full p-2 border border-gray-600 rounded-md bg-black text-white" 
                          placeholder="threshold value" 
                          type="number" 
                          step="0.1"
                          value={newThreshold.threshold} 
                          onChange={(e) => setNewThreshold({...newThreshold, threshold: parseFloat(e.target.value) || 0})} 
                        />
                      </div>
                    </div>
                    <div className="mt-3 grid grid-cols-1 md:grid-cols-3 gap-3">
                      <div>
                        <label className="block text-sm font-medium mb-1 text-white">Duration</label>
                        <input 
                          className="w-full p-2 border border-gray-600 rounded-md bg-black text-white" 
                          placeholder="e.g. 30s, 5m" 
                          value={newThreshold.duration} 
                          onChange={(e) => setNewThreshold({...newThreshold, duration: e.target.value})} 
                        />
                      </div>
                      <div>
                        <label className="block text-sm font-medium mb-1 text-white">Module ID</label>
                        <select 
                          className="w-full p-2 border border-gray-600 rounded-md bg-black text-white" 
                          value={newThreshold.moduleId} 
                          onChange={(e) => setNewThreshold({...newThreshold, moduleId: e.target.value})}
                        >
                          <option value="CMD">CMD (Command)</option>
                          <option value="LAB">LAB (Laboratory)</option>
                          <option value="COMM">COMM (Communications)</option>
                        </select>
                      </div>
                      <div>
                        <label className="block text-sm font-medium mb-1 text-white">Alarm Type</label>
                        <input 
                          className="w-full p-2 border border-gray-600 rounded-md bg-black text-white" 
                          placeholder="e.g. HIGH_TEMP, LOW_O2" 
                          value={newThreshold.alarmType} 
                          onChange={(e) => setNewThreshold({...newThreshold, alarmType: e.target.value})} 
                        />
                      </div>
                    </div>
                    <div className="mt-3">
                      <label className="block text-sm font-medium mb-1 text-white">Description</label>
                      <input 
                        className="w-full p-2 border border-gray-600 rounded-md bg-black text-white" 
                        placeholder="Brief description of this threshold" 
                        value={newThreshold.description || ''} 
                        onChange={(e) => setNewThreshold({...newThreshold, description: e.target.value})} 
                      />
                    </div>
                    <div className="mt-4 flex gap-2">
                      <Button size="sm" onClick={saveNew} className="bg-green-600 text-gray-100 hover:bg-green-700">Save Threshold</Button>
                      <Button size="sm" variant="outline" onClick={() => setNewThreshold(null)}>Cancel</Button>
                    </div>
                  </div>
                )}

                {thresholds.map((t) => {
                  if (editingId === t.id) {
                    // Edit mode
                    return (
                      <div key={t.id} className="lg:col-span-2 p-4 bg-black border border-gray-600 rounded-lg">
                        <h3 className="font-semibold text-white mb-3">Editing Threshold</h3>
                        <div className="grid grid-cols-1 md:grid-cols-3 gap-3">
                          <div>
                            <label className="block text-sm font-medium mb-1 text-white">Parameter</label>
                            <input 
                              className="w-full p-2 border border-gray-600 rounded-md bg-black text-white" 
                              defaultValue={t.parameter}
                              id={`param-${t.id}`}
                            />
                          </div>
                          <div>
                            <label className="block text-sm font-medium mb-1 text-white">Operator</label>
                            <select 
                              className="w-full p-2 border border-gray-600 rounded-md bg-black text-white" 
                              defaultValue={t.operator}
                              id={`op-${t.id}`}
                            >
                              <option value=">">Greater than (&gt;)</option>
                              <option value="<">Less than (&lt;)</option>
                              <option value=">=">Greater or equal (&gt;=)</option>
                              <option value="<=">Less or equal (&lt;=)</option>
                            </select>
                          </div>
                          <div>
                            <label className="block text-sm font-medium mb-1 text-white">Threshold Value</label>
                            <input 
                              className="w-full p-2 border border-gray-600 rounded-md bg-black text-white" 
                              type="number" 
                              step="0.1"
                              defaultValue={t.threshold}
                              id={`thresh-${t.id}`}
                            />
                          </div>
                        </div>
                        <div className="mt-3 grid grid-cols-1 md:grid-cols-3 gap-3">
                          <div>
                            <label className="block text-sm font-medium mb-1 text-white">Duration</label>
                            <input 
                              className="w-full p-2 border border-gray-600 rounded-md bg-black text-white" 
                              defaultValue={t.duration}
                              id={`dur-${t.id}`}
                            />
                          </div>
                          <div>
                            <label className="block text-sm font-medium mb-1 text-white">Module ID</label>
                            <select 
                              className="w-full p-2 border border-gray-600 rounded-md bg-black text-white" 
                              defaultValue={t.moduleId}
                              id={`mod-${t.id}`}
                            >
                              <option value="CMD">CMD (Command)</option>
                              <option value="LAB">LAB (Laboratory)</option>
                              <option value="COMM">COMM (Communications)</option>
                            </select>
                          </div>
                          <div>
                            <label className="block text-sm font-medium mb-1 text-white">Alarm Type</label>
                            <input 
                              className="w-full p-2 border border-gray-600 rounded-md bg-black text-white" 
                              defaultValue={t.alarmType}
                              id={`alarm-${t.id}`}
                            />
                          </div>
                        </div>
                        <div className="mt-3">
                          <label className="block text-sm font-medium mb-1 text-white">Description</label>
                          <input 
                            className="w-full p-2 border border-gray-600 rounded-md bg-black text-white" 
                            defaultValue={t.description || ''}
                            id={`desc-${t.id}`}
                          />
                        </div>
                        <div className="mt-4 flex gap-2">
                          <Button 
                            size="sm" 
                            onClick={() => {
                              const parameter = (document.getElementById(`param-${t.id}`) as HTMLInputElement)?.value;
                              const operator = (document.getElementById(`op-${t.id}`) as HTMLSelectElement)?.value;
                              const threshold = parseFloat((document.getElementById(`thresh-${t.id}`) as HTMLInputElement)?.value) || 0;
                              const duration = (document.getElementById(`dur-${t.id}`) as HTMLInputElement)?.value;
                              const moduleId = (document.getElementById(`mod-${t.id}`) as HTMLSelectElement)?.value;
                              const alarmType = (document.getElementById(`alarm-${t.id}`) as HTMLInputElement)?.value;
                              const description = (document.getElementById(`desc-${t.id}`) as HTMLInputElement)?.value;
                              saveEdit(t.id, { parameter, operator, threshold, duration, moduleId, alarmType, description });
                            }}
                            className="bg-green-600 hover:bg-green-700"
                          >
                            Save Changes
                          </Button>
                          <Button size="sm" variant="outline" onClick={() => setEditingId(null)}>Cancel</Button>
                        </div>
                      </div>
                    );
                  }

                  // View mode
                  return (
                    <div key={t.id} className="p-3 bg-card border border-border rounded-lg shadow-sm hover:shadow-md transition-shadow">
                      <div className="flex items-start justify-between">
                        <div className="flex-1">
                          <div className="flex items-center gap-2 mb-2">
                            <Badge variant="outline" className="text-xs font-mono">{t.moduleId}</Badge>
                            <h4 className="font-semibold text-base">{t.alarmType || 'Unnamed Threshold'}</h4>
                          </div>
                          
                          <div className="space-y-2 text-sm">
                            <div>
                              <span className="font-medium text-muted-foreground">Condition:</span>
                              <div className="font-mono bg-muted/50 px-2 py-0.5 rounded mt-0.5 w-[90%]">
                                {t.parameter} {t.operator} {t.threshold}
                              </div>
                            </div>
                            <div>
                              <span className="font-medium text-muted-foreground">Duration:</span>
                              <div className="font-mono bg-muted/50 px-2 py-0.5 rounded mt-0.5 w-[90%]">
                                {t.duration}
                              </div>
                            </div>
                          </div>
                          
                          {t.description && (
                            <div className="mt-2">
                              <span className="font-medium text-muted-foreground text-sm">Description:</span>
                              <p className="text-sm text-muted-foreground mt-1">{t.description}</p>
                            </div>
                          )}
                        </div>
                        
                        <div className="flex flex-col gap-1 ml-2">
                          <Button size="sm" variant="outline" onClick={() => startEdit(t.id)}>Edit</Button>
                          <Button size="sm" variant="destructive" onClick={() => deleteThreshold(t.id)}>Delete</Button>
                        </div>
                      </div>
                    </div>
                  );
                })}
                </div>
              </CardContent>
            </Card>
          </div>

          {/* Health Metrics Testing Section */}
          <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
            <Card>
              <CardHeader>
                <CardTitle className="flex items-center gap-2">
                  <Activity className="w-5 h-5 text-blue-500" />
                  Send Test Data
                </CardTitle>
              </CardHeader>
              <CardContent className="space-y-4">
                <p className="text-muted-foreground">
                  Send sample datasets to the analysis endpoints (health, equipment, environmental, air quality) and collect findings into the findings panel.
                </p>

                <Button 
                  onClick={handleHealthMetricsCheck}
                  disabled={isRunningHealthCheck || state.isLoading}
                  className="w-full"
                  size="lg"
                >
                  {isRunningHealthCheck ? 'Sending...' : 'Send Health Data'}
                </Button>

                <Button
                  onClick={handleEquipmentCheck}
                  disabled={state.isLoading}
                  className="w-full mt-2"
                  size="lg"
                  variant="secondary"
                >
                  Send Equipment Data
                </Button>

                <Button
                  onClick={async () => {
                    try {
                      // Update modules locally for consistency
                      await updateModuleData('CMD', { environment: environmentalSample.environments[0] as any });
                      await updateModuleData('LAB', { environment: environmentalSample.environments[1] as any });

                      // POST to environmental-monitoring/process
                      const API_BASE = import.meta.env.VITE_API_URL || 'http://localhost:8080/api';
                      const resp = await fetch(`${API_BASE}/environmental-monitoring/process`, {
                        method: 'POST',
                        headers: { 'Content-Type': 'application/json' },
                        body: JSON.stringify(environmentalSample)
                      });

                      if (!resp.ok) throw new Error(`HTTP ${resp.status}`);

                      const body = await resp.json();

                      // Extract findings and store
                      if (body && Array.isArray(body.findings)) {
                        const normalized = body.findings.map((f: any, idx: number) => ({
                          ...f,
                          id: f.id != null ? f.id : `srv-env-${Date.now()}-${idx}`,
                          description: f.details || f.description || f.type,
                          timestamp: typeof f.timestamp === 'string' ? new Date(f.timestamp).getTime() : (f.timestamp as number)
                        }));

                        dataService.setFindings(normalized as any);
                        refreshModuleData();
                      }
                    } catch (err) {
                      console.error('Environmental process failed', err);
                    }
                  }}
                  disabled={state.isLoading}
                  className="w-full mt-2"
                  size="lg"
                >
                  Run Environmental Process
                </Button>

                <Button
                  onClick={async () => {
                    try {
                      // compute timestamps
                      const now = Date.now();
                      const oneHourAgo = now - (60 * 60 * 1000);
                      const twoHoursAgo = now - (2 * 60 * 60 * 1000);
                      const threeHoursAgo = now - (3 * 60 * 60 * 1000);

                      // build payload
                      const airQualityPayload = {
                        environments: [
                          {
                            moduleID: 'LAB',
                            temperature: 21.0,
                            co2Level: 400.0,
                            coLevel: 0.5,
                            o2Level: 20.8,
                            humidity: 65.0,
                            pressure: 1013.25,
                            vocLevel: 30.0,
                            pmLevel: 20.0,
                            dewPoint: 15.0
                          }
                        ],
                        airQualityEvents: [
                          { vocLevel: 55.0, pmLevel: 20.0, moduleId: 'LAB', timestamp: threeHoursAgo },
                          { vocLevel: 30.0, pmLevel: 40.0, moduleId: 'LAB', timestamp: twoHoursAgo },
                          { vocLevel: 60.0, pmLevel: 45.0, moduleId: 'LAB', timestamp: oneHourAgo },
                          { vocLevel: 25.0, pmLevel: 15.0, moduleId: 'CMD', timestamp: now }
                        ]
                      };

                      // update LAB module locally
                      await updateModuleData('LAB', { environment: airQualityPayload.environments[0] as any });

                      // POST to air-quality-monitoring/process
                      const API_BASE = import.meta.env.VITE_API_URL || 'http://localhost:8080/api';
                      const root = API_BASE.replace(/\/api\/?$/, '');
                      const resp = await fetch(`${root}/air-quality-monitoring/process`, {
                        method: 'POST',
                        headers: { 'Content-Type': 'application/json' },
                        body: JSON.stringify(airQualityPayload)
                      });

                      if (!resp.ok) throw new Error(`HTTP ${resp.status}`);

                      const body = await resp.json();

                      if (body && Array.isArray(body.findings)) {
                        const normalized = body.findings.map((f: any, idx: number) => ({
                          ...f,
                          id: f.id != null ? f.id : `srv-air-${Date.now()}-${idx}`,
                          description: f.details || f.description || f.type,
                          timestamp: typeof f.timestamp === 'string' ? new Date(f.timestamp).getTime() : (f.timestamp as number)
                        }));

                        dataService.setFindings(normalized as any);
                        refreshModuleData();
                      }
                    } catch (err) {
                      console.error('Air quality process failed', err);
                    }
                  }}
                  disabled={state.isLoading}
                  className="w-full mt-2"
                  size="lg"
                  variant="secondary"
                >
                  Run Air Quality Process
                </Button>

                <Button
                  onClick={async () => {
                    try {
                      // Default template payload
                      const defaultTemplatePayload = [
                        {
                          moduleID: "LAB",
                          o2Level: 18.0,
                          co2Level: 1200,
                          coLevel: 5.0,
                          temperature: 29.0,
                          humidity: 45.0,
                          pressure: 94.0,
                          vocLevel: 25.0,
                          pmLevel: 15.0,
                          dewPoint: 10.0
                        },
                        {
                          moduleID: "CMD",
                          o2Level: 20.5,
                          co2Level: 700,
                          coLevel: 15.0,
                          temperature: 25.0,
                          humidity: 50.0,
                          pressure: 101.0,
                          vocLevel: 75.0,
                          pmLevel: 20.0,
                          dewPoint: 12.0
                        },
                        {
                          moduleID: "COMM",
                          o2Level: 19.8,
                          co2Level: 800,
                          coLevel: 8.0,
                          temperature: 26.0,
                          humidity: 48.0,
                          pressure: 98.0,
                          vocLevel: 35.0,
                          pmLevel: 18.0,
                          dewPoint: 11.0
                        }
                      ];

                      // Update modules locally
                      await updateModuleData('LAB', { environment: defaultTemplatePayload[0] as any });
                      await updateModuleData('CMD', { environment: defaultTemplatePayload[1] as any });
                      await updateModuleData('COMM', { environment: defaultTemplatePayload[2] as any });

                      // POST to evaluate-default endpoint
                      const API_BASE = import.meta.env.VITE_API_URL || 'http://localhost:8080/api';
                      const resp = await fetch(`${API_BASE}/api/environmental-templates/evaluate-default`, {
                        method: 'POST',
                        headers: { 'Content-Type': 'application/json' },
                        body: JSON.stringify(defaultTemplatePayload)
                      });

                      if (!resp.ok) throw new Error(`HTTP ${resp.status}`);

                      const body = await resp.json();

                      // After the server-side evaluation, fetch authoritative findings
                      // directly from the findings API and update the local store.
                      try {
                        const api = await import('@/services/apiClient').then(m => m.apiClient);
                        const respAll = await api.fetchAllFindingsFromServer();
                        if (respAll.success && respAll.data) {
                          const allFindings = Object.values(respAll.data).flat();
                          const normalizedSrv = allFindings.map((f: any, idx: number) => ({
                            ...f,
                            id: f.id != null ? f.id : `srv-${Date.now()}-${idx}`,
                            description: f.details || f.description || f.type,
                            timestamp: typeof f.timestamp === 'string' ? new Date(f.timestamp).getTime() : (f.timestamp as number)
                          }));
                          dataService.setFindings(normalizedSrv as any);
                          refreshModuleData();
                        } else {
                          // If server didn't return data, clear or keep existing findings
                          dataService.setFindings([] as any);
                          refreshModuleData();
                        }
                      } catch (err) {
                        console.warn('Failed to fetch authoritative findings after default evaluation', err);
                        // Fallback: use findings returned by the evaluation call if present
                        if (body && Array.isArray(body.findings)) {
                          const normalized = body.findings.map((f: any, idx: number) => ({
                            ...f,
                            id: f.id != null ? f.id : `srv-default-${Date.now()}-${idx}`,
                            description: f.details || f.description || f.type,
                            timestamp: typeof f.timestamp === 'string' ? new Date(f.timestamp).getTime() : (f.timestamp as number)
                          }));
                          dataService.setFindings(normalized as any);
                          refreshModuleData();
                        }
                      }
                    } catch (err) {
                      console.error('Default template evaluation failed', err);
                    }
                  }}
                  disabled={state.isLoading}
                  className="w-full mt-2"
                  size="lg"
                  variant="outline"
                >
                  Evaluate Default Templates
                </Button>

                <Button
                  onClick={async () => {
                    try {
                      // Custom template payload with saved thresholds
                      const customTemplatePayload = {
                        environments: [
                          {
                            moduleID: "LAB",
                            o2Level: 18.0,
                            co2Level: 1200,
                            coLevel: 5.0,
                            temperature: 29.0,
                            humidity: 45.0,
                            pressure: 94.0,
                            vocLevel: 25.0,
                            pmLevel: 15.0,
                            dewPoint: 10.0
                          },
                          {
                            moduleID: "CMD",
                            o2Level: 20.5,
                            co2Level: 700,
                            coLevel: 15.0,
                            temperature: 25.0,
                            humidity: 50.0,
                            pressure: 101.0,
                            vocLevel: 75.0,
                            pmLevel: 20.0,
                            dewPoint: 12.0
                          },
                          {
                            moduleID: "COMM",
                            o2Level: 19.8,
                            co2Level: 800,
                            coLevel: 8.0,
                            temperature: 26.0,
                            humidity: 48.0,
                            pressure: 98.0,
                            vocLevel: 35.0,
                            pmLevel: 18.0,
                            dewPoint: 11.0
                          }
                        ],
                        thresholds: thresholds.map(t => ({
                          parameter: t.parameter,
                          operator: t.operator,
                          threshold: t.threshold,
                          duration: t.duration,
                          moduleId: t.moduleId,
                          alarmType: t.alarmType,
                          priority: t.priority || 'MEDIUM',
                          description: t.description || ''
                        }))
                      };

                      // Update modules locally
                      await updateModuleData('LAB', { environment: customTemplatePayload.environments[0] as any });
                      await updateModuleData('CMD', { environment: customTemplatePayload.environments[1] as any });
                      await updateModuleData('COMM', { environment: customTemplatePayload.environments[2] as any });

                      // POST to evaluate-custom endpoint
                      const API_BASE = import.meta.env.VITE_API_URL || 'http://localhost:8080/api';
                      const resp = await fetch(`${API_BASE}/api/environmental-templates/evaluate-custom`, {
                        method: 'POST',
                        headers: { 'Content-Type': 'application/json' },
                        body: JSON.stringify(customTemplatePayload)
                      });

                      if (!resp.ok) throw new Error(`HTTP ${resp.status}`);

                      const body = await resp.json();

                      // After the server-side evaluation, fetch authoritative findings
                      // directly from the findings API and update the local store.
                      try {
                        const api = await import('@/services/apiClient').then(m => m.apiClient);
                        const respAll = await api.fetchAllFindingsFromServer();
                        if (respAll.success && respAll.data) {
                          const allFindings = Object.values(respAll.data).flat();
                          const normalizedSrv = allFindings.map((f: any, idx: number) => ({
                            ...f,
                            id: f.id != null ? f.id : `srv-${Date.now()}-${idx}`,
                            description: f.details || f.description || f.type,
                            timestamp: typeof f.timestamp === 'string' ? new Date(f.timestamp).getTime() : (f.timestamp as number)
                          }));
                          dataService.setFindings(normalizedSrv as any);
                          refreshModuleData();
                        } else {
                          dataService.setFindings([] as any);
                          refreshModuleData();
                        }
                      } catch (err) {
                        console.warn('Failed to fetch authoritative findings after custom evaluation', err);
                        // Fallback: use findings returned by the evaluation call if present
                        if (body && Array.isArray(body.findings)) {
                          const normalized = body.findings.map((f: any, idx: number) => ({
                            ...f,
                            id: f.id != null ? f.id : `srv-custom-${Date.now()}-${idx}`,
                            description: f.details || f.description || f.type,
                            timestamp: typeof f.timestamp === 'string' ? new Date(f.timestamp).getTime() : (f.timestamp as number)
                          }));
                          dataService.setFindings(normalized as any);
                          refreshModuleData();
                        }
                      }
                    } catch (err) {
                      console.error('Custom template evaluation failed', err);
                    }
                  }}
                  disabled={state.isLoading || thresholds.length === 0}
                  className="w-full mt-2"
                  size="lg"
                  variant="outline"
                >
                  Evaluate Custom Templates ({thresholds.length} thresholds)
                </Button>

                {cmdFindings.length > 0 && (
                  <div className="mt-4">
                    <div className="flex items-center gap-2 mb-2">
                      <AlertTriangle className="w-4 h-4 text-orange-500" />
                      <span className="text-sm font-medium">Latest Results</span>
                    </div>
                    <div className="text-sm text-muted-foreground">
                      {cmdFindings.length} finding(s) detected in CMD module
                    </div>
                  </div>
                )}
              </CardContent>
            </Card>

            {/* Findings Display */}
            <div>
              <FindingsPanel moduleId="CMD" maxHeight="300px" />
              <FindingsPanel moduleId="LAB" maxHeight="300px" />
              <FindingsPanel moduleId="COMM" maxHeight="300px" />

            </div>
          </div>
        </div>
      </main>
    </div>
  );
}
