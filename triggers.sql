/*
 * JUSTIFICATIVA SEMÂNTICA (Trigger 01):
 * Este trigger é disparado APÓS um jogador ser deletado.
 * Ele tem duas funções essenciais para a integridade do sistema:
 * 1. ATUALIZA A TABELA DE LOGS (requisito da Etapa 05),
 * registrando quem foi removido.
 * 2. ATUALIZA A CONTAGEM de 'quant_jogadores' no time,
 * decrementando (-1) o valor (seu pedido).
 */

CREATE TABLE IF NOT EXISTS Log_Jogador_Deletado (
    id_log INT AUTO_INCREMENT PRIMARY KEY,
    id_jogador_deletado INT,
    nome_jogador VARCHAR(255),
    id_time_antigo INT,
    data_delecao TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

DELIMITER //
CREATE TRIGGER trg_ProcessarDeleteJogador
AFTER DELETE ON Jogador
FOR EACH ROW
BEGIN
    INSERT INTO Log_Jogador_Deletado (id_jogador_deletado, nome_jogador, id_time_antigo)
    VALUES (OLD.id_jogador, OLD.nome, OLD.fk_time);
    UPDATE Time
    SET quant_jogadores = quant_jogadores - 1
    WHERE id_time = OLD.fk_time;
END //
DELIMITER ;


/*
 * JUSTIFICATIVA SEMÂNTICA (Trigger 02):
 * Garante a integridade dos dados ao impedir que um time seja deletado
 * se ele ainda possui jogadores vinculados (quant_jogadores > 0).
 * É disparado ANTES de um DELETE na tabela 'Time'.
 */
DELIMITER //
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

/*
 * JUSTIFICATIVA SEMÂNTICA (Trigger 03):
 * Este é o par do Trigger 01. Ele mantém a coluna 'quant_jogadores'
 * sincronizada quando um novo jogador é adicionado ao elenco.
 * É disparado APÓS um novo jogador ser inserido e
 * incrementa (+1) a contagem no time correspondente.
 */
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

/*
 * JUSTIFICATIVA SEMÂNTICA (Trigger 04 - Novo):
 * Este é o complemento do Trigger 03. Ele mantém a coluna 'quant_jogadores'
 * sincronizada quando um jogador é removido do elenco.
 * É disparado APÓS um jogador ser deletado da tabela 'Jogador'
 * e decrementa (-1) a contagem no time ao qual o jogador pertencia.
 */
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