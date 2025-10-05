import { ModuleData, ModuleID } from '@/types/module';

export const getMockModuleData = (moduleID: ModuleID): ModuleData => {
  const baseData: Record<ModuleID, ModuleData> = {
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

  return baseData[moduleID];
};
