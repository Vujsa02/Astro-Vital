import { Environment } from '@/types/module';
import { StatusIndicator } from './StatusIndicator';
import { useEffect, useState } from 'react';

type Range = [number, number];

const FALLBACK_RANGES: Record<string, { good: Range; warning: Range }> = {
  o2Level: { good: [19.5, 23.5], warning: [18, 25] },
  co2Level: { good: [0, 0.08], warning: [0.08, 0.15] },
  coLevel: { good: [0, 0.005], warning: [0.005, 0.01] },
  temperature: { good: [18, 27], warning: [15, 30] },
  humidity: { good: [30, 70], warning: [20, 80] },
  pressure: { good: [95, 105], warning: [90, 110] },
  vocLevel: { good: [0, 1], warning: [1, 2] },
  pmLevel: { good: [0, 25], warning: [25, 50] },
  dewPoint: { good: [5, 15], warning: [0, 20] }
};

interface EnvironmentTabProps {
  data: Environment;
}

const getStatus = (value: number, good: [number, number], warning: [number, number]): 'good' | 'warning' | 'critical' => {
  if (value >= good[0] && value <= good[1]) return 'good';
  if (value >= warning[0] && value <= warning[1]) return 'warning';
  return 'critical';
};

export const EnvironmentTab = ({ data }: EnvironmentTabProps) => {
  const [ranges, setRanges] = useState<Record<string, { good: Range; warning: Range }>>({});

  useEffect(() => {
    let mounted = true;
    const load = async () => {
      try {
        const API_BASE = import.meta.env.VITE_API_URL || 'http://localhost:8080/api';
        const resp = await fetch(`${API_BASE}/api/environmental-templates/default-thresholds`);
        if (!resp.ok) throw new Error(`HTTP ${resp.status}`);
        const body = await resp.json(); // array of thresholds

        // Sort thresholds by module id and log them so developer can inspect what's loaded
        const thresholds = Array.isArray(body) ? (body.slice().sort((a: any, b: any) => {
          const ma = (a.moduleId || a.moduleID || '').toString();
          const mb = (b.moduleId || b.moduleID || '').toString();
          return ma.localeCompare(mb);
        })) : [];
        console.log('EnvironmentTab: loaded thresholds (sorted by moduleId):', thresholds);

        // build ranges for this module
        const moduleId = (data as any).moduleID || (data as any).moduleId || data.moduleID;
        const byParam: Record<string, any[]> = {};
        (thresholds || []).forEach((t: any) => {
          // normalize module id (COMMAND -> CMD etc.) if needed
          const m = (t.moduleId === 'COMMAND') ? 'CMD' : (t.moduleId === 'HABITAT' ? 'COMM' : t.moduleId || t.moduleID);
          if (m !== moduleId) return;
          const p = t.parameter;
          if (!byParam[p]) byParam[p] = [];
          // parse numeric threshold
          const thr = Number(t.threshold);
          if (!isNaN(thr)) byParam[p].push({ ...t, threshold: thr });
        });

        const computeFor = (param: string) => {
          const list = byParam[param] || [];
          const lt = list.filter((x: any) => x.operator === '<').sort((a: any, b: any) => a.threshold - b.threshold);
          const gt = list.filter((x: any) => x.operator === '>').sort((a: any, b: any) => b.threshold - a.threshold);

          // Small delta used to create a small padding around single-sided thresholds.
          // Use a much smaller minimum so tiny thresholds (e.g. 0.01) don't collapse ranges to zero.
          const smallDelta = (v: number) => Math.max((Math.abs(v) * 0.02), 0.0001);

          const lowCritical = lt[0]?.threshold;
          const lowWarning = lt[1]?.threshold ?? (lowCritical != null ? lowCritical + smallDelta(lowCritical) : undefined);

          const highCritical = gt[0]?.threshold;
          const highWarning = gt[1]?.threshold ?? (highCritical != null ? highCritical - smallDelta(highCritical) : undefined);

          const fallback = (FALLBACK_RANGES as any)[param];

          const goodMin = lowWarning ?? (lowCritical != null ? lowCritical + smallDelta(lowCritical) : (fallback ? fallback.good[0] : Number.NEGATIVE_INFINITY));
          const goodMax = highWarning ?? (highCritical != null ? highCritical - smallDelta(highCritical) : (fallback ? fallback.good[1] : Number.POSITIVE_INFINITY));

          const warningMin = lowCritical ?? (fallback ? fallback.warning[0] : goodMin);
          const warningMax = highCritical ?? (fallback ? fallback.warning[1] : goodMax);

          return {
            good: [Number(goodMin), Number(goodMax)] as Range,
            warning: [Number(warningMin), Number(warningMax)] as Range
          };
        };

        const computed: Record<string, { good: Range; warning: Range }> = {};
        Object.keys(FALLBACK_RANGES).forEach(p => {
          computed[p] = computeFor(p);
        });

        if (mounted) setRanges(computed);
      } catch (err) {
        console.warn('Failed to load threshold templates for EnvironmentTab', err);
        // leave ranges empty to fall back to defaults
      }
    };

    load();
    return () => { mounted = false; };
  }, [data.moduleID, data]);

  const getRanges = (param: string) => {
    return ranges[param] ?? (FALLBACK_RANGES as any)[param];
  };

  return (
    <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
      <StatusIndicator
        status={data.o2Level != null ? getStatus(data.o2Level, getRanges('o2Level').good, getRanges('o2Level').warning) : 'critical'}
        label="Oxygen Level"
        value={data.o2Level != null ? data.o2Level.toFixed(1) : 'N/A'}
        unit="%"
      />
      <StatusIndicator
        status={data.co2Level != null ? getStatus(data.co2Level, getRanges('co2Level').good, getRanges('co2Level').warning) : 'critical'}
        label="CO₂ Level"
        value={data.co2Level != null ? data.co2Level.toFixed(2) : 'N/A'}
        unit="%"
      />
      <StatusIndicator
        status={data.coLevel != null ? getStatus(data.coLevel, getRanges('coLevel').good, getRanges('coLevel').warning) : 'critical'}
        label="CO Level"
        value={data.coLevel != null ? data.coLevel.toFixed(3) : 'N/A'}
        unit="%"
      />
      <StatusIndicator
        status={data.temperature != null ? getStatus(data.temperature, getRanges('temperature').good, getRanges('temperature').warning) : 'critical'}
        label="Temperature"
        value={data.temperature != null ? data.temperature.toFixed(1) : 'N/A'}
        unit="°C"
      />
      <StatusIndicator
        status={data.humidity != null ? getStatus(data.humidity, getRanges('humidity').good, getRanges('humidity').warning) : 'critical'}
        label="Humidity"
        value={data.humidity != null ? data.humidity.toFixed(0) : 'N/A'}
        unit="%"
      />
      <StatusIndicator
        status={data.pressure != null ? getStatus(data.pressure, getRanges('pressure').good, getRanges('pressure').warning) : 'critical'}
        label="Pressure"
        value={data.pressure != null ? data.pressure.toFixed(1) : 'N/A'}
        unit="kPa"
      />
      <StatusIndicator
        status={data.vocLevel != null ? getStatus(data.vocLevel, getRanges('vocLevel').good, getRanges('vocLevel').warning) : 'critical'}
        label="VOC Level"
        value={data.vocLevel != null ? data.vocLevel.toFixed(1) : 'N/A'}
        unit="mg/m³"
      />
      <StatusIndicator
        status={data.pmLevel != null ? getStatus(data.pmLevel, getRanges('pmLevel').good, getRanges('pmLevel').warning) : 'critical'}
        label="PM Level"
        value={data.pmLevel != null ? data.pmLevel.toFixed(0) : 'N/A'}
        unit="µg/m³"
      />
      <StatusIndicator
        status={data.dewPoint != null ? getStatus(data.dewPoint, getRanges('dewPoint').good, getRanges('dewPoint').warning) : 'critical'}
        label="Dew Point"
        value={data.dewPoint != null ? data.dewPoint.toFixed(1) : 'N/A'}
        unit="°C"
      />
    </div>
  );
};
