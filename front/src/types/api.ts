export interface Finding {
  id?: string;
  type: string;
  moduleId: string;
  description?: string;
  details?: string;
  priority: 'LOW' | 'MEDIUM' | 'HIGH' | 'CRITICAL';
  timestamp: number | string;
  resolved?: boolean;
  expiresAt?: string;
  notified?: boolean;
  expired?: boolean;
}

export interface AnalysisResult {
  findings: Finding[];
  timestamp: number;
  moduleId: string;
  analysisType: 'environmental' | 'air-quality' | 'equipment';
}

export interface ApiResponse<T> {
  success: boolean;
  data: T;
  message?: string;
  timestamp: number;
}

export interface UpdateModuleDataRequest {
  moduleId: string;
  data: Partial<import('./module').ModuleData>;
}

export interface AnalysisRequest {
  moduleId: string;
  analysisType: 'environmental' | 'air-quality' | 'equipment';
  data: any;
}

// Health Metrics Types
export interface HealthMetricsRequest {
  environment: {
    moduleID: string;
    o2Level: number;
    co2Level: number;
    temperature: number;
    dewPoint: number;
  };
  vitals: {
    spo2: number;
  };
  crewSymptoms: {
    shortnessOfBreath: boolean;
    dizziness: boolean;
    eyeIrritation: boolean;
  };
  ventilationStatus: {
    moduleID: string;
    degraded: boolean;
  };
}