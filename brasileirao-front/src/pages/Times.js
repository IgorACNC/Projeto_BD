// Em: brasileirao-front/src/pages/Times.js
import React, { useState, useEffect } from 'react';
import Modal from '../components/Modal/Modal';
import './Times.css';

const TimeForm = ({ time, onSave, onCancel }) => {
    const [formData, setFormData] = useState(time || {
        nome: '', quant_jogadores: 25, quant_socios: 0
    });

    useEffect(() => { if (time) setFormData(time); }, [time]);

    const handleChange = e => {
        const { name, value, type } = e.target;
        setFormData(prev => ({ ...prev, [name]: type === 'number' ? parseInt(value, 10) : value }));
    };
    
    const handleSubmit = e => { e.preventDefault(); onSave(formData); };

    return (
        <form onSubmit={handleSubmit} className="jogador-form">
            <input name="nome" value={formData.nome} onChange={handleChange} placeholder="Nome do Clube" required />
            <input name="quant_jogadores" type="number" value={formData.quant_jogadores} onChange={handleChange} placeholder="Nº de Jogadores" required />
            <input name="quant_socios" type="number" value={formData.quant_socios} onChange={handleChange} placeholder="Nº de Sócios" required />
            <div className="form-actions">
                <button type="button" onClick={onCancel}>Cancelar</button>
                <button type="submit">Salvar</button>
            </div>
        </form>
    );
};

const Times = () => {
    const [times, setTimes] = useState([]);
    const [termoBusca, setTermoBusca] = useState("");
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [timeEditando, setTimeEditando] = useState(null);

    const fetchTimes = () => {
        fetch('http://localhost:8080/times')
            .then(res => res.json()).then(setTimes)
            .catch(err => console.error("Erro ao buscar times:", err));
    };

    useEffect(() => { fetchTimes(); }, []);

    const handleExcluir = (id) => {
        if (window.confirm("Tem certeza que deseja excluir este time?")) {
            fetch(`http://localhost:8080/times/excluir/${id}`, { method: 'DELETE' })
            .then(() => fetchTimes());
        }
    };

    const handleSalvar = (time) => {
        const isEditing = !!time.id_time;
        const metodo = isEditing ? 'PUT' : 'POST';
        const url = isEditing ? `http://localhost:8080/times/atualizar/${time.id_time}` : 'http://localhost:8080/times';

        fetch(url, {
            method: metodo,
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(time)
        })
        .then(() => {
            fetchTimes();
            setIsModalOpen(false);
        });
    };

    const timesFiltrados = times.filter(t => t.nome.toLowerCase().includes(termoBusca.toLowerCase()));

    return (
        <div className="page-container">
            <Modal isOpen={isModalOpen} onClose={() => setIsModalOpen(false)} title={timeEditando ? "Editar Time" : "Adicionar Time"}>
                <TimeForm onCancel={() => setIsModalOpen(false)} onSave={handleSalvar} time={timeEditando} />
            </Modal>
            <div className="page-header">
                <div><h1>Times</h1><p>Gestão dos clubes do campeonato</p></div>
                <button className="add-button" onClick={() => { setTimeEditando(null); setIsModalOpen(true); }}>+ Novo Time</button>
            </div>
            <div className="search-bar">
                <input type="text" placeholder="🔍 Buscar por nome do time..." value={termoBusca} onChange={e => setTermoBusca(e.target.value)} />
            </div>
            <div className="table-wrapper">
                <table className="data-table">
                    <thead><tr><th>Nome do Clube</th><th>Jogadores</th><th>Sócios</th><th>Ações</th></tr></thead>
                    <tbody>
                        {timesFiltrados.map(time => (
                            <tr key={time.id_time}>
                                <td><strong>{time.nome}</strong></td>
                                <td>{time.quant_jogadores}</td>
                                <td>{time.quant_socios}</td>
                                <td className="action-buttons">
                                    <button title="Editar" onClick={() => { setTimeEditando(time); setIsModalOpen(true); }}>✏️</button>
                                    <button title="Excluir" onClick={() => handleExcluir(time.id_time)}>🗑️</button>
                                </td>
                            </tr>
                        ))}
                    </tbody>
                </table>
            </div>
        </div>
    );
};

export default Times;