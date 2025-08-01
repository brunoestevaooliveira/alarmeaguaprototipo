package com.waterreminder;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;

/**
 * Aplicativo de Lembrete para Tomar Água
 * Uma versão melhorada e funcional do protótipo original
 */
public class WaterReminderApp extends JFrame {
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
    
    // Dados do usuário
    private int dailyConsumed = 0;
    private List<String> drinkLog = new ArrayList<>();
    
    public WaterReminderApp() {
        prefs = Preferences.userNodeForPackage(WaterReminderApp.class);
        setupSystemTray();
        initializeGUI();
        loadPreferences();
        updateDisplay();
    }
    
    private void setupSystemTray() {
        if (!SystemTray.isSupported()) {
            System.out.println("System tray não é suportado neste sistema");
            return;
        }
        
        systemTray = SystemTray.getSystemTray();
        
        // Criar ícone da bandeja do sistema (criando uma imagem simples)
        BufferedImage image = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        g2d.setColor(Color.BLUE);
        g2d.fillOval(2, 2, 12, 12);
        g2d.setColor(Color.WHITE);
        g2d.drawString("💧", 4, 12);
        g2d.dispose();
        
        // Menu popup para o ícone da bandeja
        PopupMenu popup = new PopupMenu();
        MenuItem showItem = new MenuItem("Mostrar");
        MenuItem exitItem = new MenuItem("Sair");
        
        showItem.addActionListener(e -> {
            setVisible(true);
            setExtendedState(JFrame.NORMAL);
        });
        
        exitItem.addActionListener(e -> System.exit(0));
        
        popup.add(showItem);
        popup.addSeparator();
        popup.add(exitItem);
        
        trayIcon = new TrayIcon(image, "Lembrete de Água", popup);
        trayIcon.setImageAutoSize(true);
        
        trayIcon.addActionListener(e -> {
            setVisible(true);
            setExtendedState(JFrame.NORMAL);
        });
        
        try {
            systemTray.add(trayIcon);
        } catch (AWTException e) {
            System.out.println("Erro ao adicionar ícone à bandeja do sistema");
        }
    }
    
    private void initializeGUI() {
        setTitle("💧 Lembrete para Tomar Água - Versão Melhorada");
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        setLayout(new BorderLayout());
        
        // Painel principal
        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Configurações
        JPanel configPanel = new JPanel(new GridBagLayout());
        configPanel.setBorder(BorderFactory.createTitledBorder("⚙️ Configurações"));
        
        gbc.gridx = 0; gbc.gridy = 0;
        configPanel.add(new JLabel("Intervalo (minutos):"), gbc);
        gbc.gridx = 1;
        intervalSpinner = new JSpinner(new SpinnerNumberModel(30, 1, 480, 1));
        configPanel.add(intervalSpinner, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        configPanel.add(new JLabel("Meta diária (ml):"), gbc);
        gbc.gridx = 1;
        dailyGoalSpinner = new JSpinner(new SpinnerNumberModel(2000, 500, 5000, 100));
        configPanel.add(dailyGoalSpinner, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        configPanel.add(new JLabel("Tamanho do copo (ml):"), gbc);
        gbc.gridx = 1;
        cupSizeSpinner = new JSpinner(new SpinnerNumberModel(250, 50, 1000, 50));
        configPanel.add(cupSizeSpinner, gbc);
        
        // Controles
        JPanel controlPanel = new JPanel(new FlowLayout());
        startButton = new JButton("▶️ Iniciar Lembretes");
        stopButton = new JButton("⏹️ Parar");
        drinkButton = new JButton("💧 Bebi Água!");
        
        startButton.addActionListener(this::startReminders);
        stopButton.addActionListener(this::stopReminders);
        drinkButton.addActionListener(this::recordDrink);
        
        stopButton.setEnabled(false);
        
        controlPanel.add(startButton);
        controlPanel.add(stopButton);
        controlPanel.add(drinkButton);
        
        // Progresso
        JPanel progressPanel = new JPanel(new GridBagLayout());
        progressPanel.setBorder(BorderFactory.createTitledBorder("📊 Progresso Diário"));
        
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        consumedLabel = new JLabel("Consumido hoje: 0 ml");
        consumedLabel.setFont(consumedLabel.getFont().deriveFont(Font.BOLD, 16f));
        progressPanel.add(consumedLabel, gbc);
        
        gbc.gridy = 1;
        progressLabel = new JLabel("Meta: 0%");
        progressPanel.add(progressLabel, gbc);
        
        gbc.gridy = 2; gbc.fill = GridBagConstraints.HORIZONTAL;
        waterProgressBar = new JProgressBar(0, 100);
        waterProgressBar.setStringPainted(true);
        waterProgressBar.setString("0%");
        progressPanel.add(waterProgressBar, gbc);
        
        // Log de atividades
        JPanel logPanel = new JPanel(new BorderLayout());
        logPanel.setBorder(BorderFactory.createTitledBorder("📝 Histórico"));
        
        logTextArea = new JTextArea(8, 30);
        logTextArea.setEditable(false);
        logTextArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 11));
        JScrollPane scrollPane = new JScrollPane(logTextArea);
        logPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Adicionar componentes ao painel principal
        gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        gbc.gridx = 0; gbc.gridy = 0;
        mainPanel.add(configPanel, gbc);
        
        gbc.gridy = 1;
        mainPanel.add(controlPanel, gbc);
        
        gbc.gridy = 2;
        mainPanel.add(progressPanel, gbc);
        
        gbc.gridy = 3; gbc.fill = GridBagConstraints.BOTH; gbc.weighty = 1.0;
        mainPanel.add(logPanel, gbc);
        
        add(mainPanel, BorderLayout.CENTER);
        
        // Painel de informações
        JPanel infoPanel = new JPanel(new FlowLayout());
        infoPanel.add(new JLabel("💡 Dica: O app pode rodar minimizado na bandeja do sistema"));
        add(infoPanel, BorderLayout.SOUTH);
        
        pack();
        setLocationRelativeTo(null);
        setResizable(true);
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
        
        addToLog("⏰ Lembretes iniciados - Intervalo: " + intervalMinutes + " minutos");
        savePreferences();
        
        if (trayIcon != null) {
            trayIcon.displayMessage("Lembrete de Água", 
                "Lembretes iniciados! Intervalo: " + intervalMinutes + " minutos", 
                TrayIcon.MessageType.INFO);
        }
    }
    
    private void stopReminders(ActionEvent e) {
        if (reminderTimer != null && reminderTimer.isRunning()) {
            reminderTimer.stop();
        }
        
        startButton.setEnabled(true);
        stopButton.setEnabled(false);
        
        addToLog("⏹️ Lembretes parados");
        
        if (trayIcon != null) {
            trayIcon.displayMessage("Lembrete de Água", "Lembretes parados", TrayIcon.MessageType.INFO);
        }
    }
    
    private void showReminder(ActionEvent e) {
        // Som de lembrete
        Toolkit.getDefaultToolkit().beep();
        
        // Notification toast
        if (trayIcon != null) {
            trayIcon.displayMessage("💧 Hora de Tomar Água!", 
                "Hidrate-se! Sua saúde agradece 😊", 
                TrayIcon.MessageType.WARNING);
        }
        
        // Dialog se a janela estiver visível
        if (isVisible()) {
            int result = JOptionPane.showConfirmDialog(
                this,
                "💧 Hora de tomar água!\n\nVocê quer registrar que bebeu água agora?",
                "Lembrete de Hidratação",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.INFORMATION_MESSAGE
            );
            
            if (result == JOptionPane.YES_OPTION) {
                recordDrink(null);
            }
        }
        
        addToLog("💧 Lembrete exibido");
    }
    
    private void recordDrink(ActionEvent e) {
        int cupSize = (Integer) cupSizeSpinner.getValue();
        dailyConsumed += cupSize;
        
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
        drinkLog.add(timestamp + " - " + cupSize + "ml");
        
        addToLog("✅ Registrado: " + cupSize + "ml às " + timestamp);
        updateDisplay();
        
        // Verificar se atingiu a meta
        int dailyGoal = (Integer) dailyGoalSpinner.getValue();
        if (dailyConsumed >= dailyGoal && (dailyConsumed - cupSize) < dailyGoal) {
            if (trayIcon != null) {
                trayIcon.displayMessage("🎉 Parabéns!", 
                    "Você atingiu sua meta diária de hidratação!", 
                    TrayIcon.MessageType.INFO);
            }
            addToLog("🎉 Meta diária atingida! Parabéns!");
        }
    }
    
    private void updateDisplay() {
        int dailyGoal = (Integer) dailyGoalSpinner.getValue();
        int percentage = Math.min(100, (dailyConsumed * 100) / dailyGoal);
        
        consumedLabel.setText("Consumido hoje: " + dailyConsumed + " ml");
        progressLabel.setText("Meta: " + percentage + "% (" + dailyGoal + " ml)");
        waterProgressBar.setValue(percentage);
        waterProgressBar.setString(percentage + "%");
        
        // Cores do progresso
        if (percentage >= 100) {
            waterProgressBar.setForeground(Color.GREEN);
        } else if (percentage >= 70) {
            waterProgressBar.setForeground(Color.ORANGE);
        } else {
            waterProgressBar.setForeground(Color.RED);
        }
    }
    
    private void addToLog(String message) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM HH:mm:ss"));
        String logEntry = "[" + timestamp + "] " + message + "\n";
        logTextArea.append(logEntry);
        logTextArea.setCaretPosition(logTextArea.getDocument().getLength());
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
        // Configurar Look and Feel nativo
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeel());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> {
            new WaterReminderApp().setVisible(true);
        });
    }
}
