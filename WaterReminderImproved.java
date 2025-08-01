import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;

/**
 * Aplicativo de Lembrete para Tomar Água - Versão Melhorada
 * 
 * Melhorias em relação ao protótipo original:
 * - Interface gráfica completa
 * - Lembretes recorrentes
 * - Acompanhamento de consumo diário
 * - Notificações do sistema
 * - Persistência de configurações
 * - Execução em background (system tray)
 */
public class WaterReminderImproved extends JFrame {
    private static final String PREF_INTERVAL = "reminder_interval";
    private static final String PREF_DAILY_GOAL = "daily_goal";
    private static final String PREF_CUP_SIZE = "cup_size";
    
    private Timer reminderTimer;
    private Preferences prefs;
    private SystemTray systemTray;
    private TrayIcon trayIcon;
    
    // Componentes da interface
    private JSpinner intervalSpinner;
    private JSpinner dailyGoalSpinner;
    private JSpinner cupSizeSpinner;
    private JLabel consumedLabel;
    private JLabel progressLabel;
    private JProgressBar waterProgressBar;
    private JTextArea logTextArea;
    private JButton startButton;
    private JButton stopButton;
    private JButton drinkButton;
    private JButton resetButton;
    
    // Dados do usuário
    private int dailyConsumed = 0;
    private List<String> drinkLog = new ArrayList<>();
    
    public WaterReminderImproved() {
        prefs = Preferences.userNodeForPackage(WaterReminderImproved.class);
        setupSystemTray();
        initializeGUI();
        loadPreferences();
        updateDisplay();
        addToLog("Aplicativo iniciado!");
    }
    
    private void setupSystemTray() {
        if (!SystemTray.isSupported()) {
            System.out.println("System tray não é suportado neste sistema");
            return;
        }
        
        systemTray = SystemTray.getSystemTray();
        
        // Criar ícone da bandeja do sistema
        BufferedImage image = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(new Color(30, 144, 255)); // DodgerBlue
        g2d.fillOval(1, 1, 14, 14);
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 10));
        g2d.drawString("W", 5, 11);
        g2d.dispose();
        
        // Menu popup para o ícone da bandeja
        PopupMenu popup = new PopupMenu();
        MenuItem showItem = new MenuItem("Mostrar Aplicativo");
        MenuItem hideItem = new MenuItem("Minimizar");
        MenuItem exitItem = new MenuItem("Sair");
        
        showItem.addActionListener(e -> {
            setVisible(true);
            setExtendedState(JFrame.NORMAL);
            toFront();
        });
        
        hideItem.addActionListener(e -> setVisible(false));
        exitItem.addActionListener(e -> System.exit(0));
        
        popup.add(showItem);
        popup.add(hideItem);
        popup.addSeparator();
        popup.add(exitItem);
        
        trayIcon = new TrayIcon(image, "Lembrete de Água", popup);
        trayIcon.setImageAutoSize(true);
        
        trayIcon.addActionListener(e -> {
            setVisible(true);
            setExtendedState(JFrame.NORMAL);
            toFront();
        });
        
        try {
            systemTray.add(trayIcon);
        } catch (AWTException e) {
            System.out.println("Erro ao adicionar ícone à bandeja do sistema: " + e.getMessage());
        }
    }
    
    private void initializeGUI() {
        setTitle("💧 Lembrete para Tomar Água - Versão Melhorada v2.0");
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        setLayout(new BorderLayout());
        
        // Painel principal com scroll
        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        
        // === SEÇÃO DE CONFIGURAÇÕES ===
        JPanel configPanel = new JPanel(new GridBagLayout());
        configPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.BLUE, 2), 
            "⚙️ Configurações"
        ));
        configPanel.setBackground(new Color(240, 248, 255));
        
        GridBagConstraints configGbc = new GridBagConstraints();
        configGbc.insets = new Insets(5, 5, 5, 5);
        configGbc.anchor = GridBagConstraints.WEST;
        
        configGbc.gridx = 0; configGbc.gridy = 0;
        configPanel.add(new JLabel("⏰ Intervalo entre lembretes (minutos):"), configGbc);
        configGbc.gridx = 1;
        intervalSpinner = new JSpinner(new SpinnerNumberModel(30, 1, 480, 5));
        ((JSpinner.DefaultEditor) intervalSpinner.getEditor()).getTextField().setColumns(5);
        configPanel.add(intervalSpinner, configGbc);
        
        configGbc.gridx = 0; configGbc.gridy = 1;
        configPanel.add(new JLabel("🎯 Meta diária de água (ml):"), configGbc);
        configGbc.gridx = 1;
        dailyGoalSpinner = new JSpinner(new SpinnerNumberModel(2000, 500, 5000, 250));
        ((JSpinner.DefaultEditor) dailyGoalSpinner.getEditor()).getTextField().setColumns(5);
        configPanel.add(dailyGoalSpinner, configGbc);
        
        configGbc.gridx = 0; configGbc.gridy = 2;
        configPanel.add(new JLabel("🥤 Tamanho do copo/garrafa (ml):"), configGbc);
        configGbc.gridx = 1;
        cupSizeSpinner = new JSpinner(new SpinnerNumberModel(250, 50, 1000, 50));
        ((JSpinner.DefaultEditor) cupSizeSpinner.getEditor()).getTextField().setColumns(5);
        configPanel.add(cupSizeSpinner, configGbc);
        
        // === SEÇÃO DE CONTROLES ===
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        controlPanel.setBorder(BorderFactory.createTitledBorder("🎮 Controles"));
        controlPanel.setBackground(new Color(248, 255, 240));
        
        startButton = new JButton("▶️ Iniciar Lembretes");
        stopButton = new JButton("⏹️ Parar Lembretes");
        drinkButton = new JButton("💧 Registrar que Bebi Água");
        resetButton = new JButton("🔄 Zerar Contador Diário");
        
        startButton.setBackground(new Color(144, 238, 144));
        stopButton.setBackground(new Color(255, 182, 193));
        drinkButton.setBackground(new Color(173, 216, 230));
        resetButton.setBackground(new Color(255, 218, 185));
        
        startButton.addActionListener(this::startReminders);
        stopButton.addActionListener(this::stopReminders);
        drinkButton.addActionListener(this::recordDrink);
        resetButton.addActionListener(this::resetDailyCount);
        
        stopButton.setEnabled(false);
        
        controlPanel.add(startButton);
        controlPanel.add(stopButton);
        controlPanel.add(drinkButton);
        controlPanel.add(resetButton);
        
        // === SEÇÃO DE PROGRESSO ===
        JPanel progressPanel = new JPanel(new GridBagLayout());
        progressPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.GREEN, 2), 
            "📊 Progresso Diário"
        ));
        progressPanel.setBackground(new Color(255, 255, 240));
        
        GridBagConstraints progGbc = new GridBagConstraints();
        progGbc.insets = new Insets(5, 5, 5, 5);
        
        progGbc.gridx = 0; progGbc.gridy = 0; progGbc.gridwidth = 2;
        consumedLabel = new JLabel("💧 Consumido hoje: 0 ml");
        consumedLabel.setFont(consumedLabel.getFont().deriveFont(Font.BOLD, 18f));
        consumedLabel.setForeground(new Color(0, 100, 200));
        progressPanel.add(consumedLabel, progGbc);
        
        progGbc.gridy = 1;
        progressLabel = new JLabel("🎯 Meta: 0% concluída");
        progressLabel.setFont(progressLabel.getFont().deriveFont(Font.BOLD, 14f));
        progressPanel.add(progressLabel, progGbc);
        
        progGbc.gridy = 2; progGbc.fill = GridBagConstraints.HORIZONTAL;
        waterProgressBar = new JProgressBar(0, 100);
        waterProgressBar.setStringPainted(true);
        waterProgressBar.setString("0%");
        waterProgressBar.setPreferredSize(new Dimension(300, 25));
        progressPanel.add(waterProgressBar, progGbc);
        
        // === SEÇÃO DE HISTÓRICO ===
        JPanel logPanel = new JPanel(new BorderLayout());
        logPanel.setBorder(BorderFactory.createTitledBorder("📝 Histórico de Atividades"));
        
        logTextArea = new JTextArea(10, 40);
        logTextArea.setEditable(false);
        logTextArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 11));
        logTextArea.setBackground(new Color(248, 248, 248));
        JScrollPane scrollPane = new JScrollPane(logTextArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        logPanel.add(scrollPane, BorderLayout.CENTER);
        
        // === ADICIONANDO TUDO AO PAINEL PRINCIPAL ===
        gbc.gridx = 0; gbc.gridy = 0; gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(configPanel, gbc);
        
        gbc.gridy = 1;
        mainPanel.add(controlPanel, gbc);
        
        gbc.gridy = 2;
        mainPanel.add(progressPanel, gbc);
        
        gbc.gridy = 3; gbc.fill = GridBagConstraints.BOTH; gbc.weighty = 1.0;
        mainPanel.add(logPanel, gbc);
        
        add(mainPanel, BorderLayout.CENTER);
        
        // === PAINEL DE INFORMAÇÕES ===
        JPanel infoPanel = new JPanel(new FlowLayout());
        infoPanel.setBackground(new Color(230, 230, 250));
        JLabel infoLabel = new JLabel("💡 Dica: Minimize o app para a bandeja do sistema e ele continuará funcionando!");
        infoLabel.setFont(infoLabel.getFont().deriveFont(Font.ITALIC, 12f));
        infoPanel.add(infoLabel);
        add(infoPanel, BorderLayout.SOUTH);
        
        pack();
        setLocationRelativeTo(null);
        setResizable(true);
        setMinimumSize(new Dimension(500, 600));
    }
    
    private void startReminders(ActionEvent e) {
        int intervalMinutes = (Integer) intervalSpinner.getValue();
        
        if (reminderTimer != null && reminderTimer.isRunning()) {
            reminderTimer.stop();
        }
        
        reminderTimer = new Timer(intervalMinutes * 60 * 1000, this::showReminder);
        reminderTimer.start();
        
        startButton.setEnabled(false);
        stopButton.setEnabled(true);
        
        addToLog("⏰ Lembretes iniciados! Intervalo: " + intervalMinutes + " minutos");
        savePreferences();
        
        if (trayIcon != null) {
            trayIcon.displayMessage("💧 Lembrete de Água", 
                "Lembretes ativados! Você será lembrado a cada " + intervalMinutes + " minutos.", 
                TrayIcon.MessageType.INFO);
        }
    }
    
    private void stopReminders(ActionEvent e) {
        if (reminderTimer != null && reminderTimer.isRunning()) {
            reminderTimer.stop();
        }
        
        startButton.setEnabled(true);
        stopButton.setEnabled(false);
        
        addToLog("⏹️ Lembretes desativados");
        
        if (trayIcon != null) {
            trayIcon.displayMessage("💧 Lembrete de Água", 
                "Lembretes desativados. Clique em 'Iniciar' para reativar.", 
                TrayIcon.MessageType.WARNING);
        }
    }
    
    private void showReminder(ActionEvent e) {
        // Som de lembrete mais chamativo
        for (int i = 0; i < 3; i++) {
            Toolkit.getDefaultToolkit().beep();
            try {
                Thread.sleep(200);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }
        
        // Notification toast
        if (trayIcon != null) {
            trayIcon.displayMessage("💧 HORA DE TOMAR ÁGUA! 💧", 
                "Hidrate-se agora! Sua saúde e bem-estar agradecem! 😊", 
                TrayIcon.MessageType.WARNING);
        }
        
        // Dialog se a janela estiver visível
        if (isVisible()) {
            int result = JOptionPane.showConfirmDialog(
                this,
                "💧 HORA DE TOMAR ÁGUA! 💧\n\n" +
                "É importante manter-se hidratado!\n" +
                "Você quer registrar que bebeu água agora?",
                "⚡ Lembrete de Hidratação ⚡",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.INFORMATION_MESSAGE
            );
            
            if (result == JOptionPane.YES_OPTION) {
                recordDrink(null);
            }
        }
        
        addToLog("💧 Lembrete de hidratação exibido");
    }
    
    private void recordDrink(ActionEvent e) {
        int cupSize = (Integer) cupSizeSpinner.getValue();
        dailyConsumed += cupSize;
        
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
        drinkLog.add(timestamp + " - " + cupSize + "ml");
        
        addToLog("✅ Registrado: " + cupSize + "ml às " + timestamp + " - Total: " + dailyConsumed + "ml");
        updateDisplay();
        
        // Verificar se atingiu a meta
        int dailyGoal = (Integer) dailyGoalSpinner.getValue();
        if (dailyConsumed >= dailyGoal && (dailyConsumed - cupSize) < dailyGoal) {
            if (trayIcon != null) {
                trayIcon.displayMessage("🎉 PARABÉNS! 🎉", 
                    "Você atingiu sua meta diária de hidratação! Continue assim!", 
                    TrayIcon.MessageType.INFO);
            }
            addToLog("🎉 META DIÁRIA ATINGIDA! Parabéns pela dedicação!");
            
            JOptionPane.showMessageDialog(this,
                "🎉 PARABÉNS! 🎉\n\n" +
                "Você atingiu sua meta diária de " + dailyGoal + "ml!\n" +
                "Continue se hidratando bem!",
                "Meta Atingida!",
                JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void resetDailyCount(ActionEvent e) {
        int result = JOptionPane.showConfirmDialog(this,
            "Tem certeza que deseja zerar o contador diário?\n" +
            "Consumo atual: " + dailyConsumed + "ml",
            "Confirmar Reset",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);
            
        if (result == JOptionPane.YES_OPTION) {
            dailyConsumed = 0;
            drinkLog.clear();
            updateDisplay();
            addToLog("🔄 Contador diário resetado para 0ml");
        }
    }
    
    private void updateDisplay() {
        int dailyGoal = (Integer) dailyGoalSpinner.getValue();
        int percentage = Math.min(100, (dailyConsumed * 100) / dailyGoal);
        
        consumedLabel.setText("💧 Consumido hoje: " + dailyConsumed + " ml");
        progressLabel.setText("🎯 Meta: " + percentage + "% concluída (" + dailyGoal + " ml)");
        waterProgressBar.setValue(percentage);
        waterProgressBar.setString(percentage + "% da meta");
        
        // Cores do progresso com gradientes
        if (percentage >= 100) {
            waterProgressBar.setForeground(new Color(34, 139, 34)); // Forest Green
            waterProgressBar.setBackground(new Color(144, 238, 144)); // Light Green
        } else if (percentage >= 70) {
            waterProgressBar.setForeground(new Color(255, 140, 0)); // Dark Orange
            waterProgressBar.setBackground(new Color(255, 218, 185)); // Peach Puff
        } else if (percentage >= 30) {
            waterProgressBar.setForeground(new Color(255, 215, 0)); // Gold
            waterProgressBar.setBackground(new Color(255, 255, 224)); // Light Yellow
        } else {
            waterProgressBar.setForeground(new Color(220, 20, 60)); // Crimson
            waterProgressBar.setBackground(new Color(255, 182, 193)); // Light Pink
        }
    }
    
    private void addToLog(String message) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
        String logEntry = "[" + timestamp + "] " + message + "\n";
        logTextArea.append(logEntry);
        logTextArea.setCaretPosition(logTextArea.getDocument().getLength());
        
        // Limitar o histórico a 100 linhas para não consumir muita memória
        String[] lines = logTextArea.getText().split("\n");
        if (lines.length > 100) {
            StringBuilder sb = new StringBuilder();
            for (int i = lines.length - 100; i < lines.length; i++) {
                sb.append(lines[i]).append("\n");
            }
            logTextArea.setText(sb.toString());
        }
    }
    
    private void savePreferences() {
        prefs.putInt(PREF_INTERVAL, (Integer) intervalSpinner.getValue());
        prefs.putInt(PREF_DAILY_GOAL, (Integer) dailyGoalSpinner.getValue());
        prefs.putInt(PREF_CUP_SIZE, (Integer) cupSizeSpinner.getValue());
    }
    
    private void loadPreferences() {
        intervalSpinner.setValue(prefs.getInt(PREF_INTERVAL, 30));
        dailyGoalSpinner.setValue(prefs.getInt(PREF_DAILY_GOAL, 2000));
        cupSizeSpinner.setValue(prefs.getInt(PREF_CUP_SIZE, 250));
    }
    
    public static void main(String[] args) {
        // Configurar Look and Feel nativo do sistema
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | 
                 IllegalAccessException | UnsupportedLookAndFeelException e) {
            System.err.println("Não foi possível definir o Look and Feel: " + e.getMessage());
        }
        
        SwingUtilities.invokeLater(() -> {
            try {
                WaterReminderImproved app = new WaterReminderImproved();
                app.setVisible(true);
                
                // Mensagem de boas-vindas
                JOptionPane.showMessageDialog(app,
                    "💧 Bem-vindo ao Lembrete de Água Melhorado! 💧\n\n" +
                    "Configure suas preferências e clique em 'Iniciar Lembretes'.\n" +
                    "O app pode ser minimizado para a bandeja do sistema.\n\n" +
                    "Mantenha-se hidratado e saudável! 😊",
                    "Bem-vindo!",
                    JOptionPane.INFORMATION_MESSAGE);
                    
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null,
                    "Erro ao iniciar a aplicação: " + e.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}
