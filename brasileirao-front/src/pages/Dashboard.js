import React from 'react';
import './Dashboard.css';

const Dashboard = () => {
  // TODO: No futuro, estes numeros podem vir de uma API (ex: /times/count)
  const stats = [
    { label: 'Times Cadastrados', value: 30, icon: '⚽' },
    { label: 'Jogadores Ativos', value: '30', icon: '🏃' },
    { label: 'Técnicos', value: 30, icon: '👨‍🏫' },
    { label: 'Estádios', value: 30, icon: '🏟️' },
  ];

  return (
    <div className="dashboard-container">
      <header className="dashboard-header">
        <h1>Dashboard Brasileirão</h1>
        <p>Visão geral do sistema de gerenciamento do campeonato</p>
      </header>
      <div className="stats-grid">
        {stats.map(stat => (
          <div key={stat.label} className="stat-card">
            <div className="stat-icon">{stat.icon}</div>
            <div className="stat-info">
              <span className="stat-value">{stat.value}</span>
              <span className="stat-label">{stat.label}</span>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};

export default Dashboard;