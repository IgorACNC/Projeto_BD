@echo off
title Servidor Brasileirao CRUD

echo ======================================================
echo      INICIANDO SERVIDOR WEB DO BRASILEIRAO...
echo ======================================================
echo.

REM --- ETAPA 1: COMPILACAO DO PROJETO ---
echo Compilando todos os arquivos .java do projeto...

REM Este comando encontra e compila TODOS os arquivos .java dentro da pasta 'src'.
dir /s /B src\*.java > sources.txt
javac -cp "lib/*" -d bin @sources.txt

REM Verifica se a compilacao teve algum erro.
if %ERRORLEVEL% neq 0 (
    echo.
    echo ERRO NA COMPILACAO! Verifique as mensagens de erro acima.
    del sources.txt
    pause
    exit /b 1
)

del sources.txt
echo Compilacao concluida com sucesso!
echo.

REM --- ETAPA 2: EXECUCAO DO SERVIDOR ---
echo Iniciando o servidor na porta 8080...
echo.
echo ======================================================
echo Servidor iniciado!
echo Acesse: http://localhost:8080
echo Para parar o servidor, pressione Ctrl+C nesta janela.
echo ======================================================
echo.

REM Executa a classe principal do servidor.
java -cp "bin;lib/*" main.CrudServer

echo.
echo Servidor finalizado.
pause