export type ModuleID = 'CMD' | 'LAB' | 'COMM';

export interface Environment {
  o2Level: number;
  co2Level: number;
  coLevel: number;
  moduleID: ModuleID;
  temperature: number;
  humidity: number;
  pressure: number;
  vocLevel: number;
  pmLevel: number;
  dewPoint: number;
}

export interface CrewSymptoms {
  shortnessOfBreath: boolean;
  dizziness: boolean;
  eyeIrritation: boolean;
  crewMemberID: string;
  cough: boolean;
  headache: boolean;
  fatigue: boolean;
}

export interface VentilationStatus {
  degraded: boolean;
}

export interface AirFilter {
  dirty: boolean;
  efficiency: number;
  moduleID: ModuleID;
}

export interface WaterRecycling {
  moduleID: ModuleID;
  degraded: boolean;
  efficiency: number;
  leakageDetected: boolean;
}

export interface ModuleData {
  environment: Environment;
  symptoms: CrewSymptoms;
  ventilation: VentilationStatus;
  airFilter: AirFilter;
  waterRecycling: WaterRecycling;
}

export type UserRole = 'crew' | 'engineer' | 'kb-manager';
