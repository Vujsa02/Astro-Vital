import { Toaster } from "@/components/ui/toaster";
import { Toaster as Sonner } from "@/components/ui/sonner";
import { TooltipProvider } from "@/components/ui/tooltip";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { BrowserRouter, Routes, Route } from "react-router-dom";
import { DataProvider } from "@/contexts/DataContext";
import RoleSelection from "./pages/RoleSelection";
import CrewMember from "./pages/CrewMember";
import LifeSupportEngineer from "./pages/LifeSupportEngineer";
import KnowledgeBaseManager from "./pages/KnowledgeBaseManager";
import NotFound from "./pages/NotFound";

const queryClient = new QueryClient();

const App = () => (
  <QueryClientProvider client={queryClient}>
    <DataProvider>
      <TooltipProvider>
        <Toaster />
        <Sonner />
        <BrowserRouter>
          <Routes>
            <Route path="/" element={<RoleSelection />} />
            <Route path="/crew" element={<CrewMember />} />
            <Route path="/engineer" element={<LifeSupportEngineer />} />
            <Route path="/kb-manager" element={<KnowledgeBaseManager />} />
            {/* ADD ALL CUSTOM ROUTES ABOVE THE CATCH-ALL "*" ROUTE */}
            <Route path="*" element={<NotFound />} />
          </Routes>
        </BrowserRouter>
      </TooltipProvider>
    </DataProvider>
  </QueryClientProvider>
);

export default App;
