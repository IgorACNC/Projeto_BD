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

Siga estes passos para configurar o ambiente e deixar o projeto pronto para ser executado.

### Passo 1: Configurar o Banco de Dados

Você precisa criar o banco de dados e as tabelas, e depois popular com os dados iniciais.

1.  Abra seu cliente MySQL e conecte-se ao seu servidor de banco de dados.
2.  Execute o script para criar o banco e as tabelas, que está no arquivo `schema.sql`.
3.  Execute o script para inserir os dados iniciais nas tabelas, que está no arquivo `data.sql`.

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

### Passo 3: Configurar a Biblioteca do MySQL (JDBC Driver)

> **Nota Importante:** Este passo é crucial se você for executar o projeto a partir de uma IDE (VS Code, Eclipse, IntelliJ). Se você usar apenas o script `run-server.bat`, ele já cuida disso para você.

A aplicação Java precisa do "driver" JDBC para se comunicar com o MySQL.

1.  No seu projeto, dentro da sua IDE, acesse as configurações de `Build Path` ou `Libraries`.
2.  Adicione o arquivo `.jar` que está na pasta `lib/` do projeto (`mysql-connector-j-9.4.0.jar`) como uma biblioteca externa.
3.  Isso garante que a IDE consiga encontrar as classes de conexão com o MySQL durante a execução.

### Passo 4: Compilar e Executar o Servidor

O projeto inclui um script que automatiza a compilação e execução do servidor.

1.  Abra um terminal ou prompt de comando na pasta raiz do projeto.
2.  Execute o seguinte comando:

    **No Windows:**
    ```bat
    .\run-server.bat
    ```

    **No Linux ou macOS:**
    ```bash
    # Dê permissão de execução (apenas na primeira vez)
    chmod +x run-server.sh 
    
    # Execute o script
    ./run-server.sh 
    ```
    *(Nota: `run-server.sh` é a versão para Linux/macOS do `.bat`)*.


Se tudo correr bem, você verá mensagens indicando que o servidor foi iniciado com sucesso.

---

## Acessando a Aplicação

Com o servidor rodando, abra seu navegador de internet e acesse o seguinte endereço:

[http://localhost:8080](http://localhost:8080)

Você verá a página inicial do sistema de gerenciamento. Para parar o servidor, volte para a janela do terminal e pressione `Ctrl + C`.
