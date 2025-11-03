-- Tudo de Jogador
SELECT * FROM Jogador;

-- Tudo de Time
SELECT * FROM Time;

-- Time com Estatisticas
SELECT t.nome as time_nome, 
COUNT(j.id_jogador) as total_jogadores, SUM(j.gols_temporada_jogador) as total_gols_time, AVG(j.idade) as idade_media, te.nome as tecnico_nome 
FROM Time t 
LEFT JOIN Jogador j ON t.id_time = j.fk_time 
LEFT JOIN Tecnico te ON t.fk_tecnico = te.id_tecnico 
GROUP BY t.id_time, t.nome, te.nome ORDER BY total_gols_time DESC;

-- Estatisticas por Posicao
SELECT j.posicao_jogador, 
COUNT(*) as total_jogadores, AVG(j.idade) as idade_media, AVG(j.altura) as altura_media, SUM(j.gols_temporada_jogador) as total_gols, SUM(j.assistencias) as total_assistencias 
FROM Jogador j 
GROUP BY j.posicao_jogador ORDER BY total_gols DESC;

-- Times e seus Estadios
SELECT t.nome as time_nome, e.nome as estadio_nome, e.capacidade, e.bairro, e.rua, e.numero, p.nome as presidente_nome 
FROM Time t 
LEFT JOIN Estadio e ON t.fk_estadio = e.id_estadio 
LEFT JOIN Presidente p ON t.fk_presidente = p.id_presidente 
WHERE e.capacidade IS NOT NULL 
ORDER BY e.capacidade DESC;

-- Tecnicos por Experiencia
SELECT te.nome as tecnico_nome, te.idade, te.quant_time_treinou, t.nome as time_atual, p.nome as presidente_nome 
FROM Tecnico te 
LEFT JOIN Time t ON te.id_tecnico = t.fk_tecnico 
LEFT JOIN Presidente p ON t.fk_presidente = p.id_presidente 
ORDER BY te.quant_time_treinou DESC, te.idade DESC;

-- entrega 4
-- Indices
CREATE INDEX idx_jogador_posicao ON Jogador(posicao_jogador);
CREATE INDEX idx_time_nome ON Time(nome);


-- Novas Consultas

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

-- entrega 5

-- Função para classificar desempenho do jogador com base nos gols na temporada
DELIMITER $$
CREATE FUNCTION fn_ClassificarDesempenho(gols INT)
RETURNS VARCHAR(30)
DETERMINISTIC
BEGIN
    DECLARE nivel VARCHAR(30);
    
    IF gols >= 15 THEN
        SET nivel = 'Candidato a Artilheiro';
    ELSEIF gols >= 5 THEN
        SET nivel = 'Bom Desempenho';
    ELSE
        SET nivel = 'Desempenho Regular';
    END IF;
    
    RETURN nivel;
END$$
DELIMITER ;

-- participações em gols dos jogadores
-- Esta função calcula o total de participações em gols (gols + assistências) de um jogador.
DELIMITER $$
CREATE FUNCTION fn_CalcularParticipacoesGols(gols INT, assistencias INT)
RETURNS INT
DETERMINISTIC
BEGIN
    DECLARE total_participacoes INT;
    SET total_participacoes = gols + assistencias;
    RETURN total_participacoes;
END$$
DELIMITER ;

-- procedimento atualiza o técnico de um time e incrementa o contador de times treinados do novo técnico
DELIMITER $$
CREATE PROCEDURE sp_ContratarTecnico(IN time_id INT, IN novo_tecnico_id INT)
BEGIN
    UPDATE Time
    SET fk_tecnico = novo_tecnico_id
    WHERE id_time = time_id;
    
    UPDATE Tecnico
    SET quant_time_treinou = quant_time_treinou + 1
    WHERE id_tecnico = novo_tecnico_id;
END$$
DELIMITER ;

-- procedimento usa um cursor para iterar sobre os jogadores de um time e concatená-los em uma única string de saída
DELIMITER $$
CREATE PROCEDURE sp_GerarListaElenco(IN time_id INT, OUT lista_elenco TEXT)
BEGIN
    DECLARE done INT DEFAULT FALSE;
    DECLARE nome_jogador VARCHAR(100);
    DECLARE posicao_jogador VARCHAR(20);
    
    DECLARE cur_elenco CURSOR FOR 
        SELECT nome, posicao_jogador 
        FROM Jogador 
        WHERE fk_time = time_id 
        ORDER BY posicao_jogador, nome;
        
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;
    
    SET lista_elenco = '';
    
    OPEN cur_elenco;
    
    loop_jogadores: LOOP
        FETCH cur_elenco INTO nome_jogador, posicao_jogador;    
        IF done THEN
            LEAVE loop_jogadores;
        END IF;
        
        SET lista_elenco = CONCAT(lista_elenco, posicao_jogador, ': ', nome_jogador, '\n');
    END LOOP;
    
    CLOSE cur_elenco;
END$$
DELIMITER ;
