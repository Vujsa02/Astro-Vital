# Astro-Vital Frontend Data Architecture

## Overview

The frontend has been enhanced with a comprehensive data management system that provides:

- **Centralized state management** via React Context
- **Local data persistence** with localStorage
- **Backend synchronization** with your Spring Boot API
- **Offline-first architecture** with fallback simulation
- **Real-time data updates** and notifications

## Architecture Components

### 1. Data Service (`src/services/dataService.ts`)

The core data management service that handles:

- **Local Storage Management**: Persists data locally for offline use
- **Backend Synchronization**: Syncs with your Spring Boot backend when online
- **Simulation Fallback**: Provides realistic data when backend is unavailable
- **State Management**: Maintains consistent data state across the application

#### Key Features:

- Automatic offline/online detection
- Local data persistence
- Backend sync when connection restored
- Realistic analysis simulation for development

### 2. API Client (`src/services/apiClient.ts`)

RESTful API client for backend communication:

```typescript
// Module data operations
apiClient.getModuleData(moduleId)
apiClient.updateModuleData(moduleId, data)
apiClient.updateEnvironment(moduleId, environment)
apiClient.updateSymptoms(moduleId, symptoms)

// Analysis operations
apiClient.runEnvironmentalAnalysis(moduleId, environments, ...)
apiClient.runAirQualityAnalysis(moduleId, environments, events)
apiClient.runEquipmentAnalysis(moduleId, equipment)

// Findings management
apiClient.getFindings(moduleId)
apiClient.resolveFinding(findingId)
apiClient.clearFindings(moduleId)
```

### 3. Data Context (`src/contexts/DataContext.tsx`)

React Context providing application-wide state management:

```typescript
const {
  state, // Current application state
  getModuleData, // Get data for specific module
  updateModuleData, // Update module data
  getFindingsForModule, // Get findings for module
  runAnalysis, // Run backend analysis
  resolveFinding, // Mark finding as resolved
  clearFindings, // Clear findings
  isOnline, // Check backend connectivity
  resetData, // Reset to defaults
} = useData();
```

## Component Updates

### Enhanced ModuleView Component

The ModuleView now provides:

- **Real-time data updates** with auto-refresh every 30 seconds
- **Analysis integration** with "Run Analysis" button
- **Findings display** showing active alerts and recommendations
- **Connection status** indicators
- **Interactive symptoms updating** for crew members

### New Dashboard Components

#### SystemOverview Component

- **System-wide status** at a glance
- **Module health indicators** with click-to-navigate
- **Active findings summary** across all modules
- **Connection and sync status**

#### FindingsPanel Component

- **Active findings display** with priority indicators
- **Finding resolution** with one-click actions
- **Module-specific** or system-wide views
- **Timestamp tracking** for audit trails

## Data Flow

### 1. Data Updates

```
User Input → DataContext → DataService → [Backend API + Local Storage]
```

### 2. Backend Analysis

```
Analysis Request → API Client → Spring Boot Backend → Findings → UI Notifications
```

### 3. Offline Mode

```
User Input → Local Storage → Queue for Sync → Auto-sync when Online
```

## Backend Integration

### Expected API Endpoints

Your Spring Boot backend should provide these endpoints:

```
GET    /api/modules                    - Get all module data
GET    /api/modules/{moduleId}         - Get specific module data
PUT    /api/modules/{moduleId}         - Update module data
PUT    /api/modules/{moduleId}/environment - Update environment data
PUT    /api/modules/{moduleId}/symptoms    - Update crew symptoms

POST   /api/analysis                  - Run generic analysis
POST   /api/analysis/environmental    - Run environmental analysis
POST   /api/analysis/air-quality      - Run air quality analysis
POST   /api/analysis/equipment        - Run equipment analysis

GET    /api/findings                  - Get all findings
GET    /api/findings?moduleId={id}    - Get findings for module
POST   /api/findings/{id}/resolve     - Resolve finding
DELETE /api/findings                  - Clear all findings
DELETE /api/findings?moduleId={id}    - Clear module findings

GET    /api/health                    - Health check
```

### Data Models

The frontend expects your backend to use these data structures:

```typescript
interface Environment {
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

interface Finding {
  id: string;
  type: string;
  moduleId: string;
  description: string;
  priority: "LOW" | "MEDIUM" | "HIGH" | "CRITICAL";
  timestamp: number;
  resolved?: boolean;
}

interface AnalysisResult {
  findings: Finding[];
  timestamp: number;
  moduleId: string;
  analysisType: "environmental" | "air-quality" | "equipment";
}
```

## Configuration

### Environment Variables

Create a `.env` file in the frontend root:

```bash
VITE_API_URL=http://localhost:8080/api
```

### Backend URL Configuration

The API client automatically uses:

- `process.env.VITE_API_URL` if set
- Falls back to `http://localhost:8080/api`

## Usage Examples

### Updating Module Data

```typescript
// In a component
const { updateModuleData } = useData();

const handleSymptomsChange = async (symptoms) => {
  await updateModuleData("CMD", { symptoms });
};
```

### Running Analysis

```typescript
const { runAnalysis } = useData();

const analyzeEnvironment = async () => {
  const result = await runAnalysis({
    moduleId: "LAB",
    analysisType: "environmental",
    data: environmentData,
  });

  // Findings automatically added to state
  console.log(`Found ${result.findings.length} issues`);
};
```

### Managing Findings

```typescript
const { getFindingsForModule, resolveFinding } = useData();

const moduleFindings = getFindingsForModule("CMD");
const criticalFindings = moduleFindings.filter(
  (f) => f.priority === "CRITICAL"
);

// Resolve a finding
await resolveFinding(findingId);
```

## Development Features

### Offline Development

- Works without backend connection
- Simulates realistic analysis results
- Persists data locally for consistent development experience

### Data Simulation

- Generates contextual findings based on environmental thresholds
- Simulates different priority levels
- Creates realistic timestamps and descriptions

### State Persistence

- Survives browser refreshes
- Maintains findings across sessions
- Preserves user modifications

## Testing the Integration

1. **Start your Spring Boot backend** on `http://localhost:8080`

2. **Frontend will automatically**:

   - Detect backend availability
   - Show connection status in UI
   - Sync data when backend responds
   - Fall back to simulation when offline

3. **Test scenarios**:
   - Update symptoms → should persist and sync
   - Run analysis → should call backend and display findings
   - Go offline → should continue working with local data
   - Come back online → should auto-sync changes

## Troubleshooting

### Backend Connection Issues

- Check `VITE_API_URL` configuration
- Verify backend is running on expected port
- Check browser console for CORS issues
- Ensure backend endpoints match expected API

### Data Not Persisting

- Check localStorage in browser dev tools
- Verify `updateModuleData` calls are awaited
- Check for JavaScript errors in console

### Analysis Not Working

- Verify backend analysis endpoints exist
- Check request/response formats match expected schema
- Enable network tab to see API calls

This architecture provides a robust, offline-capable data management system that seamlessly integrates with your Drools-based backend while providing an excellent user experience even when the backend is unavailable.
