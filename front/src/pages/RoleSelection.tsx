import { useNavigate } from 'react-router-dom';
import { Button } from '@/components/ui/button';
import { Users, Wrench, Database } from 'lucide-react';

export default function RoleSelection() {
  const navigate = useNavigate();

  const roles = [
    {
      id: 'crew',
      title: 'Crew Member',
      description: 'Access module information and report symptoms',
      icon: Users,
      path: '/crew',
    },
    {
      id: 'engineer',
      title: 'Life Support Engineer',
      description: 'Monitor all modules and life support systems',
      icon: Wrench,
      path: '/engineer',
    },
    {
      id: 'kb-manager',
      title: 'Knowledge Base Manager',
      description: 'Manage mission knowledge and documentation',
      icon: Database,
      path: '/kb-manager',
    },
  ];

  return (
    <div className="min-h-screen flex items-center justify-center p-8 bg-background">
      <div className="max-w-6xl w-full space-y-8">
        <div className="text-center space-y-4">
          <h1 className="text-5xl font-bold tracking-tight">
            <span className="text-foreground">MISSION</span>{' '}
            <span className="text-primary">CONTROL</span>
          </h1>
          <p className="text-xl text-muted-foreground">Select Your Role</p>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
          {roles.map((role) => (
            <div
              key={role.id}
              className="group relative p-8 bg-card border border-border rounded-lg hover:border-primary transition-all duration-300 hover:shadow-[0_0_20px_hsl(var(--glow-primary))]"
            >
              <div className="space-y-4">
                <div className="w-16 h-16 rounded-full bg-primary/10 flex items-center justify-center group-hover:bg-primary/20 transition-colors">
                  <role.icon className="w-8 h-8 text-primary" />
                </div>
                <div>
                  <h3 className="text-2xl font-bold mb-2">{role.title}</h3>
                  <p className="text-muted-foreground">{role.description}</p>
                </div>
                <Button
                  onClick={() => navigate(role.path)}
                  className="w-full bg-primary hover:bg-primary/90 text-primary-foreground"
                >
                  Access System
                </Button>
              </div>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
}
