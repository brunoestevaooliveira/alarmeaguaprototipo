#!/bin/bash

# Script para compilar e executar o Lembrete de Água Melhorado

echo "🔧 Compilando o Lembrete de Água Melhorado..."

# Compilar o arquivo Java
javac WaterReminderImproved.java

if [ $? -eq 0 ]; then
    echo "✅ Compilação bem-sucedida!"
    echo "🚀 Iniciando o aplicativo..."
    echo ""
    
    # Executar o programa
    java WaterReminderImproved
else
    echo "❌ Erro na compilação!"
    echo "Verifique se o Java está instalado corretamente."
fi
