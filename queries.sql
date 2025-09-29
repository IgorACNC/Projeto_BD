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
