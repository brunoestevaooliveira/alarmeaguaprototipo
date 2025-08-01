@echo off
echo 🔧 Compilando o Lembrete de Água Melhorado...

REM Compilar o arquivo Java
javac WaterReminderImproved.java

if %ERRORLEVEL% == 0 (
    echo ✅ Compilação bem-sucedida!
    echo 🚀 Iniciando o aplicativo...
    echo.
    
    REM Executar o programa
    java WaterReminderImproved
) else (
    echo ❌ Erro na compilação!
    echo Verifique se o Java está instalado corretamente.
    pause
)
