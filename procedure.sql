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