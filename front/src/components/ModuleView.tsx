import { useState } from 'react';
import { ModuleID } from '@/types/module';
import { getMockModuleData } from '@/data/mockData';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';
import { EnvironmentTab } from './EnvironmentTab';
import { SymptomsTab } from './SymptomsTab';
import { EquipmentTab } from './EquipmentTab';

interface ModuleViewProps {
  moduleID: ModuleID;
}

export const ModuleView = ({ moduleID }: ModuleViewProps) => {
  const [data] = useState(() => getMockModuleData(moduleID));

  const moduleNames = {
    CMD: 'Command Module',
    LAB: 'Laboratory Module',
    COMM: 'Communication Module',
  };

  return (
    <div className="space-y-6">
      <div className="border-l-4 border-primary pl-4">
        <h2 className="text-3xl font-bold text-foreground">{moduleNames[moduleID]}</h2>
        <p className="text-muted-foreground">Module ID: {moduleID}</p>
      </div>

      <Tabs defaultValue="environment" className="w-full">
        <TabsList className="grid w-full grid-cols-3 bg-secondary">
          <TabsTrigger value="environment">Environment</TabsTrigger>
          <TabsTrigger value="symptoms">Symptoms</TabsTrigger>
          <TabsTrigger value="equipment">Equipment</TabsTrigger>
        </TabsList>

        <TabsContent value="environment" className="mt-6">
          <EnvironmentTab data={data.environment} />
        </TabsContent>

        <TabsContent value="symptoms" className="mt-6">
          <SymptomsTab data={data.symptoms} readOnly />
        </TabsContent>

        <TabsContent value="equipment" className="mt-6">
          <EquipmentTab
            ventilation={data.ventilation}
            airFilter={data.airFilter}
            waterRecycling={data.waterRecycling}
          />
        </TabsContent>
      </Tabs>
    </div>
  );
};
