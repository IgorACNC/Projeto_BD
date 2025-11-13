-- Novas Consultas - Entrega 4

-- Tecnicos Desempregados (sem time)
SELECT te.nome as tecnico_nome, te.idade, te.quant_time_treinou
FROM Tecnico te
LEFT JOIN Time t ON te.id_tecnico = t.fk_tecnico
WHERE t.fk_tecnico IS NULL
ORDER BY te.quant_time_treinou DESC, te.idade DESC;

-- Partidas e seus Arbitros (incluindo casos sem arbitro ou sem partida)
(SELECT 
    p.id_partida, 
    p.time_casa, 
    p.time_fora, 
    a.nome AS arbitro_nome
FROM Partida p
LEFT JOIN Apita ap ON p.id_partida = ap.fk_partida
LEFT JOIN Arbitro a ON ap.fk_arbitro = a.id_arbitro)
UNION
(SELECT 
    p.id_partida, 
    p.time_casa, 
    p.time_fora, 
    a.nome AS arbitro_nome
FROM Arbitro a
RIGHT JOIN Apita ap ON a.id_arbitro = ap.fk_arbitro
RIGHT JOIN Partida p ON ap.fk_partida = p.id_partida
WHERE p.id_partida IS NULL OR a.id_arbitro IS NULL);

-- Jogadores em Times com Estádios com Capacidade Maior que 60.000
SELECT 
    j.nome AS jogador_nome,
    j.posicao_jogador,
    t.nome AS time_nome
FROM Jogador j
JOIN Time t ON j.fk_time = t.id_time
WHERE t.fk_estadio IN (
    SELECT id_estadio
    FROM Estadio
    WHERE capacidade > 60000
);

-- Listar todos os times e incluir uma coluna que mostra o artilheiro
SELECT 
    t.nome AS time_nome,
    t.quant_socios,
    (SELECT j.nome 
     FROM Jogador j 
     WHERE j.fk_time = t.id_time 
     ORDER BY j.gols_temporada_jogador DESC 
     LIMIT 1) AS artilheiro_do_time,
    (SELECT MAX(j.gols_temporada_jogador)
     FROM Jogador j 
     WHERE j.fk_time = t.id_time) AS gols_artilheiro
FROM Time t
ORDER BY t.nome;