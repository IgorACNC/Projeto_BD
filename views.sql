CREATE VIEW vw_JogadorDetalhado AS
SELECT 
    j.nome AS jogador_nome,
    j.posicao_jogador,
    j.idade AS jogador_idade,
    j.nacionalidade,
    j.gols_temporada_jogador,
    j.assistencias,
    t.nome AS time_nome,
    te.nome AS tecnico_nome,
    p.nome AS presidente_nome
FROM Jogador j
JOIN Time t ON j.fk_time = t.id_time
LEFT JOIN Tecnico te ON t.fk_tecnico = te.id_tecnico
LEFT JOIN Presidente p ON t.fk_presidente = p.id_presidente;

CREATE VIEW vw_InfraestruturaTime AS
SELECT
    t.nome AS time_nome,
    t.quant_jogadores,
    t.quant_socios,
    p.nome AS presidente_nome,
    p.tempo_cargo AS meses_no_cargo,
    te.nome AS tecnico_nome,
    te.quant_time_treinou,
    e.nome AS estadio_nome,
    e.capacidade,
    e.bairro
FROM Time t
LEFT JOIN Presidente p ON t.fk_presidente = p.id_presidente
LEFT JOIN Tecnico te ON t.fk_tecnico = te.id_tecnico
LEFT JOIN Estadio e ON t.fk_estadio = e.id_estadio;