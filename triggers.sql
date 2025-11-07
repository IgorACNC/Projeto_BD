DELIMITER //


-- proibe a exclusão de um time que ainda possui jogadores cadastrados
CREATE TRIGGER trg_ImpedirExclusaoTimeComJogadores
BEFORE DELETE ON Time
FOR EACH ROW
BEGIN
    IF OLD.quant_jogadores > 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Erro: Nao e permitido excluir um time que ainda possui jogadores cadastrados!';
    END IF;
END //
DELIMITER ;

-- aumenta a quantidade de jogadores no time após a inserção de um novo jogador
DELIMITER //
CREATE TRIGGER trg_AtualizarQuantJogadoresAposInsert
AFTER INSERT ON Jogador
FOR EACH ROW
BEGIN
    UPDATE Time
    SET quant_jogadores = quant_jogadores + 1
    WHERE id_time = NEW.fk_time;
END //
DELIMITER ;

-- diminui a quantidade de jogadores no time após a exclusão de um jogador
DELIMITER //
CREATE TRIGGER trg_AtualizarQuantJogadoresAposDelete
AFTER DELETE ON Jogador
FOR EACH ROW
BEGIN
    UPDATE Time
    SET quant_jogadores = quant_jogadores - 1
    WHERE id_time = OLD.fk_time;
END //
DELIMITER ;