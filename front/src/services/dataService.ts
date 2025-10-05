import { ModuleData, ModuleID } from '@/types/module';
import { Finding, AnalysisResult, ApiResponse, UpdateModuleDataRequest, AnalysisRequest } from '@/types/api';
import { apiClient } from './apiClient';

const STORAGE_KEY = 'astro-vital-data';

interface StoredData {
  modules: Record<ModuleID, ModuleData>;
  findings: Finding[];
  lastSync: number;
}

export class DataService {
  private isOnline: boolean = navigator.onLine;

  constructor() {
    // Clear persisted app data only when a new browser session starts.
    // We use sessionStorage to detect a new session (tab/window). This
    // prevents clearing on refresh while still allowing a fresh start when
    // the user opens a new session.
    try {
      const sessionKey = 'astro-vital-session';
      if (!sessionStorage.getItem(sessionKey)) {
        // New session: remove persisted data so app starts fresh
        localStorage.removeItem(STORAGE_KEY);
        sessionStorage.setItem(sessionKey, Date.now().toString());
        console.info('[DataService] New session - cleared persisted data for fresh start');
      } else {
        console.info('[DataService] Existing session - preserving persisted data');
      }
    } catch (e) {
      console.warn('[DataService] Failed to access storage for session management', e);
    }

    // Listen for online/offline events
    window.addEventListener('online', () => {
      this.isOnline = true;
      this.syncWithBackend();
    });
    window.addEventListener('offline', () => {
      this.isOnline = false;
    });
  }

  // Local Storage Management
  private getStoredData(): StoredData {
    const stored = localStorage.getItem(STORAGE_KEY);
    if (!stored) {
      return {
        modules: this.getInitialModuleData(),
        findings: [],
        lastSync: 0
      };
    }
    return JSON.parse(stored);
  }

  private saveStoredData(data: StoredData): void {
    localStorage.setItem(STORAGE_KEY, JSON.stringify(data));
  }

  private getInitialModuleData(): Record<ModuleID, ModuleData> {
    // Import your existing mock data
    return {
      CMD: {
        environment: {
          o2Level: 20.9,
          co2Level: 0.04,
          coLevel: 0.001,
          moduleID: 'CMD',
          temperature: 22.5,
          humidity: 45,
          pressure: 101.3,
          vocLevel: 0.5,
          pmLevel: 10,
          dewPoint: 10.2,
        },
        symptoms: {
          shortnessOfBreath: false,
          dizziness: false,
          eyeIrritation: false,
          crewMemberID: 'CM-001',
          cough: false,
          headache: false,
          fatigue: false,
        },
        ventilation: {
          degraded: false,
        },
        airFilter: {
          dirty: false,
          efficiency: 98.5,
          moduleID: 'CMD',
        },
        waterRecycling: {
          moduleID: 'CMD',
          degraded: false,
          efficiency: 99.2,
          leakageDetected: false,
        },
      },
      LAB: {
        environment: {
          o2Level: 20.7,
          co2Level: 0.06,
          coLevel: 0.002,
          moduleID: 'LAB',
          temperature: 21.8,
          humidity: 48,
          pressure: 101.1,
          vocLevel: 0.7,
          pmLevel: 15,
          dewPoint: 10.8,
        },
        symptoms: {
          shortnessOfBreath: false,
          dizziness: true,
          eyeIrritation: false,
          crewMemberID: 'CM-002',
          cough: false,
          headache: true,
          fatigue: false,
        },
        ventilation: {
          degraded: true,
        },
        airFilter: {
          dirty: true,
          efficiency: 85.0,
          moduleID: 'LAB',
        },
        waterRecycling: {
          moduleID: 'LAB',
          degraded: false,
          efficiency: 97.5,
          leakageDetected: false,
        },
      },
      COMM: {
        environment: {
          o2Level: 20.8,
          co2Level: 0.05,
          coLevel: 0.001,
          moduleID: 'COMM',
          temperature: 23.0,
          humidity: 42,
          pressure: 101.4,
          vocLevel: 0.4,
          pmLevel: 8,
          dewPoint: 9.5,
        },
        symptoms: {
          shortnessOfBreath: false,
          dizziness: false,
          eyeIrritation: false,
          crewMemberID: 'CM-003',
          cough: false,
          headache: false,
          fatigue: false,
        },
        ventilation: {
          degraded: false,
        },
        airFilter: {
          dirty: false,
          efficiency: 99.0,
          moduleID: 'COMM',
        },
        waterRecycling: {
          moduleID: 'COMM',
          degraded: false,
          efficiency: 98.8,
          leakageDetected: false,
        },
      },
    };
  }

  // Data Retrieval Methods
  getAllModuleData(): Record<ModuleID, ModuleData> {
    return this.getStoredData().modules;
  }

  getModuleData(moduleId: ModuleID): ModuleData {
    const data = this.getStoredData();
    return data.modules[moduleId];
  }

  getAllFindings(): Finding[] {
    return this.getStoredData().findings;
  }

  // Replace local findings with server-provided findings
  setFindings(findings: Finding[]): void {
    const data = this.getStoredData();
    data.findings = findings;
    this.saveStoredData(data);
  }

  getFindingsForModule(moduleId: ModuleID): Finding[] {
    return this.getStoredData().findings.filter(f => f.moduleId === moduleId);
  }

  // Data Update Methods
  updateModuleData(moduleId: ModuleID, updates: Partial<ModuleData>): void {
    const data = this.getStoredData();

    // Ensure module exists
    const existing = data.modules[moduleId] || ({} as ModuleData);

    // Merge helper: if a nested field is an object, merge its keys, otherwise replace
    const merge = (orig: any, upd: any) => {
      if (upd == null) return orig;
      if (typeof upd !== 'object' || Array.isArray(upd)) return upd;
      return { ...orig, ...upd };
    };

    const merged: ModuleData = {
      environment: merge(existing.environment, updates.environment),
      symptoms: merge(existing.symptoms, updates.symptoms),
      ventilation: merge(existing.ventilation, updates.ventilation),
      airFilter: merge(existing.airFilter, updates.airFilter),
      waterRecycling: merge(existing.waterRecycling, updates.waterRecycling),
      // copy any other top-level keys that might exist on ModuleData
      ...(Object.keys(existing).reduce((acc, key) => {
        if (!['environment', 'symptoms', 'ventilation', 'airFilter', 'waterRecycling'].includes(key)) {
          (acc as any)[key] = (existing as any)[key];
        }
        return acc;
      }, {} as Record<string, any>))
    };

    // Apply any other top-level primitive updates that were provided
    for (const k of Object.keys(updates) as (keyof ModuleData)[]) {
      if (k === 'environment' || k === 'symptoms' || k === 'ventilation' || k === 'airFilter' || k === 'waterRecycling') continue;
      const val = updates[k];
      if (val !== undefined) {
        (merged as any)[k] = val as any;
      }
    }

    data.modules[moduleId] = merged;
    this.saveStoredData(data);

    // Attempt to sync with backend if online
    if (this.isOnline) {
      this.syncModuleDataToBackend(moduleId, updates);
    }
  }

  addFinding(finding: Omit<Finding, 'id' | 'timestamp'>): Finding {
    const newFinding: Finding = {
      ...finding,
      id: crypto.randomUUID(),
      timestamp: Date.now()
    };

    const data = this.getStoredData();
    data.findings.push(newFinding);
    this.saveStoredData(data);

    return newFinding;
  }

  resolveFinding(findingId: string): void {
    const data = this.getStoredData();
    const finding = data.findings.find(f => f.id === findingId);
    if (finding) {
      finding.resolved = true;
    }
    this.saveStoredData(data);
  }

  clearFindings(moduleId?: ModuleID): void {
    const data = this.getStoredData();
    if (moduleId) {
      data.findings = data.findings.filter(f => f.moduleId !== moduleId);
    } else {
      data.findings = [];
    }
    this.saveStoredData(data);
  }

  // Backend Communication
  private async syncModuleDataToBackend(moduleId: ModuleID, updates: Partial<ModuleData>): Promise<void> {
    // Syncing module data to backend is disabled because the service
    // does not expose a /modules API. Keeping this as a no-op prevents
    // repeated 404 errors in the console when the frontend attempts to
    // push local module updates. If/when the backend provides module
    // update endpoints, replace this with calls to apiClient.updateModuleData
    // or to more specific endpoints.
    return;
  }

  async runAnalysis(request: AnalysisRequest): Promise<AnalysisResult> {
    try {
      if (this.isOnline) {
        // Try real backend first
        const response = await apiClient.runAnalysis(request);

        if (response.success) {
          // Add findings to local storage
          if (response.data.findings) {
            const data = this.getStoredData();
            data.findings.push(...response.data.findings);
            this.saveStoredData(data);
          }

          return response.data;
        }
      }
    } catch (error) {
      console.warn('Backend unavailable, using simulation:', error);
    }

    // Fallback to simulation
    return this.simulateAnalysis(request);
  }

  private simulateAnalysis(request: AnalysisRequest): AnalysisResult {
    // Simulate analysis based on current data
    const moduleData = this.getModuleData(request.moduleId as ModuleID);
    const findings: Finding[] = [];

    // Simulate some findings based on thresholds
    if (request.analysisType === 'environmental') {
      if (moduleData.environment.o2Level < 19.5) {
        findings.push({
          id: crypto.randomUUID(),
          type: 'Low Oxygen',
          moduleId: request.moduleId,
          description: `Oxygen level critically low: ${moduleData.environment.o2Level}%`,
          priority: 'HIGH',
          timestamp: Date.now()
        });
      }

      if (moduleData.environment.co2Level > 0.08) {
        findings.push({
          id: crypto.randomUUID(),
          type: 'High CO2',
          moduleId: request.moduleId,
          description: `CO2 level too high: ${moduleData.environment.co2Level}%`,
          priority: 'HIGH',
          timestamp: Date.now()
        });
      }

      if (moduleData.environment.temperature > 28 || moduleData.environment.temperature < 18) {
        findings.push({
          id: crypto.randomUUID(),
          type: 'Temperature Alert',
          moduleId: request.moduleId,
          description: `Temperature out of range: ${moduleData.environment.temperature}°C`,
          priority: 'MEDIUM',
          timestamp: Date.now()
        });
      }
    }

    // Add findings to storage
    if (findings.length > 0) {
      const data = this.getStoredData();
      data.findings.push(...findings);
      this.saveStoredData(data);
    }

    return {
      findings,
      timestamp: Date.now(),
      moduleId: request.moduleId,
      analysisType: request.analysisType
    };
  }

  private async syncWithBackend(): Promise<void> {
    try {
      // Check if backend is available
      const isAvailable = await apiClient.isBackendAvailable();
      if (!isAvailable) {
        console.log('Backend not available for sync');
        return;
      }

      // Sync local changes with backend when coming back online
      const data = this.getStoredData();
      
      // Send any unsaved changes
      for (const moduleId of Object.keys(data.modules) as ModuleID[]) {
        await this.syncModuleDataToBackend(moduleId, data.modules[moduleId]);
      }

      // Fetch latest findings from server and replace local findings
      try {
        const serverFindingsResp = await apiClient.fetchAllFindingsFromServer();
        if (serverFindingsResp.success && serverFindingsResp.data) {
          // flatten map into array
          const allFindings: Finding[] = Object.values(serverFindingsResp.data).flat();
          this.setFindings(allFindings);
        }
      } catch (err) {
        console.warn('Failed to fetch findings from server during sync', err);
      }

      data.lastSync = Date.now();
      this.saveStoredData(data);
    } catch (error) {
      console.error('Backend sync failed:', error);
    }
  }

  // Utility Methods
  isBackendAvailable(): boolean {
    return this.isOnline;
  }

  getLastSyncTime(): number {
    return this.getStoredData().lastSync;
  }

  resetData(): void {
    localStorage.removeItem(STORAGE_KEY);
  }

  // Reload only the mock module data and clear findings (used by Refresh)
  reloadMockData(): void {
    const data = this.getStoredData();
    data.modules = this.getInitialModuleData();
    data.findings = [];
    data.lastSync = 0;
    this.saveStoredData(data);
    console.info('[DataService] Reloaded mock module data and cleared findings');
  }
}

export const dataService = new DataService();