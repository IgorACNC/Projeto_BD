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