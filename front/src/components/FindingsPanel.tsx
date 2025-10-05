import { useData } from '@/contexts/DataContext';
import { Badge } from '@/components/ui/badge';
import { Button } from '@/components/ui/button';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { ScrollArea } from '@/components/ui/scroll-area';
import { CheckCircle, AlertTriangle, Clock, X } from 'lucide-react';
import { Finding } from '@/types/api';
import { ModuleID } from '@/types/module';

interface FindingsPanelProps {
  moduleId?: ModuleID;
  maxHeight?: string;
}

export const FindingsPanel = ({ moduleId, maxHeight = "400px" }: FindingsPanelProps) => {
  const { state, getFindingsForModule, resolveFinding, deleteMultipleFindings } = useData();
  
  const findings = moduleId 
    ? getFindingsForModule(moduleId)
    : state.findings.filter(f => !f.resolved);

  const getPriorityColor = (priority: Finding['priority']) => {
    switch (priority) {
      case 'CRITICAL': return 'destructive';
      case 'HIGH': return 'destructive';
      case 'MEDIUM': return 'secondary';
      case 'LOW': return 'outline';
      default: return 'outline';
    }
  };

  const getPriorityIcon = (priority: Finding['priority']) => {
    switch (priority) {
      case 'CRITICAL': return <AlertTriangle className="w-4 h-4 text-red-500" />;
      case 'HIGH': return <AlertTriangle className="w-4 h-4 text-orange-500" />;
      case 'MEDIUM': return <Clock className="w-4 h-4 text-yellow-500" />;
      case 'LOW': return <Clock className="w-4 h-4 text-blue-500" />;
      default: return <Clock className="w-4 h-4 text-gray-500" />;
    }
  };

  const formatTimestamp = (timestamp: number | string) => {
    const date = typeof timestamp === 'string' ? new Date(timestamp) : new Date(timestamp);
    return date.toLocaleString();
  };

  if (findings.length === 0) {
    return (
      <Card className="mb-4">
        <CardHeader>
          <CardTitle className="flex items-center gap-2">
            <CheckCircle className="w-5 h-5 text-green-500" />
            {moduleId ? `${moduleId} Module Status` : 'System Status'}
          </CardTitle>
        </CardHeader>
        <CardContent>
          <p className="text-muted-foreground text-center py-8">
            No active findings. All systems operating normally.
          </p>
        </CardContent>
      </Card>
    );
  }

  return (
    <Card className="mb-4">
      <CardHeader>
        <div className="flex items-center justify-between">
          <CardTitle className="flex items-center gap-2">
            <AlertTriangle className="w-5 h-5 text-yellow-500" />
            {moduleId ? `${moduleId} Module Findings` : 'System Findings'}
            <Badge variant="secondary">{findings.length}</Badge>
          </CardTitle>
          {findings.length > 0 && (
            <Button
              variant="outline"
              size="sm"
              onClick={async () => {
                const identifiers = findings.map(f => ({ type: f.type, moduleId: f.moduleId }));
                await deleteMultipleFindings(identifiers);
              }}
            >
              Clear All
            </Button>
          )}
        </div>
      </CardHeader>
      <CardContent>
        <ScrollArea style={{ maxHeight: maxHeight, overflowY: 'auto' }}>
          <div className="space-y-3">
            {findings.map((finding) => (
              <div
                key={finding.id}
                className="border rounded-lg p-3 space-y-2 hover:bg-muted/50 transition-colors"
              >
                <div className="flex items-start justify-between">
                  <div className="flex items-center gap-2">
                    {getPriorityIcon(finding.priority)}
                    <span className="font-medium">{finding.type}</span>
                    <Badge variant={getPriorityColor(finding.priority)}>
                      {finding.priority}
                    </Badge>
                  </div>
                  <Button
                    variant="ghost"
                    size="sm"
                    onClick={() => finding.id && resolveFinding(finding.id)}
                    className="h-6 w-6 p-0"
                    title="Delete finding (server)"
                  >
                    <X className="w-4 h-4" />
                  </Button>
                </div>
                
                <p className="text-sm text-muted-foreground">
                  {finding.details || finding.description || finding.type}
                </p>
                
                <div className="flex items-center justify-between text-xs text-muted-foreground">
                  <span>Module: {finding.moduleId}</span>
                  <span>{formatTimestamp(finding.timestamp)}</span>
                </div>
              </div>
            ))}
          </div>
        </ScrollArea>
      </CardContent>
    </Card>
  );
};