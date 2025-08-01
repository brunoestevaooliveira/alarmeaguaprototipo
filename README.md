# 💧 Lembrete para Tomar Água - Versão Melhorada

Uma aplicação Java completa e funcional para lembrar você de se manter hidratado ao longo do dia!

## 🌟 Funcionalidades

### ✨ Versão Original (LembreteAgua.java)
- Console simples com timer básico
- Mensagens de lembrete no terminal

### 🚀 Versão Melhorada (WaterReminderApp.java)
- **Interface Gráfica Moderna**: Interface intuitiva com Java Swing
- **Sistema de Notificações**: Alertas visuais e sonoros
- **Bandeja do Sistema**: Minimização para system tray
- **Configurações Personalizáveis**: 
  - Intervalo entre lembretes (1-480 minutos)
  - Meta diária de água (500-5000ml)
  - Tamanho do copo padrão (50-1000ml)
- **Rastreamento de Progresso**: 
  - Contador de água consumida
  - Barra de progresso visual
  - Percentual da meta diária
- **Persistência de Dados**: Configurações salvas automaticamente
- **Histórico Detalhado**: Log completo de atividades com timestamps
- **Validação de Meta**: Celebração ao atingir objetivo diário

### 💡 Versão Simplificada (WaterReminderSimple.java) - **RECOMENDADA**
- **Interface Funcional**: GUI simplificada que funciona em qualquer ambiente
- **Compatibilidade Máxima**: Testada e funcionando no Linux
- **System Tray Opcional**: Funciona com ou sem suporte à bandeja do sistema
- **Todas as Funcionalidades**: Mantém recursos essenciais da versão completa

## � Comparação de Versões

| Funcionalidade | Versão Original | Versão Melhorada | Versão Simplificada |
|----------------|-----------------|------------------|---------------------|
| Interface | ❌ Console apenas | ✅ GUI completa | ✅ GUI funcional |
| Notificações | ❌ Texto simples | ✅ Sistema + Som | ✅ Dialogs + Som |
| Configurações | ❌ Fixas no código | ✅ Personalizáveis | ✅ Personalizáveis |
| Progresso | ❌ Não rastreia | ✅ Barra visual | ✅ Barra visual |
| Persistência | ❌ Não salva | ✅ Auto-save | ✅ Auto-save |
| System Tray | ❌ Não suporta | ✅ Minimização | ⚠️ Opcional |
| Histórico | ❌ Não mantém | ✅ Log completo | ✅ Log completo |
| Compatibilidade | ✅ Universal | ⚠️ Requer ambiente completo | ✅ Universal |

## 🔧 Requisitos do Sistema

- **Java**: JDK 8 ou superior
- **Sistema Operacional**: Windows, macOS, ou Linux
- **Ambiente Gráfico**: Para interface GUI (X11 no Linux)
- **Permissões**: Acesso à bandeja do sistema (opcional na versão simplificada)

## � Como Executar

### ⭐ Versão Simplificada (RECOMENDADA)
```bash
# Compilar e executar
javac WaterReminderSimple.java
java WaterReminderSimple
```

### Versão Original (Console)
```bash
javac LembreteAgua.java
java LembreteAgua
```

### Versão Melhorada (GUI Completa)

#### Opção 1: Execução Direta
```bash
# Compilar
javac -cp . src/main/java/com/waterreminder/WaterReminderApp.java

# Executar
java -cp .:src/main/java com.waterreminder.WaterReminderApp
```

#### Opção 2: Usando Scripts
```bash
# Linux/macOS
chmod +x run.sh
./run.sh

# Windows
run.bat
```

## 📖 Manual de Uso

### � Configuração Inicial
1. **Intervalo**: Define minutos entre lembretes (padrão: 30min)
2. **Meta Diária**: Quantidade de água desejada por dia (padrão: 2000ml)
3. **Tamanho do Copo**: Volume padrão por registro (padrão: 250ml)

### ⏰ Controles Principais
- **▶️ Iniciar Lembretes**: Ativa o sistema de notificações
- **⏹️ Parar**: Desativa os lembretes temporariamente  
- **💧 Bebi Água!**: Registra consumo manualmente

### � Monitoramento
- **Progresso Visual**: Barra colorida indica % da meta
- **Consumo Atual**: Total de água registrado hoje
- **Histórico**: Log cronológico de todas as atividades

### 🔔 Notificações
- **Som**: Beep do sistema ao lembrar
- **Popup**: Balloon tip na bandeja do sistema (versão completa)
- **Dialog**: Janela de confirmação em todas as versões

## 🎨 Recursos Visuais

- **Interface Intuitiva**: Layout organizado com abas e painéis
- **Ícones Expressivos**: Emojis para melhor UX
- **Cores Dinâmicas**: Barra de progresso muda cor conforme meta
- **Sistema Tray**: Ícone na bandeja para acesso rápido (quando suportado)

## � Estrutura do Projeto

```
📦 alarmeaguaprototipo/
├── � LembreteAgua.java             # Versão original simples
├── 📄 WaterReminderSimple.java      # ⭐ Versão simplificada funcional
├── 📁 src/main/java/com/waterreminder/
│   ├── 📄 WaterReminderApp.java     # Versão melhorada completa
│   └── 📄 WaterUtils.java           # Utilitários de hidratação
├── 📄 TestJava.java                 # Teste de ambiente Java
├── 📄 TestSwing.java                # Teste de interface Swing
├── 📄 run.sh                        # Script para Linux/macOS
├── 📄 run.bat                       # Script para Windows
├── 📄 README.md                     # Esta documentação
├── 📄 VERSION_COMPARISON.md         # Comparação detalhada
└── 📄 IMPROVEMENTS_SUMMARY.md       # Resumo das melhorias

```

## 🐛 Solução de Problemas

### Erro: "class not found"
```bash
# Para versão simplificada
javac WaterReminderSimple.java
java WaterReminderSimple

# Para versão completa
java -cp .:src/main/java com.waterreminder.WaterReminderApp
```

### SystemTray não funciona
- Use a versão simplificada que funciona sem system tray
- No Linux: certifique-se que o DISPLAY está configurado

### Erro de compilação
```bash
# Verificar versão do Java
java -version
javac -version

# Testar ambiente
javac TestJava.java && java TestJava
javac TestSwing.java && java TestSwing
```

## 🎯 Próximas Melhorias

- [ ] **Estatísticas Avançadas**: Gráficos de consumo semanal/mensal
- [ ] **Múltiplos Perfis**: Configurações para diferentes usuários
- [ ] **Integração Web**: Sincronização com apps de saúde
- [ ] **Modo Escuro**: Tema alternativo para interface
- [ ] **Sons Personalizados**: Diferentes alertas sonoros
- [ ] **Exportar Dados**: Relatórios em PDF/CSV

## 📄 Licença

Este projeto está sob a licença MIT. Veja o arquivo `LICENSE` para mais detalhes.

## 🤝 Contribuições

Contribuições são sempre bem-vindas! Sinta-se à vontade para:

1. 🍴 Fork o projeto
2. 🌿 Criar uma branch para sua feature (`git checkout -b feature/AmazingFeature`)
3. ✅ Commit suas mudanças (`git commit -m 'Add some AmazingFeature'`)
4. 📤 Push para a branch (`git push origin feature/AmazingFeature`)
5. 🔃 Abrir um Pull Request

---

**Desenvolvido com ❤️ para promover uma vida mais saudável e hidratada! 💧**
