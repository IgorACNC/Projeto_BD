@echo off
echo Configurando e iniciando o Tomcat...

REM Configurar JAVA_HOME
set JAVA_HOME=C:\Program Files\Java\jdk-23

REM Navegar para o diretório do Tomcat
cd "C:\Users\iacnc\Downloads\apache-tomcat-9.0.109-windows-x64\apache-tomcat-9.0.109\bin"

REM Iniciar o Tomcat
echo Iniciando Tomcat...
call startup.bat

echo Tomcat iniciado! Acesse: http://localhost:8080/Projeto_BD
pause
