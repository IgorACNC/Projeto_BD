import React from 'react';
import { BrowserRouter, Routes, Route } from 'react-router-dom';
import Sidebar from './components/Sidebar';
import Dashboard from './pages/Dashboard';
import Jogadores from './pages/Jogadores';
import Relatorios from './pages/Relatorios';
import Graficos from './pages/Graficos';
import Times from './pages/Times';
import './App.css';

function App() {
  return (
    <BrowserRouter>
      <div className="app-container">
        <Sidebar />
        <main className="main-content">
          <Routes>
            <Route path="/" element={<Dashboard />} />
            <Route path="/jogadores" element={<Jogadores />} />
            <Route path="/times" element={<Times />} />
            <Route path="/relatorios" element={<Relatorios />} />
            <Route path="/graficos" element={<Graficos />} />
          </Routes>
        </main>
      </div>
    </BrowserRouter>
  );
}

export default App;