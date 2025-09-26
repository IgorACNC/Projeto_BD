<h1 align="center">Projeto de Gerenciamento do Brasileirão</h1>

<p align="center">
  Um sistema CRUD (Criar, Ler, Atualizar, Deletar) em Java para gerenciar dados de times e jogadores do campeonato brasileiro, rodando em um servidor web local embutido.
</p>

---

## Pré-requisitos

Antes de começar, certifique-se de que você tem os seguintes softwares instalados em sua máquina:

1.  **Java Development Kit (JDK)**: Versão 8 ou superior.
2.  **MySQL Server**: O banco de dados onde as informações serão armazenadas.
3.  **Um cliente MySQL** (Opcional, mas recomendado): Como DBeaver, MySQL Workbench ou o cliente de linha de comando do MySQL para facilitar a configuração do banco.

---

## Configuração do Projeto

Siga estes 3 passos para configurar o ambiente e deixar o projeto pronto para ser executado.

### Passo 1: Configurar o Banco de Dados

Você precisa criar o banco de dados e as tabelas, e depois popular com os dados iniciais.

1.  Abra seu cliente MySQL e conecte-se ao seu servidor de banco de dados.
2.  Execute o script para criar o banco e as tabelas. O conteúdo do arquivo `schema.sql` está abaixo:

    ```sql
    -- Copie e cole este comando no seu cliente MySQL
    CREATE DATABASE IF NOT EXISTS Brasileirao_serie_A;
    USE Brasileirao_serie_A;

    CREATE TABLE IF NOT EXISTS Time(
        id_time INT PRIMARY KEY AUTO_INCREMENT,
        nome VARCHAR(100) NOT NULL,
        quant_jogadores INT NOT NULL DEFAULT 11,
        quant_socios INT NOT NULL DEFAULT 0
    );

    CREATE TABLE IF NOT EXISTS Jogador(
        id_jogador INT PRIMARY KEY AUTO_INCREMENT,
        nome VARCHAR(100) NOT NULL,
        idade INT NOT NULL,
        altura FLOAT NOT NULL,
        numero_camisa INT NOT NULL,
        quant_times_jogados INT NOT NULL DEFAULT 1,
        gols_temporada_jogador INT NOT NULL DEFAULT 0,
        pe_dominante VARCHAR(10) NOT NULL CHECK (pe_dominante IN ('destro','canhoto')),
        gols_penalti INT NOT NULL DEFAULT 0,
        gols_cabeca INT NOT NULL DEFAULT 0,
        peso FLOAT NOT NULL,
        posicao_jogador VARCHAR(20) NOT NULL CHECK (posicao_jogador IN ('Goleiro','Lateral','Zagueiro','Volante','Meia','Atacante')),
        quant_jogos INT NOT NULL,
        nacionalidade VARCHAR(50) NOT NULL,
        assistencias INT NOT NULL DEFAULT 0,
        fk_time INT,
        FOREIGN KEY (fk_time) REFERENCES Time (id_time) ON UPDATE CASCADE
    );

    -- (E as outras tabelas que você decidir manter)
    ```

3.  Agora, execute o script para inserir os dados iniciais nas tabelas. O conteúdo está no arquivo `data.sql`.

### Passo 2: Configurar a Conexão com o Banco

O projeto precisa saber como se conectar ao seu banco de dados.

1.  Abra o arquivo: `src/util/ConnectionFactory.java`.
2.  Altere os valores das constantes `URL`, `USER` e `PASS` para corresponderem à configuração do seu MySQL.

    ```java
    // src/util/ConnectionFactory.java

    public class ConnectionFactory {
        // Altere esta linha se o nome do seu banco ou a porta forem diferentes
        private static final String URL = "jdbc:mysql://localhost:3306/Brasileirao_serie_A";
        
        // Altere para o seu usuario do MySQL (geralmente "root")
        private static final String USER = "root"; 
        
        // MUITO IMPORTANTE: Altere para a senha do seu usuario root do MySQL
        private static final String PASS = "sua_senha_aqui"; 
    
        // ... resto do código ...
    }
    ```

### Passo 3: Compilar e Executar o Servidor

O projeto inclui um script que automatiza a compilação e execução do servidor.

1.  Abra um terminal ou prompt de comando na pasta raiz do projeto (onde o arquivo `run-server.bat` está localizado).
2.  Execute o seguinte comando:

    **No Windows:**
    ```bat
    .\run-server.bat
    ```

    **No Linux ou macOS:**
    ```bash
    # Dê permissão de execução ao script (apenas na primeira vez)
    chmod +x run-server.sh 
    
    # Execute o script
    ./run-server.sh 
    ```
    *(Nota: Se você não tiver um `run-server.sh`, pode criar um com os comandos equivalentes do `.bat` ou executar os comandos manualmente).*


Se tudo correr bem, você verá mensagens indicando que o servidor foi iniciado com sucesso.

---

## Acessando a Aplicação

Com o servidor rodando, abra seu navegador de internet e acesse o seguinte endereço:

[http://localhost:8080](http://localhost:8080)

Você verá a página inicial do sistema de gerenciamento, de onde poderá navegar para as seções de Times, Jogadores e Relatórios.

Para parar o servidor, volte para a janela do terminal onde ele está rodando e pressione `Ctrl + C`.
