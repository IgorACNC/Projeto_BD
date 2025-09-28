-- Arquivo: JogadorDAO.java
SELECT * FROM Jogador;

SELECT j.id_jogador, j.nome as jogador_nome, j.posicao_jogador, j.numero_camisa, j.gols_temporada_jogador, j.assistencias, j.nacionalidade, t.nome as time_nome, t.quant_jogadores FROM Jogador j LEFT JOIN Time t ON j.fk_time = t.id_time ORDER BY j.gols_temporada_jogador DESC;
SELECT j.nome as jogador_nome, j.gols_temporada_jogador, j.posicao_jogador, t.nome as time_nome, j.nacionalidade FROM Jogador j LEFT JOIN Time t ON j.fk_time = t.id_time WHERE j.gols_temporada_jogador > 0 ORDER BY j.gols_temporada_jogador DESC, j.assistencias DESC LIMIT 10;

-- Arquivo: RelatorioDAO.java
SELECT t.nome as time_nome, 
COUNT(j.id_jogador) as total_jogadores, SUM(j.gols_temporada_jogador) as total_gols_time, AVG(j.idade) as idade_media, te.nome as tecnico_nome 
FROM Time t 
LEFT JOIN Jogador j ON t.id_time = j.fk_time 
LEFT JOIN Tecnico te ON t.fk_tecnico = te.id_tecnico 
GROUP BY t.id_time, t.nome, te.nome ORDER BY total_gols_time DESC;

SELECT j.posicao_jogador, 
COUNT(*) as total_jogadores, AVG(j.idade) as idade_media, AVG(j.altura) as altura_media, SUM(j.gols_temporada_jogador) as total_gols, SUM(j.assistencias) as total_assistencias 
FROM Jogador j 
GROUP BY j.posicao_jogador ORDER BY total_gols DESC;

SELECT t.nome as time_nome, e.nome as estadio_nome, e.capacidade, e.bairro, e.rua, e.numero, p.nome as presidente_nome 
FROM Time t 
LEFT JOIN Estadio e ON t.fk_estadio = e.id_estadio 
LEFT JOIN Presidente p ON t.fk_presidente = p.id_presidente 
WHERE e.capacidade IS NOT NULL 
ORDER BY e.capacidade DESC;

SELECT te.nome as tecnico_nome, te.idade, te.quant_time_treinou, t.nome as time_atual, p.nome as presidente_nome 
FROM Tecnico te 
LEFT JOIN Time t ON te.id_tecnico = t.fk_tecnico 
LEFT JOIN Presidente p ON t.fk_presidente = p.id_presidente 
ORDER BY te.quant_time_treinou DESC, te.idade DESC;

SELECT j.nacionalidade, 
COUNT(*) as total_jogadores, AVG(j.idade) as idade_media, SUM(j.gols_temporada_jogador) as total_gols, AVG(j.gols_temporada_jogador) as media_gols_por_jogador 
FROM Jogador j 
GROUP BY j.nacionalidade ORDER BY total_jogadores DESC, total_gols DESC;

-- Arquivo: TimeDAO.java
SELECT * FROM Time;

SELECT t.id_time, t.nome as time_nome, t.quant_jogadores, t.quant_socios, te.nome as tecnico_nome, p.nome as presidente_nome, e.nome as estadio_nome, e.capacidade
FROM Time t
LEFT JOIN Tecnico te ON t.fk_tecnico = te.id_tecnico
LEFT JOIN Presidente p ON t.fk_presidente = p.id_presidente
LEFT JOIN Estadio e ON t.fk_estadio = e.id_estadio;
