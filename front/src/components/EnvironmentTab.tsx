import { Environment } from '@/types/module';
import { StatusIndicator } from './StatusIndicator';

interface EnvironmentTabProps {
  data: Environment;
}

const getStatus = (value: number, good: [number, number], warning: [number, number]): 'good' | 'warning' | 'critical' => {
  if (value >= good[0] && value <= good[1]) return 'good';
  if (value >= warning[0] && value <= warning[1]) return 'warning';
  return 'critical';
};

export const EnvironmentTab = ({ data }: EnvironmentTabProps) => {
  return (
    <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
      <StatusIndicator
        status={getStatus(data.o2Level, [19.5, 23.5], [18, 25])}
        label="Oxygen Level"
        value={data.o2Level.toFixed(1)}
        unit="%"
      />
      <StatusIndicator
        status={getStatus(data.co2Level, [0, 0.08], [0.08, 0.15])}
        label="CO₂ Level"
        value={data.co2Level.toFixed(2)}
        unit="%"
      />
      <StatusIndicator
        status={getStatus(data.coLevel, [0, 0.005], [0.005, 0.01])}
        label="CO Level"
        value={data.coLevel.toFixed(3)}
        unit="%"
      />
      <StatusIndicator
        status={getStatus(data.temperature, [18, 27], [15, 30])}
        label="Temperature"
        value={data.temperature.toFixed(1)}
        unit="°C"
      />
      <StatusIndicator
        status={getStatus(data.humidity, [30, 70], [20, 80])}
        label="Humidity"
        value={data.humidity.toFixed(0)}
        unit="%"
      />
      <StatusIndicator
        status={getStatus(data.pressure, [95, 105], [90, 110])}
        label="Pressure"
        value={data.pressure.toFixed(1)}
        unit="kPa"
      />
      <StatusIndicator
        status={getStatus(data.vocLevel, [0, 1], [1, 2])}
        label="VOC Level"
        value={data.vocLevel.toFixed(1)}
        unit="mg/m³"
      />
      <StatusIndicator
        status={getStatus(data.pmLevel, [0, 25], [25, 50])}
        label="PM Level"
        value={data.pmLevel.toFixed(0)}
        unit="µg/m³"
      />
      <StatusIndicator
        status={getStatus(data.dewPoint, [5, 15], [0, 20])}
        label="Dew Point"
        value={data.dewPoint.toFixed(1)}
        unit="°C"
      />
    </div>
  );
};
