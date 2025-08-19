@echo off
echo ===================================
echo    INICIANDO APLICACAO MALEYANGA
echo ===================================

REM Configurar variaveis de ambiente
set GRAILS_HOME=W:\App\grails-2.3.11\grails-2.3.11
set JAVA_OPTS=-Xms512m -Xmx2048m -XX:PermSize=256m -XX:MaxPermSize=512m
set JAVA_HOME=C:\Program Files\Java\jdk1.7.0_80
set PATH=%GRAILS_HOME%\bin;%JAVA_HOME%\bin;%PATH%
set DEFAULT_PORT=8090

REM Atualizando JAVA_HOME para Java 7
if not exist "%JAVA_HOME%" (
    echo ERRO: JAVA_HOME nao configurado corretamente para Java 7.
    exit /b 1
)

echo JAVA_HOME configurado para: %JAVA_HOME%
echo.
echo Verificando configuracao...
echo GRAILS_HOME: %GRAILS_HOME%
echo.

REM Verificar se a porta 8090 esta ocupada
echo Verificando se a porta %DEFAULT_PORT% esta disponivel...
netstat -an | find ":%DEFAULT_PORT%" | find "LISTENING" >nul
if %errorlevel% == 0 (
    echo.
    echo AVISO: A porta %DEFAULT_PORT% ja esta sendo usada!
    echo.
    echo Opcoes disponiveis:
    echo 1. Parar o servico que esta usando a porta %DEFAULT_PORT%
    echo 2. Usar uma porta diferente
    echo 3. Cancelar
    echo.
    set /p choice="Digite sua opcao (1, 2 ou 3): "

    if "!choice!"=="1" (
        echo.
        echo Tentando parar processos na porta %DEFAULT_PORT%...
        for /f "tokens=5" %%a in ('netstat -ano ^| find ":%DEFAULT_PORT%" ^| find "LISTENING"') do (
            echo Parando processo PID: %%a
            taskkill /PID %%a /F >nul 2>&1
        )
        timeout /t 3 >nul
        echo Processo parado.
        set GRAILS_PORT=%DEFAULT_PORT%
    ) else if "!choice!"=="2" (
        echo.
        set /p new_port="Digite a nova porta (ex: 8091): "
        if "!new_port!"=="" (
            echo Porta invalida. Usando porta 8091 por padrao.
            set GRAILS_PORT=8091
        ) else (
            set GRAILS_PORT=!new_port!
        )
        echo Usando porta !GRAILS_PORT!
    ) else (
        echo Operacao cancelada.
        pause
        exit /b 1
    )
) else (
    echo Porta %DEFAULT_PORT% disponivel.
    set GRAILS_PORT=%DEFAULT_PORT%
)

REM Navegar para o diretorio do projeto
cd /d W:\mz.maleyanga

echo.
echo Limpando cache...
call "%GRAILS_HOME%\bin\grails.bat" clean --non-interactive --stacktrace

echo.
echo Atualizando dependencias...
call "%GRAILS_HOME%\bin\grails.bat" refresh-dependencies --non-interactive --stacktrace

echo.
echo Compilando projeto...
call "%GRAILS_HOME%\bin\grails.bat" compile --non-interactive --stacktrace

echo.
echo Iniciando aplicacao na porta %GRAILS_PORT%...
echo Acesse: http://localhost:%GRAILS_PORT%/MALEYANGA
echo.

REM Iniciar aplicacao com a porta selecionada
if "%GRAILS_PORT%"=="%DEFAULT_PORT%" (
    call "%GRAILS_HOME%\bin\grails.bat" run-app --non-interactive --stacktrace
) else (
    call "%GRAILS_HOME%\bin\grails.bat" -Dserver.port=%GRAILS_PORT% run-app --non-interactive --stacktrace
)

pause
