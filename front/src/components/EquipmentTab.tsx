import { VentilationStatus, AirFilter, WaterRecycling } from '@/types/module';
import { StatusIndicator } from './StatusIndicator';
import { AlertTriangle, CheckCircle } from 'lucide-react';

interface EquipmentTabProps {
  ventilation: VentilationStatus;
  airFilter: AirFilter;
  waterRecycling: WaterRecycling;
}

export const EquipmentTab = ({ ventilation, airFilter, waterRecycling }: EquipmentTabProps) => {
  return (
    <div className="space-y-6">
      <div className="p-6 bg-card border border-border rounded-lg">
        <h3 className="text-lg font-semibold mb-4 flex items-center gap-2">
          Ventilation System
          {ventilation.degraded ? (
            <AlertTriangle className="w-5 h-5 text-[hsl(45,100%,55%)]" />
          ) : (
            <CheckCircle className="w-5 h-5 text-[hsl(145,70%,50%)]" />
          )}
        </h3>
        <div className="flex items-center gap-2">
          <span className="text-muted-foreground">Status:</span>
          <span
            className={`font-bold ${
              ventilation.degraded ? 'text-[hsl(45,100%,55%)]' : 'text-[hsl(145,70%,50%)]'
            }`}
          >
            {ventilation.degraded ? 'DEGRADED' : 'OPERATIONAL'}
          </span>
        </div>
      </div>

      <div className="p-6 bg-card border border-border rounded-lg">
        <h3 className="text-lg font-semibold mb-4 flex items-center gap-2">
          Air Filter
          {airFilter.dirty ? (
            <AlertTriangle className="w-5 h-5 text-[hsl(45,100%,55%)]" />
          ) : (
            <CheckCircle className="w-5 h-5 text-[hsl(145,70%,50%)]" />
          )}
        </h3>
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          <StatusIndicator
            status={airFilter.efficiency >= 95 ? 'good' : airFilter.efficiency >= 85 ? 'warning' : 'critical'}
            label="Efficiency"
            value={airFilter.efficiency.toFixed(1)}
            unit="%"
          />
          <div className="flex flex-col gap-2 p-4 bg-secondary/30 border border-border rounded-lg">
            <div className="text-sm text-muted-foreground uppercase tracking-wider">Filter Condition</div>
            <div
              className={`text-2xl font-bold ${
                airFilter.dirty ? 'text-[hsl(45,100%,55%)]' : 'text-[hsl(145,70%,50%)]'
              }`}
            >
              {airFilter.dirty ? 'DIRTY' : 'CLEAN'}
            </div>
          </div>
        </div>
      </div>

      <div className="p-6 bg-card border border-border rounded-lg">
        <h3 className="text-lg font-semibold mb-4 flex items-center gap-2">
          Water Recycling System
          {waterRecycling.degraded || waterRecycling.leakageDetected ? (
            <AlertTriangle className="w-5 h-5 text-[hsl(45,100%,55%)]" />
          ) : (
            <CheckCircle className="w-5 h-5 text-[hsl(145,70%,50%)]" />
          )}
        </h3>
        <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
          <StatusIndicator
            status={waterRecycling.efficiency >= 95 ? 'good' : waterRecycling.efficiency >= 90 ? 'warning' : 'critical'}
            label="Efficiency"
            value={waterRecycling.efficiency.toFixed(1)}
            unit="%"
          />
          <div className="flex flex-col gap-2 p-4 bg-secondary/30 border border-border rounded-lg">
            <div className="text-sm text-muted-foreground uppercase tracking-wider">System Status</div>
            <div
              className={`text-2xl font-bold ${
                waterRecycling.degraded ? 'text-[hsl(45,100%,55%)]' : 'text-[hsl(145,70%,50%)]'
              }`}
            >
              {waterRecycling.degraded ? 'DEGRADED' : 'OPERATIONAL'}
            </div>
          </div>
          <div className="flex flex-col gap-2 p-4 bg-secondary/30 border border-border rounded-lg">
            <div className="text-sm text-muted-foreground uppercase tracking-wider">Leakage</div>
            <div
              className={`text-2xl font-bold ${
                waterRecycling.leakageDetected ? 'text-[hsl(0,85%,55%)]' : 'text-[hsl(145,70%,50%)]'
              }`}
            >
              {waterRecycling.leakageDetected ? 'DETECTED' : 'NONE'}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};
