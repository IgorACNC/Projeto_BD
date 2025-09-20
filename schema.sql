CREATE DATABASE IF NOT EXISTS Brasileirao_serie_A;
USE Brasileirao_serie_A;

CREATE TABLE IF NOT EXISTS Tecnico(
    id_tecnico INT PRIMARY KEY AUTO_INCREMENT,
    nome VARCHAR(100) NOT NULL,
    idade INT NOT NULL,
    quant_time_treinou INT NOT NULL DEFAULT 1 
);
CREATE TABLE IF NOT EXISTS Presidente(
    id_presidente INT PRIMARY KEY AUTO_INCREMENT,
    nome VARCHAR(100) NOT NULL,
    idade INT NOT NULL,
    quant_titulo INT NOT NULL DEFAULT 0,
    tempo_cargo INT NOT NULL DEFAULT 1
);

CREATE TABLE IF NOT EXISTS Estadio(
    id_estadio INT PRIMARY KEY AUTO_INCREMENT,
    nome VARCHAR(100) NOT NULL,
    capacidade INT NOT NULL,
    rua VARCHAR(100) NOT NULL,
    numero INT NOT NULL,
    bairro VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS Time(
    id_time INT PRIMARY KEY AUTO_INCREMENT,
    nome VARCHAR(100) NOT NULL,
    quant_jogadores INT NOT NULL,
    quant_socios INT NOT NULL,
    fk_tecnico INT,
    fk_presidente INT,
    fk_estadio INT,
    FOREIGN KEY (fk_tecnico) REFERENCES Tecnico(id_tecnico),
    FOREIGN KEY (fk_presidente) REFERENCES Presidente(id_presidente),
    FOREIGN KEY (fk_estadio) REFERENCES Estadio(id_estadio)
);

CREATE TABLE IF NOT EXISTS Jogador(
    id_jogador INT PRIMARY KEY AUTO_INCREMENT,
    nome VARCHAR(100) NOT NULL,
    idade INT NOT NULL,
    altura FLOAT NOT NULL,
    numero_camisa INT NOT NULL,
    quant_times_jogados INT NOT NULL DEFAULT 1,
    gols_temporada_jogador INT NOT NULL DEFAULT 0,
    pe_dominante ENUM('destro','canhoto') NOT NULL,
    gols_penalti INT NOT NULL DEFAULT 0,
    gols_cabeca INT NOT NULL DEFAULT 0,
    peso FLOAT NOT NULL,
    posicao_jogador ENUM('Goleiro','Lateral','Zagueiro','Volante','Meia','Atacante') NOT NULL,
    quant_jogos INT NOT NULL,
    nacionalidade VARCHAR(50) NOT NULL,
    assistencias INT NOT NULL DEFAULT 0,
    fk_time INT,
    FOREIGN KEY (fk_time) REFERENCES Time (id_time)
);

CREATE TABLE IF NOT EXISTS Rival(
    fk_rivaliza INT,
    fk_rivalizado INT,
    FOREIGN KEY (fk_rivaliza) REFERENCES Time(id_time),
    FOREIGN KEY (fk_rivalizado) REFERENCES Time(id_time)
);

CREATE TABLE IF NOT EXISTS Partida(
    id_partida INT PRIMARY KEY AUTO_INCREMENT,
    time_casa VARCHAR(100) NOT NULL,
    time_fora VARCHAR(100) NOT NULL,
    resultado VARCHAR(10) NOT NULL
);

CREATE TABLE IF NOT EXISTS Disputa(
    fk_time INT,
    fk_partida INT,
    FOREIGN KEY (fk_time) REFERENCES Time(id_time),
    FOREIGN KEY (fk_partida) REFERENCES Partida(id_partida)
);

CREATE TABLE IF NOT EXISTS Participa(
    id_jogo INT PRIMARY KEY AUTO_INCREMENT,
    tempo_jogado INT NOT NULL DEFAULT 0,
    gols_no_jogo INT NOT NULL DEFAULT 0,
    assistencias_no_jogo INT NOT NULL DEFAULT 0,
    passes_dados INT NOT NULL DEFAULT 0,
    cartoes_sofridos INT NOT NULL DEFAULT 0,
    fk_jogador INT,
    FOREIGN KEY (fk_jogador) REFERENCES Jogador(id_jogador)
);

CREATE TABLE IF NOT EXISTS Arbitro(
    id_arbitro INT PRIMARY KEY AUTO_INCREMENT,
    nome VARCHAR(100) NOT NULL,
    idade INT NOT NULL,
    media_cartoes INT NOT NULL,
    media_cartoes INT NOT NULL
);

CREATE TABLE IF NOT EXISTS Apita(
    fk_arbitro INT,
    fk_partida INT,
    FOREIGN KEY (fk_arbitro) REFERENCES Arbitro(id_arbitro),
    FOREIGN KEY (fk_partida) REFERENCES Partida(id_partida)
);