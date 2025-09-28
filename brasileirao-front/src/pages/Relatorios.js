import React, { useState } from 'react';
import './Relatorios.css'; // A importação do CSS corrige o estilo dos botões

// Mapeamento dos cabeçalhos para cada tipo de relatório
const CABECALHOS_RELATORIOS = {
    times_estatisticas: ["Time", "Total de Jogadores", "Total de Gols", "Idade Média", "Técnico"],
    estatisticas_posicao: ["Posição", "Total de Jogadores", "Idade Média", "Altura Média", "Total de Gols", "Total de Assistências"],
    times_estadios: ["Time", "Estádio", "Capacidade", "Bairro", "Endereço", "Presidente"],
    tecnicos_experiencia: ["Técnico", "Idade", "Times Treinados", "Time Atual", "Presidente"],
    jogadores_nacionalidade: ["Nacionalidade", "Total de Jogadores", "Idade Média", "Total Gols", "Média Gols/Jogador"],
};

const Relatorios = () => {
    const [dadosRelatorio, setDadosRelatorio] = useState([]);
    const [cabecalhos, setCabecalhos] = useState([]);
    const [tituloRelatorio, setTituloRelatorio] = useState("");
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);

    const buscarRelatorio = (tipo, titulo) => {
        setLoading(true);
        setError(null);
        setTituloRelatorio(titulo);
        setDadosRelatorio([]);
        setCabecalhos(CABECALHOS_RELATORIOS[tipo] || []);

        fetch(`http://localhost:8080/relatorios?type=${tipo}`)
            .then(res => {
                if (!res.ok) { throw new Error('Falha na resposta da rede'); }
                return res.json();
            })
            .then(data => {
                setDadosRelatorio(data);
                setLoading(false);
            })
            .catch(err => {
                console.error("Erro ao buscar relatorio:", err);
                setError("Não foi possível carregar os dados do relatório.");
                setLoading(false);
            });
    };

    const relatoriosDisponiveis = [
        { id: 'times_estatisticas', titulo: 'Estatísticas dos Times', desc: 'Resumo dos clubes e jogadores' },
        { id: 'estatisticas_posicao', titulo: 'Jogadores por Posição', desc: 'Distribuição por posições em campo' },
        { id: 'times_estadios', titulo: 'Times e seus Estádios', desc: 'Informações sobre os estádios' },
        { id: 'tecnicos_experiencia', titulo: 'Técnicos Experientes', desc: 'Treinadores por experiência' },
        { id: 'jogadores_nacionalidade', titulo: 'Jogadores por Nacionalidade', desc: 'Distribuição por país de origem' },
    ];

    return (
        <div className="page-container">
            <div className="page-header">
                <div><h1>Relatórios</h1><p>Análises e estatísticas do Brasileirão</p></div>
            </div>

            <div className="relatorios-grid">
                {relatoriosDisponiveis.map(rel => (
                    <button key={rel.id} className="report-card" onClick={() => buscarRelatorio(rel.id, rel.titulo)}>
                        <strong>{rel.titulo}</strong>
                        <span>{rel.desc}</span>
                    </button>
                ))}
            </div>

            {tituloRelatorio && (
                <div className="report-view">
                    <h2>{tituloRelatorio}</h2>
                    {loading && <p>Carregando...</p>}
                    {error && <p style={{color: 'red'}}>{error}</p>}
                    {!loading && !error && (
                        <div className="table-wrapper">
                            <table className="data-table">
                                <thead>
                                    <tr>{cabecalhos.map((th, i) => <th key={`header-${i}`}>{th}</th>)}</tr>
                                </thead>
                                <tbody>
                                    {dadosRelatorio.length > 0 ? (
                                        dadosRelatorio.map((linha, i) => (
                                            <tr key={`row-${i}`}>
                                                {linha.map((td, j) => <td key={`cell-${i}-${j}`}>{td}</td>)}
                                            </tr>
                                        ))
                                    ) : (
                                        <tr><td colSpan={cabecalhos.length}>Nenhum dado encontrado para este relatório.</td></tr>
                                    )}
                                </tbody>
                            </table>
                        </div>
                    )}
                </div>
            )}
        </div>
    );
};

export default Relatorios;