import React, { createContext, useContext, useReducer, useCallback, useEffect } from 'react';
import { ModuleData, ModuleID } from '@/types/module';
import { Finding, AnalysisResult, AnalysisRequest, HealthMetricsRequest } from '@/types/api';
import { dataService } from '@/services/dataService';
import { useToast } from '@/hooks/use-toast';
import { apiClient } from '@/services/apiClient';

interface DataState {
  modules: Record<ModuleID, ModuleData>;
  findings: Finding[];
  isLoading: boolean;
  lastUpdated: number;
  connectionStatus: 'online' | 'offline';
}

type DataAction =
  | { type: 'SET_LOADING'; payload: boolean }
  | { type: 'SET_MODULE_DATA'; payload: { moduleId: ModuleID; data: ModuleData } }
  | { type: 'UPDATE_MODULE_DATA'; payload: { moduleId: ModuleID; updates: Partial<ModuleData> } }
  | { type: 'SET_ALL_MODULES'; payload: Record<ModuleID, ModuleData> }
  | { type: 'SET_FINDINGS'; payload: Finding[] }
  | { type: 'ADD_FINDING'; payload: Finding }
  | { type: 'RESOLVE_FINDING'; payload: string }
  | { type: 'CLEAR_FINDINGS'; payload?: ModuleID }
  | { type: 'SET_CONNECTION_STATUS'; payload: 'online' | 'offline' }
  | { type: 'REFRESH_DATA' };

const initialState: DataState = {
  modules: dataService.getAllModuleData(),
  findings: dataService.getAllFindings(),
  isLoading: false,
  lastUpdated: Date.now(),
  connectionStatus: navigator.onLine ? 'online' : 'offline'
};

function dataReducer(state: DataState, action: DataAction): DataState {
  switch (action.type) {
    case 'SET_LOADING':
      return { ...state, isLoading: action.payload };
    
    case 'SET_MODULE_DATA':
      return {
        ...state,
        modules: {
          ...state.modules,
          [action.payload.moduleId]: action.payload.data
        },
        lastUpdated: Date.now()
      };
    
    case 'UPDATE_MODULE_DATA':
      // perform a safe merge at UI level as well so nested fields are preserved
      const existing = state.modules[action.payload.moduleId] || ({} as ModuleData);
      const merge = (orig: any, upd: any) => {
        if (upd == null) return orig;
        if (typeof upd !== 'object' || Array.isArray(upd)) return upd;
        return { ...orig, ...upd };
      };

      const merged: ModuleData = {
        environment: merge(existing.environment, action.payload.updates.environment),
        symptoms: merge(existing.symptoms, action.payload.updates.symptoms),
        ventilation: merge(existing.ventilation, action.payload.updates.ventilation),
        airFilter: merge(existing.airFilter, action.payload.updates.airFilter),
        waterRecycling: merge(existing.waterRecycling, action.payload.updates.waterRecycling),
        ...(Object.keys(existing).reduce((acc, key) => {
          if (!['environment', 'symptoms', 'ventilation', 'airFilter', 'waterRecycling'].includes(key)) {
            (acc as any)[key] = (existing as any)[key];
          }
          return acc;
        }, {} as Record<string, any>))
      };

      for (const k of Object.keys(action.payload.updates) as (keyof ModuleData)[]) {
        if (['environment', 'symptoms', 'ventilation', 'airFilter', 'waterRecycling'].includes(k as string)) continue;
        const val = action.payload.updates[k];
        if (val !== undefined) {
          (merged as any)[k] = val as any;
        }
      }

      return {
        ...state,
        modules: {
          ...state.modules,
          [action.payload.moduleId]: merged
        },
        lastUpdated: Date.now()
      };
    
    case 'SET_ALL_MODULES':
      return {
        ...state,
        modules: action.payload,
        lastUpdated: Date.now()
      };
    
    case 'SET_FINDINGS':
      return { ...state, findings: action.payload };
    
    case 'ADD_FINDING':
      return {
        ...state,
        findings: [...state.findings, action.payload]
      };
    
    case 'RESOLVE_FINDING':
      return {
        ...state,
        findings: state.findings.map(f =>
          f.id === action.payload ? { ...f, resolved: true } : f
        )
      };
    
    case 'CLEAR_FINDINGS':
      return {
        ...state,
        findings: action.payload
          ? state.findings.filter(f => f.moduleId !== action.payload)
          : []
      };
    
    case 'SET_CONNECTION_STATUS':
      return { ...state, connectionStatus: action.payload };
    
    case 'REFRESH_DATA':
      return {
        ...state,
        modules: dataService.getAllModuleData(),
        findings: dataService.getAllFindings(),
        lastUpdated: Date.now()
      };
    
    default:
      return state;
  }
}

interface DataContextType {
  // State
  state: DataState;
  
  // Module data methods
  getModuleData: (moduleId: ModuleID) => ModuleData;
  updateModuleData: (moduleId: ModuleID, updates: Partial<ModuleData>) => Promise<void>;
  refreshModuleData: (moduleId?: ModuleID) => void;
  
  // Findings methods
  getFindingsForModule: (moduleId: ModuleID) => Finding[];
  resolveFinding: (findingId: string) => void;
  clearFindings: (moduleId?: ModuleID) => Promise<void>;
  deleteFinding: (type: string, moduleId: string) => Promise<void>;
  deleteMultipleFindings: (identifiers: Array<{ type: string; moduleId: string }>) => Promise<void>;
  
  // Analysis methods
  runAnalysis: (request: AnalysisRequest) => Promise<AnalysisResult>;
  checkHealthMetrics: (healthData: HealthMetricsRequest) => Promise<Finding[]>;
  
  // Utility methods
  isOnline: () => boolean;
  getLastSyncTime: () => number;
  resetData: () => void;
  reloadMockData: () => void;
}

const DataContext = createContext<DataContextType | undefined>(undefined);

export const useData = () => {
  const context = useContext(DataContext);
  if (!context) {
    throw new Error('useData must be used within a DataProvider');
  }
  return context;
};

interface DataProviderProps {
  children: React.ReactNode;
}

export const DataProvider: React.FC<DataProviderProps> = ({ children }) => {
  const [state, dispatch] = useReducer(dataReducer, initialState);
  const { toast } = useToast();

  // Listen for online/offline events
  useEffect(() => {
    const handleOnline = () => {
      dispatch({ type: 'SET_CONNECTION_STATUS', payload: 'online' });
      toast({
        title: 'Back Online',
        description: 'Syncing data with server...',
      });
    };

    const handleOffline = () => {
      dispatch({ type: 'SET_CONNECTION_STATUS', payload: 'offline' });
      toast({
        title: 'Offline Mode',
        description: 'Changes will be saved locally and synced when reconnected.',
        variant: 'destructive'
      });
    };

    window.addEventListener('online', handleOnline);
    window.addEventListener('offline', handleOffline);

    return () => {
      window.removeEventListener('online', handleOnline);
      window.removeEventListener('offline', handleOffline);
    };
  }, [toast]);

  // On mount, try to load findings from server if backend is available
  useEffect(() => {
    let mounted = true;
    const loadFindings = async () => {
      try {
        const resp = await apiClient.fetchAllFindingsFromServer();
        if (!mounted) return;
        if (resp.success && resp.data) {
          // flatten map into findings array
          const allFindings: Finding[] = Object.values(resp.data).flat();
          // normalize timestamps if necessary
          const normalized = allFindings.map((f, idx) => ({
            ...f,
            id: f.id != null ? f.id : `srv-${Date.now()}-${idx}`,
            description: (f as any).details || (f as any).description || f.type,
            timestamp: typeof f.timestamp === 'string' ? new Date(f.timestamp).getTime() : (f.timestamp as number)
          }));

          dataService.setFindings(normalized);
          dispatch({ type: 'SET_FINDINGS', payload: normalized });
        }
      } catch (err) {
        console.warn('Failed to load findings from server on mount', err);
      }
    };

    loadFindings();

    return () => { mounted = false; };
  }, []);

  // Module data methods
  const getModuleData = useCallback((moduleId: ModuleID): ModuleData => {
    return state.modules[moduleId];
  }, [state.modules]);

  const updateModuleData = useCallback(async (moduleId: ModuleID, updates: Partial<ModuleData>) => {
    dispatch({ type: 'SET_LOADING', payload: true });
    
    try {
      // Update local state immediately for responsiveness
      dispatch({ type: 'UPDATE_MODULE_DATA', payload: { moduleId, updates } });
      
      // Update in data service (handles persistence and backend sync)
      dataService.updateModuleData(moduleId, updates);
      
      toast({
        title: 'Data Updated',
        description: `Module ${moduleId} data has been updated successfully.`,
      });
    } catch (error) {
      console.error('Failed to update module data:', error);
      toast({
        title: 'Update Failed',
        description: 'Failed to update module data. Please try again.',
        variant: 'destructive'
      });
      
      // Refresh data to ensure consistency
      dispatch({ type: 'REFRESH_DATA' });
    } finally {
      dispatch({ type: 'SET_LOADING', payload: false });
    }
  }, [toast]);

  const refreshModuleData = useCallback((moduleId?: ModuleID) => {
    if (moduleId) {
      const data = dataService.getModuleData(moduleId);
      dispatch({ type: 'SET_MODULE_DATA', payload: { moduleId, data } });
    } else {
      dispatch({ type: 'REFRESH_DATA' });
    }
  }, []);

  // Findings methods
  const getFindingsForModule = useCallback((moduleId: ModuleID): Finding[] => {
    return state.findings.filter(f => f.moduleId === moduleId && !f.resolved);
  }, [state.findings]);

  const resolveFinding = useCallback(async (findingId: string) => {
    // Find the finding to get its type and moduleId
    const finding = state.findings.find(f => f.id === findingId);
    if (!finding) {
      toast({ title: 'Not Found', description: 'Finding not found locally.', variant: 'destructive' });
      return;
    }

    try {
      dispatch({ type: 'SET_LOADING', payload: true });
      const resp = await apiClient.deleteFinding(finding.type, finding.moduleId);
      if (resp.success) {
        // remove from local state
        dispatch({ type: 'SET_FINDINGS', payload: state.findings.filter(f => f.id !== findingId) });
        dataService.setFindings(state.findings.filter(f => f.id !== findingId));

        toast({ title: 'Finding Deleted', description: 'The finding was deleted from server.' });
      } else {
        throw new Error(resp.data?.message || resp.message || 'Failed to delete finding on server');
      }
    } catch (error) {
      console.error('Failed to delete finding:', error);
      toast({ title: 'Delete Failed', description: 'Failed to delete the finding on server.', variant: 'destructive' });
    } finally {
      dispatch({ type: 'SET_LOADING', payload: false });
    }
  }, [state.findings, toast]);

  const clearFindings = useCallback(async (moduleId?: ModuleID) => {
    try {
      dispatch({ type: 'SET_LOADING', payload: true });
      if (moduleId) {
        const resp = await apiClient.clearModuleFindingsServer(moduleId);
        if (resp.success) {
          // remove from local state
          dispatch({ type: 'SET_FINDINGS', payload: state.findings.filter(f => f.moduleId !== moduleId) });
          dataService.setFindings(state.findings.filter(f => f.moduleId !== moduleId));
          toast({ title: 'Findings Cleared', description: `Findings for module ${moduleId} cleared.` });
        } else {
          throw new Error(resp.data?.message || resp.message || 'Failed to clear module findings');
        }
      } else {
        const resp = await apiClient.clearAllFindingsServer();
        if (resp.success) {
          dispatch({ type: 'SET_FINDINGS', payload: [] });
          dataService.setFindings([]);
          toast({ title: 'Findings Cleared', description: 'All findings cleared.' });
        } else {
          throw new Error(resp.data?.message || resp.message || 'Failed to clear all findings');
        }
      }
    } catch (error) {
      console.error('Failed to clear findings:', error);
      toast({ title: 'Clear Failed', description: 'Failed to clear findings on server.', variant: 'destructive' });
    } finally {
      dispatch({ type: 'SET_LOADING', payload: false });
    }
  }, [toast, state.findings]);

  const deleteFinding = useCallback(async (type: string, moduleId: string) => {
    try {
      const response = await apiClient.deleteFinding(type, moduleId);
      
      if (response.success) {
        // Remove the finding from local state
        dispatch({ type: 'SET_FINDINGS', payload: 
          state.findings.filter(f => !(f.type === type && f.moduleId === moduleId))
        });
        
        toast({
          title: 'Finding Deleted',
          description: `Finding "${type}" removed from module ${moduleId}.`,
        });
      } else {
        throw new Error(response.data.message || 'Failed to delete finding');
      }
    } catch (error) {
      console.error('Failed to delete finding:', error);
      toast({
        title: 'Delete Failed',
        description: 'Failed to delete the finding. Please try again.',
        variant: 'destructive'
      });
    }
  }, [toast, state.findings]);

  const deleteMultipleFindings = useCallback(async (identifiers: Array<{ type: string; moduleId: string }>) => {
    try {
      const response = await apiClient.deleteMultipleFindings(identifiers);
      
      if (response.success) {
        // Remove the findings from local state
        const identifierSet = new Set(identifiers.map(id => `${id.type}-${id.moduleId}`));
        dispatch({ type: 'SET_FINDINGS', payload: 
          state.findings.filter(f => !identifierSet.has(`${f.type}-${f.moduleId}`))
        });
        
        toast({
          title: 'Findings Deleted',
          description: `${response.data.deletedCount} finding(s) removed successfully.`,
        });
      } else {
        throw new Error(response.data.message || 'Failed to delete findings');
      }
    } catch (error) {
      console.error('Failed to delete findings:', error);
      toast({
        title: 'Delete Failed',
        description: 'Failed to delete the findings. Please try again.',
        variant: 'destructive'
      });
    }
  }, [toast, state.findings]);

  // Analysis methods
  const runAnalysis = useCallback(async (request: AnalysisRequest): Promise<AnalysisResult> => {
    dispatch({ type: 'SET_LOADING', payload: true });
    
    try {
      const result = await dataService.runAnalysis(request);
      
      // Update findings in state
      dispatch({ type: 'SET_FINDINGS', payload: dataService.getAllFindings() });
      
      toast({
        title: 'Analysis Complete',
        description: `Found ${result.findings.length} issue(s) in module ${request.moduleId}.`,
      });
      
      return result;
    } catch (error) {
      console.error('Analysis failed:', error);
      toast({
        title: 'Analysis Failed',
        description: 'Failed to run analysis. Please try again.',
        variant: 'destructive'
      });
      throw error;
    } finally {
      dispatch({ type: 'SET_LOADING', payload: false });
    }
  }, [toast]);

  // Health Metrics Check
  const checkHealthMetrics = useCallback(async (healthData: HealthMetricsRequest): Promise<Finding[]> => {
    dispatch({ type: 'SET_LOADING', payload: true });
    
    try {
      const response = await apiClient.checkHealthMetrics(healthData);
      
      if (response.success) {
        // Convert response findings to our Finding format and add unique IDs
        const findings = response.data.map((finding, index) => ({
          ...finding,
          id: finding.id || `health-${Date.now()}-${index}`,
          description: finding.details || finding.description || finding.type,
          timestamp: typeof finding.timestamp === 'string' 
            ? new Date(finding.timestamp).getTime() 
            : finding.timestamp
        }));
        
        // Add findings to state
        findings.forEach(finding => {
          dispatch({ type: 'ADD_FINDING', payload: finding });
        });
        
        toast({
          title: 'Health Metrics Analysis Complete',
          description: `Found ${findings.length} health issue(s) in module ${healthData.environment.moduleID}.`,
        });
        
        return findings;
      } else {
        throw new Error(response.message || 'Health metrics check failed');
      }
    } catch (error) {
      console.error('Health metrics check failed:', error);
      toast({
        title: 'Health Metrics Check Failed',
        description: 'Failed to run health metrics analysis. Please try again.',
        variant: 'destructive'
      });
      throw error;
    } finally {
      dispatch({ type: 'SET_LOADING', payload: false });
    }
  }, [toast]);

  // Utility methods
  const isOnline = useCallback(() => {
    return dataService.isBackendAvailable();
  }, []);

  const getLastSyncTime = useCallback(() => {
    return dataService.getLastSyncTime();
  }, []);

  const resetData = useCallback(() => {
    dataService.resetData();
    dispatch({ type: 'REFRESH_DATA' });
    
    toast({
      title: 'Data Reset',
      description: 'All data has been reset to defaults.',
    });
  }, [toast]);

  const reloadMockData = useCallback(() => {
    dataService.reloadMockData();
    dispatch({ type: 'REFRESH_DATA' });
    toast({ title: 'Mock Data Reloaded', description: 'Module data reset to mock defaults and findings cleared.' });
  }, [toast]);

  const contextValue: DataContextType = {
    state,
    getModuleData,
    updateModuleData,
    refreshModuleData,
    getFindingsForModule,
    resolveFinding,
    clearFindings,
    deleteFinding,
    deleteMultipleFindings,
    runAnalysis,
    checkHealthMetrics,
    isOnline,
    getLastSyncTime,
    resetData
    ,
    reloadMockData
  };

  return (
    <DataContext.Provider value={contextValue}>
      {children}
    </DataContext.Provider>
  );
};