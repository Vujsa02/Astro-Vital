import { CrewSymptoms } from '@/types/module';
import { Checkbox } from '@/components/ui/checkbox';
import { Label } from '@/components/ui/label';

interface SymptomsTabProps {
  data: CrewSymptoms;
  onUpdate?: (symptoms: CrewSymptoms) => void;
  readOnly?: boolean;
}

export const SymptomsTab = ({ data, onUpdate, readOnly = false }: SymptomsTabProps) => {
  const symptoms = [
    { key: 'shortnessOfBreath', label: 'Shortness of Breath' },
    { key: 'dizziness', label: 'Dizziness' },
    { key: 'eyeIrritation', label: 'Eye Irritation' },
    { key: 'cough', label: 'Cough' },
    { key: 'headache', label: 'Headache' },
    { key: 'fatigue', label: 'Fatigue' },
  ];

  const handleChange = (key: keyof CrewSymptoms, value: boolean) => {
    if (onUpdate) {
      onUpdate({ ...data, [key]: value });
    }
  };

  return (
    <div className="space-y-6">
      <div className="p-4 bg-card border border-border rounded-lg">
        <div className="text-sm text-muted-foreground mb-4">
          Crew Member: <span className="text-foreground font-mono">{data.crewMemberID}</span>
        </div>
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          {symptoms.map(({ key, label }) => (
            <div key={key} className="flex items-center space-x-3 p-3 bg-secondary/30 rounded border border-border">
              <Checkbox
                id={key}
                checked={data[key as keyof CrewSymptoms] as boolean}
                onCheckedChange={(checked) => handleChange(key as keyof CrewSymptoms, checked as boolean)}
                disabled={readOnly}
              />
              <Label
                htmlFor={key}
                className="text-sm font-medium leading-none peer-disabled:cursor-not-allowed peer-disabled:opacity-70 cursor-pointer"
              >
                {label}
              </Label>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
};
