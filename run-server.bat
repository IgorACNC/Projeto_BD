@echo off
echo 🚀 Iniciando Servidor Web do Brasileirao...
echo.

REM Compilar o projeto
echo 📦 Compilando projeto...
javac -cp "lib/*" -d bin -sourcepath src src/main/CrudServer.java src/main/JogadoresHandler.java src/dao/*.java src/model/*.java src/util/*.java

if %ERRORLEVEL% neq 0 (
    echo ❌ Erro na compilação!
    pause
    exit /b 1
)

echo ✅ Compilação concluída!

REM Executar o servidor
echo 🌐 Iniciando servidor na porta 8080...
echo.
echo ✅ Servidor iniciado!
echo 🌐 Acesse: http://localhost:8080
echo ⏹️  Para parar, pressione Ctrl+C
echo.

java -cp "bin;lib/*" main.CrudServer

pause
