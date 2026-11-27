@echo off
setlocal
title AquaSentinel - Lancement global
REM ============================================================
REM  A placer dans le dossier Backend (a cote de serveur-eureka, etc.)
REM  Ordre : eureka (instance unique) -> 7 services -> passerelle (config ignore)
REM ============================================================

set "ROOT=%~dp0"
set "EUREKA_PORT=8761"
REM  EUREKA_PEERS=0 : une seule instance Eureka (8761)
REM  EUREKA_PEERS=1 : deux instances repliquees (peer1 8761 + peer2 8762)
set "EUREKA_PEERS=1"
set "MAX_WAIT=120"

echo.
echo === Verification de Java et Maven ===
where mvn >nul 2>nul || (echo [ERREUR] Maven introuvable dans le PATH. & pause & exit /b 1)
where java >nul 2>nul || (echo [ERREUR] Java introuvable dans le PATH. & pause & exit /b 1)

echo.
echo === 1/3 Eureka Server ===
if "%EUREKA_PEERS%"=="1" goto eureka_peers

echo Mode : instance unique sur le port %EUREKA_PORT%
start "AquaSentinel-serveur-eureka" /D "%ROOT%serveur-eureka" cmd /k "set SERVER_PORT=%EUREKA_PORT%&& set EUREKA_CLIENT_REGISTER_WITH_EUREKA=false&& set EUREKA_CLIENT_FETCH_REGISTRY=false&& mvn spring-boot:run"
goto eureka_wait

:eureka_peers
echo Mode : 2 peers (peer1 sur 8761, peer2 sur 8762)
call :launchprofile serveur-eureka peer1
call :launchprofile serveur-eureka peer2

:eureka_wait
call :waitport %EUREKA_PORT% "Eureka Server"

echo.
echo === 2/3 Microservices ===
call :launch service-utilisateurs
call :launch service-batiments
call :launch service-consommation
call :launch service-detection-fuites
call :launch service-interventions
call :launch service-pieces-rechange
call :launch service-facturation

echo.
echo Attente du demarrage des services (30 s)...
timeout /t 30 /nobreak >nul

echo.
echo === 3/3 API Gateway ===
call :launch passerelle-api

echo.
echo ============================================================
echo  Tout est lance. Dashboard Eureka : http://localhost:%EUREKA_PORT%
echo  Chaque service est dans sa propre fenetre "AquaSentinel-..."
echo  Pour tout arreter : stop-all.bat
echo ============================================================
start "" "http://localhost:%EUREKA_PORT%"
pause
exit /b 0

REM ---------- Lance un module dans une nouvelle fenetre ----------
:launch
if not exist "%ROOT%%~1\pom.xml" (
    echo [ATTENTION] %~1 : pom.xml introuvable, module ignore.
    exit /b 0
)
echo Demarrage de %~1 ...
start "AquaSentinel-%~1" /D "%ROOT%%~1" cmd /k "mvn spring-boot:run"
exit /b 0

REM ---------- Lance un module avec un profil Spring ----------
:launchprofile
echo Demarrage de %~1 (profil %~2) ...
start "AquaSentinel-%~1-%~2" /D "%ROOT%%~1" cmd /k "mvn spring-boot:run -Dspring-boot.run.profiles=%~2"
exit /b 0

REM ---------- Attend qu'un port local soit ouvert ----------
:waitport
echo Attente de %~2 sur le port %~1 (max %MAX_WAIT% s)...
powershell -NoProfile -Command "$t=0; while($t -lt %MAX_WAIT%){ try { $c=New-Object Net.Sockets.TcpClient; $c.Connect('localhost',%~1); $c.Close(); exit 0 } catch { Start-Sleep 2; $t+=2 } }; exit 1"
if errorlevel 1 (
    echo [ATTENTION] %~2 ne repond pas sur le port %~1. On continue quand meme.
) else (
    echo %~2 est pret.
)
exit /b 0