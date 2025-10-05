import { ModuleData, ModuleID, Environment, CrewSymptoms } from '@/types/module';
import { Finding, AnalysisResult, ApiResponse, AnalysisRequest } from '@/types/api';

const API_BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080/api';

class ApiClient {
  private async request<T>(
    endpoint: string,
    options: RequestInit = {}
  ): Promise<ApiResponse<T>> {
    const url = `${API_BASE_URL}${endpoint}`;
    
    const config: RequestInit = {
      headers: {
        'Content-Type': 'application/json',
        ...options.headers,
      },
      ...options,
    };

    try {
      const response = await fetch(url, config);
      
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }

      const data = await response.json();
      
      return {
        success: true,
        data,
        timestamp: Date.now()
      };
    } catch (error) {
      console.error('API request failed:', error);
      return {
        success: false,
        data: null as unknown as T,
        message: error instanceof Error ? error.message : 'Unknown error',
        timestamp: Date.now()
      };
    }
  }

  // Module Data Endpoints
  async getModuleData(moduleId: ModuleID): Promise<ApiResponse<ModuleData>> {
    return this.request<ModuleData>(`/modules/${moduleId}`);
  }

  async getAllModuleData(): Promise<ApiResponse<Record<ModuleID, ModuleData>>> {
    return this.request<Record<ModuleID, ModuleData>>('/modules');
  }

  async updateModuleData(
    moduleId: ModuleID, 
    data: Partial<ModuleData>
  ): Promise<ApiResponse<ModuleData>> {
    return this.request<ModuleData>(`/modules/${moduleId}`, {
      method: 'PUT',
      body: JSON.stringify(data),
    });
  }

  async updateEnvironment(
    moduleId: ModuleID, 
    environment: Partial<Environment>
  ): Promise<ApiResponse<Environment>> {
    return this.request<Environment>(`/modules/${moduleId}/environment`, {
      method: 'PUT',
      body: JSON.stringify(environment),
    });
  }

  async updateSymptoms(
    moduleId: ModuleID, 
    symptoms: CrewSymptoms
  ): Promise<ApiResponse<CrewSymptoms>> {
    return this.request<CrewSymptoms>(`/modules/${moduleId}/symptoms`, {
      method: 'PUT',
      body: JSON.stringify(symptoms),
    });
  }

  // Analysis Endpoints
  async runEnvironmentalAnalysis(
    moduleId: ModuleID,
    environments: Environment[],
    condensationData?: any[],
    humidityEvents?: any[],
    waterRecyclings?: any[],
    ventilationStatuses?: any[]
  ): Promise<ApiResponse<AnalysisResult>> {
    return this.request<AnalysisResult>('/analysis/environmental', {
      method: 'POST',
      body: JSON.stringify({
        moduleId,
        environments,
        condensationData: condensationData || [],
        humidityEvents: humidityEvents || [],
        waterRecyclings: waterRecyclings || [],
        ventilationStatuses: ventilationStatuses || []
      }),
    });
  }

  async runAirQualityAnalysis(
    moduleId: ModuleID,
    environments: Environment[],
    airQualityEvents: any[]
  ): Promise<ApiResponse<AnalysisResult>> {
    return this.request<AnalysisResult>('/analysis/air-quality', {
      method: 'POST',
      body: JSON.stringify({
        moduleId,
        environments,
        airQualityEvents
      }),
    });
  }

  async runEquipmentAnalysis(
    moduleId: ModuleID,
    equipment: any
  ): Promise<ApiResponse<AnalysisResult>> {
    return this.request<AnalysisResult>('/analysis/equipment', {
      method: 'POST',
      body: JSON.stringify({
        moduleId,
        equipment
      }),
    });
  }

  // Generic analysis endpoint (falls back to this if specific endpoints don't exist)
  async runAnalysis(request: AnalysisRequest): Promise<ApiResponse<AnalysisResult>> {
    return this.request<AnalysisResult>('/analysis', {
      method: 'POST',
      body: JSON.stringify(request),
    });
  }

  // Findings Endpoints
  async getFindings(moduleId?: ModuleID): Promise<ApiResponse<Finding[]>> {
    const endpoint = moduleId ? `/findings?moduleId=${moduleId}` : '/findings';
    return this.request<Finding[]>(endpoint);
  }

  // Direct calls to backend FindingsController (no /api prefix)
  async fetchAllFindingsFromServer(): Promise<ApiResponse<Record<string, Finding[]>>> {
    const url = `${API_BASE_URL}/findings`;
    try {
      const response = await fetch(url, { headers: { 'Content-Type': 'application/json' } });
      if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
      const data = await response.json();
      return { success: true, data, timestamp: Date.now() };
    } catch (error) {
      console.error('Fetch all findings failed:', error);
      return { success: false, data: {} as Record<string, Finding[]>, message: error instanceof Error ? error.message : 'Unknown error', timestamp: Date.now() };
    }
  }

  async fetchModuleFindingsFromServer(moduleId: ModuleID): Promise<ApiResponse<Finding[]>> {
    const url = `${API_BASE_URL.replace(/\/api\/?$/, '')}/findings/${encodeURIComponent(moduleId)}`;
    try {
      const response = await fetch(url, { headers: { 'Content-Type': 'application/json' } });
      if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
      const data = await response.json();
      return { success: true, data, timestamp: Date.now() };
    } catch (error) {
      console.error('Fetch module findings failed:', error);
      return { success: false, data: [] as Finding[], message: error instanceof Error ? error.message : 'Unknown error', timestamp: Date.now() };
    }
  }

  async resolveFinding(findingId: string): Promise<ApiResponse<void>> {
    return this.request<void>(`/findings/${findingId}/resolve`, {
      method: 'POST',
    });
  }

  async clearFindings(moduleId?: ModuleID): Promise<ApiResponse<void>> {
    const endpoint = moduleId ? `/findings?moduleId=${moduleId}` : '/findings';
    return this.request<void>(endpoint, {
      method: 'DELETE',
    });
  }

  // Delete specific finding by type and moduleId (using health-metrics endpoint)
  async deleteFinding(type: string, moduleId: string): Promise<ApiResponse<{ success: boolean; message: string }>> {
    const root = API_BASE_URL.replace(/\/api\/?$/, '');
    const url = `${root}/findings/single`;
    
    const config: RequestInit = {
      method: 'DELETE',
      headers: {
        'Content-Type': 'application/json',
      },
    };

    try {
      const response = await fetch(`${url}?type=${encodeURIComponent(type)}&moduleId=${encodeURIComponent(moduleId)}`, config);
      
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }

      const data = await response.json();
      
      return {
        success: true,
        data,
        timestamp: Date.now()
      };
    } catch (error) {
      console.error('Delete finding failed:', error);
      return {
        success: false,
        data: { success: false, message: error instanceof Error ? error.message : 'Unknown error' },
        message: error instanceof Error ? error.message : 'Unknown error',
        timestamp: Date.now()
      };
    }
  }

  // Delete multiple findings
  async deleteMultipleFindings(identifiers: Array<{ type: string; moduleId: string }>): Promise<ApiResponse<{ success: boolean; message: string; deletedCount: number }>> {
    const url = `${API_BASE_URL}/findings/multiple`;

    const config: RequestInit = {
      method: 'DELETE',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(identifiers),
    };

    try {
      const response = await fetch(url, config);
      
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }

      const data = await response.json();
      
      return {
        success: true,
        data,
        timestamp: Date.now()
      };
    } catch (error) {
      console.error('Delete multiple findings failed:', error);
      return {
        success: false,
        data: { success: false, message: error instanceof Error ? error.message : 'Unknown error', deletedCount: 0 },
        message: error instanceof Error ? error.message : 'Unknown error',
        timestamp: Date.now()
      };
    }
  }

  async clearModuleFindingsServer(moduleId: string): Promise<ApiResponse<{ success: boolean; message: string }>> {
    const url = `${API_BASE_URL}/findings/${encodeURIComponent(moduleId)}`;
    try {
      const response = await fetch(url, { method: 'DELETE', headers: { 'Content-Type': 'application/json' } });
      if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
      const data = await response.json();
      return { success: true, data, timestamp: Date.now() };
    } catch (error) {
      console.error('Clear module findings failed:', error);
      return { success: false, data: { success: false, message: error instanceof Error ? error.message : 'Unknown error' }, message: error instanceof Error ? error.message : 'Unknown error', timestamp: Date.now() };
    }
  }

  async clearAllFindingsServer(): Promise<ApiResponse<{ success: boolean; message: string }>> {
    const url = `${API_BASE_URL}/findings`;
    try {
      const response = await fetch(url, { method: 'DELETE', headers: { 'Content-Type': 'application/json' } });
      if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
      const data = await response.json();
      return { success: true, data, timestamp: Date.now() };
    } catch (error) {
      console.error('Clear all findings failed:', error);
      return { success: false, data: { success: false, message: error instanceof Error ? error.message : 'Unknown error' }, message: error instanceof Error ? error.message : 'Unknown error', timestamp: Date.now() };
    }
  }

  // Health Check
  async healthCheck(): Promise<ApiResponse<{ status: string; timestamp: number }>> {
    return this.request<{ status: string; timestamp: number }>('/health');
  }

  // Health Metrics Check - NEW
  async checkHealthMetrics(healthData: import('@/types/api').HealthMetricsRequest): Promise<ApiResponse<import('@/types/api').Finding[]>> {
    const root = API_BASE_URL.replace(/\/api\/?$/, '');
    const url = `${root}/health-metrics/check`;
    
    const config: RequestInit = {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(healthData),
    };

    try {
      const response = await fetch(url, config);
      
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }

      const data = await response.json();
      
      return {
        success: true,
        data,
        timestamp: Date.now()
      };
    } catch (error) {
      console.error('Health metrics check failed:', error);
      return {
        success: false,
        data: [] as import('@/types/api').Finding[],
        message: error instanceof Error ? error.message : 'Unknown error',
        timestamp: Date.now()
      };
    }
  }

  // Equipment maintenance check endpoint
  async checkEquipmentMaintenance(equipmentData: any): Promise<ApiResponse<any>> {
    const root = API_BASE_URL.replace(/\/api\/?$/, '');
    const url = `${root}/equipment-maintenance/check`;

    try {
      const response = await fetch(url, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(equipmentData),
      });

      if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
      const data = await response.json();
      return { success: true, data, timestamp: Date.now() };
    } catch (error) {
      console.error('Equipment maintenance check failed:', error);
      return { success: false, data: null as any, message: error instanceof Error ? error.message : 'Unknown error', timestamp: Date.now() };
    }
  }

  // Utility method to check if backend is available
  async isBackendAvailable(): Promise<boolean> {
    try {
      const response = await this.healthCheck();
      return response.success;
    } catch {
      return false;
    }
  }
}

export const apiClient = new ApiClient();