// Em: brasileirao-front/src/pages/Jogadores.js
import React, { useState, useEffect } from 'react';
import Modal from '../components/Modal/Modal';
import './Jogadores.css';

const JogadorForm = ({ jogador, onSave, onCancel }) => {
    const [formData, setFormData] = useState(jogador || {
        nome: '', idade: 0, altura: 0, numero_camisa: 0, posicao_jogador: '', fk_time: 0,
        quant_times_jogados: 1, gols_temporada_jogador: 0, pe_dominante: 'destro',
        gols_penalti: 0, gols_cabeca: 0, peso: 0, quant_jogos: 0, nacionalidade: '', assistencias: 0
    });

    useEffect(() => { if (jogador) setFormData(jogador); }, [jogador]);

    const handleChange = e => {
        const { name, value, type } = e.target;
        const valorFinal = type === 'number' ? parseFloat(value) || 0 : value;
        setFormData(prev => ({ ...prev, [name]: valorFinal }));
    };

    const handleSubmit = e => { e.preventDefault(); onSave(formData); };

    return (
        <form onSubmit={handleSubmit} className="jogador-form">
            <input name="nome" value={formData.nome} onChange={handleChange} placeholder="Nome do Jogador" required />
            <input name="nacionalidade" value={formData.nacionalidade} onChange={handleChange} placeholder="Nacionalidade" required />
            <input name="posicao_jogador" value={formData.posicao_jogador} onChange={handleChange} placeholder="Posição (Ex: Atacante)" required />
            <input name="idade" type="number" value={formData.idade} onChange={handleChange} placeholder="Idade" required />
            <input name="numero_camisa" type="number" value={formData.numero_camisa} onChange={handleChange} placeholder="Nº da Camisa" required />
            <input name="fk_time" type="number" value={formData.fk_time} onChange={handleChange} placeholder="ID do Time" required />
            <input name="altura" type="number" step="0.01" value={formData.altura} onChange={handleChange} placeholder="Altura (ex: 1.82)" required />
            <input name="peso" type="number" step="0.1" value={formData.peso} onChange={handleChange} placeholder="Peso (ex: 75.5)" required />
            <input name="quant_jogos" type="number" value={formData.quant_jogos} onChange={handleChange} placeholder="Quantidade de Jogos" required />
            <select name="pe_dominante" value={formData.pe_dominante} onChange={handleChange}><option value="destro">Destro</option><option value="canhoto">Canhoto</option></select>
            <input name="gols_temporada_jogador" type="number" value={formData.gols_temporada_jogador} onChange={handleChange} placeholder="Gols na Temporada" />
            <input name="assistencias" type="number" value={formData.assistencias} onChange={handleChange} placeholder="Assistências" />
            <div className="form-actions">
                <button type="button" onClick={onCancel}>Cancelar</button>
                <button type="submit">Salvar</button>
            </div>
        </form>
    );
};

const Jogadores = () => {
    const [jogadores, setJogadores] = useState([]);
    const [termoBusca, setTermoBusca] = useState("");
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [jogadorEditando, setJogadorEditando] = useState(null);

    const fetchJogadores = () => {
        fetch('http://localhost:8080/jogadores')
            .then(res => res.json()).then(setJogadores)
            .catch(err => console.error("Erro ao buscar jogadores:", err));
    };

    useEffect(() => { fetchJogadores(); }, []);

    const handleSalvar = (jogador) => {
        const isEditing = !!jogador.id_jogador;
        const metodo = isEditing ? 'PUT' : 'POST';
        const url = isEditing ? `http://localhost:8080/jogadores/atualizar/${jogador.id_jogador}` : 'http://localhost:8080/jogadores';

        fetch(url, { method: metodo, headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(jogador) })
            .then(res => res.json())
            .then(() => { fetchJogadores(); setIsModalOpen(false); });
    };

    const handleExcluir = (id) => {
        if (window.confirm("Tem certeza?")) {
            fetch(`http://localhost:8080/jogadores/excluir/${id}`, { method: 'DELETE' })
                .then(res => res.json()).then(() => fetchJogadores());
        }
    };

    const jogadoresFiltrados = jogadores.filter(j =>
        (j.nome && j.nome.toLowerCase().includes(termoBusca.toLowerCase())) ||
        (j.posicao_jogador && j.posicao_jogador.toLowerCase().includes(termoBusca.toLowerCase()))
    );

    return (
        <div className="page-container">
            <Modal isOpen={isModalOpen} onClose={() => setIsModalOpen(false)} title={jogadorEditando ? "Editar Jogador" : "Adicionar Jogador"}>
                <JogadorForm onCancel={() => setIsModalOpen(false)} onSave={handleSalvar} jogador={jogadorEditando} />
            </Modal>
            <div className="page-header">
                <div><h1>Jogadores</h1><p>Gestão dos atletas do campeonato</p></div>
                <button className="add-button" onClick={() => { setJogadorEditando(null); setIsModalOpen(true); }}>+ Novo Jogador</button>
            </div>
            <div className="search-bar">
                <input type="text" placeholder="🔍 Buscar por nome ou posição..." value={termoBusca} onChange={e => setTermoBusca(e.target.value)} />
            </div>
            <div className="table-wrapper">
                <table className="data-table">
                    <thead><tr><th>Nome</th><th>Time (ID)</th><th>Posição</th><th>Nº</th><th>Idade</th><th>Gols</th><th>Assist.</th><th>Ações</th></tr></thead>
                    <tbody>
                        {jogadoresFiltrados.map(jogador => (
                            <tr key={jogador.id_jogador}>
                                <td>{jogador.nome}</td><td>{jogador.fk_time}</td>
                                <td><span className="tag">{jogador.posicao_jogador}</span></td><td>{jogador.numero_camisa}</td>
                                <td>{jogador.idade}</td><td><span className="gols">{jogador.gols_temporada_jogador}</span></td>
                                <td>{jogador.assistencias}</td>
                                <td className="action-buttons">
                                    <button title="Editar" onClick={() => { setJogadorEditando(jogador); setIsModalOpen(true); }}>✏️</button>
                                    <button title="Excluir" onClick={() => handleExcluir(jogador.id_jogador)}>🗑️</button>
                                </td>
                            </tr>
                        ))}
                    </tbody>
                </table>
            </div>
        </div>
    );
};

export default Jogadores;