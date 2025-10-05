import { cn } from '@/lib/utils';

interface StatusIndicatorProps {
  status: 'good' | 'warning' | 'critical';
  label: string;
  value: string | number;
  unit?: string;
}

export const StatusIndicator = ({ status, label, value, unit }: StatusIndicatorProps) => {
  const statusColors = {
    good: 'text-[hsl(145,70%,50%)]',
    warning: 'text-[hsl(45,100%,55%)]',
    critical: 'text-[hsl(0,85%,55%)]',
  };

  const glowColors = {
    good: 'shadow-[0_0_10px_hsl(var(--glow-success))]',
    warning: 'shadow-[0_0_10px_hsl(var(--glow-warning))]',
    critical: 'shadow-[0_0_10px_hsl(var(--glow-danger))]',
  };

  return (
    <div className="flex flex-col gap-2 p-4 bg-card border border-border rounded-lg">
      <div className="text-sm text-muted-foreground uppercase tracking-wider">{label}</div>
      <div className={cn('text-2xl font-bold font-mono', statusColors[status], glowColors[status])}>
        {value}
        {unit && <span className="text-lg ml-1">{unit}</span>}
      </div>
    </div>
  );
};
