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

 


