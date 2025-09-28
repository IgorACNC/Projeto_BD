import React from 'react';
import { NavLink } from 'react-router-dom';
import './Sidebar.css';

const Sidebar = () => {
  return (
    <div className="sidebar">
      <div className="sidebar-header">
        <h2>Brasileirão</h2>
        <span>Sistema de Gestão</span>
      </div>
      <nav className="sidebar-nav">
        <p>NAVEGAÇÃO PRINCIPAL</p>
        <NavLink to="/" end>Dashboard</NavLink>
        <NavLink to="/times">Times</NavLink>
        <NavLink to="/jogadores">Jogadores</NavLink>
        <NavLink to="/relatorios">Relatórios</NavLink>
        <NavLink to="/graficos">Gráficos</NavLink>
      </nav>
    </div>
  );
};

export default Sidebar;