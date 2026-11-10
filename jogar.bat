@echo off
chcp 65001 >nul
cd /d "%~dp0"

if exist "target\vant-game.jar" goto jogar

echo Compilando o jogo...
call mvn -q -DskipTests package
if not errorlevel 1 goto jogar

echo.
echo Maven falhou. Tentando compilar com javac...
if not exist "out" mkdir out
javac -encoding UTF-8 -d out src\main\java\dev\vant\game\*.java
if errorlevel 1 goto erro
java -cp out dev.vant.game.VantGame
goto fim

:jogar
java -jar "target\vant-game.jar"
goto fim

:erro
echo.
echo Nao foi possivel compilar o jogo.

:fim
echo.
pause
