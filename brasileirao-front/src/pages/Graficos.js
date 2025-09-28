import React from 'react';
import './Graficos.css';

const listaDeGraficos = [
    { titulo: "Gráfico 1: Estatísticas por Posição", descricao: "Análise da distribuição e performance média dos jogadores.", imagemSrc: "http://localhost:8080/public/images/figura1.png" },
    { titulo: "Gráfico 2: Ranking de Artilheiros", descricao: "Top 10 maiores artilheiros do campeonato.", imagemSrc: "http://localhost:8080/public/images/figura2.png" },
    { titulo: "Gráfico 3: Desempenho dos Times", descricao: "Comparativo de vitórias, derrotas e empates.", imagemSrc: "http://localhost:8080/public/images/figura3.png" },
    { titulo: "Gráfico 4: Outra Análise", descricao: "Descrição do seu quarto gráfico.", imagemSrc: "http://localhost:8080/public/images/figura4.png" },
    { titulo: "Gráfico 5: Mais Dados", descricao: "Descrição do seu quinto gráfico.", imagemSrc: "http://localhost:8080/public/images/figura5.png" },
    { titulo: "Gráfico 6: Performance Defensiva", descricao: "Análise de gols sofridos e defesas.", imagemSrc: "http://localhost:8080/public/images/figura6.png" },
    // Adicione os objetos para as figuras 7 a 14 aqui...
];

const Graficos = () => {
    return (
        <div className="page-container">
            <div className="page-header">
                <div>
                    <h1>Gráficos</h1>
                    <p>Visualização de dados e estatísticas do campeonato</p>
                </div>
            </div>
            <div className="grid-graficos">
                {listaDeGraficos.map((grafico, index) => (
                    <div className="grafico-card" key={index}>
                        <h2>{grafico.titulo}</h2>
                        <p>{grafico.descricao}</p>
                        <div className="grafico-imagem">
                            <img src={grafico.imagemSrc} alt={grafico.titulo} />
                        </div>
                    </div>
                ))}
            </div>
        </div>
    );
};

export default Graficos;