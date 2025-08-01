import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Versão simplificada do Lembrete de Água que deve funcionar
 */
public class WaterReminderSimple extends JFrame {
    private Timer reminderTimer;
    private JSpinner intervalSpinner;
    private JButton startButton;
    private JButton stopButton;
    private JButton drinkButton;
    private JLabel statusLabel;
    private int waterCount = 0;
    
    public WaterReminderSimple() {
        initializeGUI();
    }
    
    private void initializeGUI() {
        setTitle("💧 Lembrete para Tomar Água");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        // Painel principal
        JPanel mainPanel = new JPanel(new GridLayout(4, 1, 10, 10));
        
        // Configuração do intervalo
        JPanel configPanel = new JPanel(new FlowLayout());
        configPanel.add(new JLabel("Intervalo (minutos):"));
        intervalSpinner = new JSpinner(new SpinnerNumberModel(30, 1, 480, 1));
        configPanel.add(intervalSpinner);
        
        // Botões de controle
        JPanel controlPanel = new JPanel(new FlowLayout());
        startButton = new JButton("▶️ Iniciar Lembretes");
        stopButton = new JButton("⏹️ Parar");
        drinkButton = new JButton("💧 Bebi Água!");
        
        startButton.addActionListener(e -> startReminders());
        stopButton.addActionListener(e -> stopReminders());
        drinkButton.addActionListener(e -> recordDrink());
        
        stopButton.setEnabled(false);
        
        controlPanel.add(startButton);
        controlPanel.add(stopButton);
        controlPanel.add(drinkButton);
        
        // Status
        statusLabel = new JLabel("Pronto para iniciar! Copos bebidos hoje: 0", JLabel.CENTER);
        statusLabel.setFont(statusLabel.getFont().deriveFont(Font.BOLD, 16f));
        
        // Adicionar ao painel principal
        mainPanel.add(new JLabel("🚰 Lembrete de Água - Versão Simplificada", JLabel.CENTER));
        mainPanel.add(configPanel);
        mainPanel.add(controlPanel);
        mainPanel.add(statusLabel);
        
        add(mainPanel, BorderLayout.CENTER);
        
        pack();
        setLocationRelativeTo(null);
        setResizable(false);
    }
    
    private void startReminders() {
        int intervalMinutes = (Integer) intervalSpinner.getValue();
        
        if (reminderTimer != null && reminderTimer.isRunning()) {
            reminderTimer.stop();
        }
        
        reminderTimer = new Timer(intervalMinutes * 60 * 1000, e -> showReminder());
        reminderTimer.start();
        
        startButton.setEnabled(false);
        stopButton.setEnabled(true);
        
        statusLabel.setText("⏰ Lembretes ativos! Próximo em " + intervalMinutes + " min. Copos: " + waterCount);
        
        // Mostrar um lembrete imediato para testar
        showReminderDialog();
    }
    
    private void stopReminders() {
        if (reminderTimer != null && reminderTimer.isRunning()) {
            reminderTimer.stop();
        }
        
        startButton.setEnabled(true);
        stopButton.setEnabled(false);
        
        statusLabel.setText("⏹️ Lembretes parados. Copos bebidos hoje: " + waterCount);
    }
    
    private void showReminder() {
        // Som de alerta
        Toolkit.getDefaultToolkit().beep();
        
        // Mostrar dialog
        showReminderDialog();
    }
    
    private void showReminderDialog() {
        int result = JOptionPane.showConfirmDialog(
            this,
            "💧 HORA DE TOMAR ÁGUA! 💧\n\nHidrate-se agora!\nVocê quer registrar que bebeu água?",
            "⚡ Lembrete de Hidratação ⚡",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.INFORMATION_MESSAGE
        );
        
        if (result == JOptionPane.YES_OPTION) {
            recordDrink();
        }
    }
    
    private void recordDrink() {
        waterCount++;
        statusLabel.setText("✅ Ótimo! Copos bebidos hoje: " + waterCount);
        
        if (waterCount >= 8) {
            JOptionPane.showMessageDialog(this, 
                "🎉 PARABÉNS! 🎉\n\nVocê bebeu " + waterCount + " copos de água hoje!\nContinue se hidratando bem!",
                "Meta Atingida!",
                JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    public static void main(String[] args) {
        // Configurar Look and Feel
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            // Use o padrão se não conseguir definir o Nimbus
        }
        
        SwingUtilities.invokeLater(() -> {
            new WaterReminderSimple().setVisible(true);
            
            // Mensagem de boas-vindas
            JOptionPane.showMessageDialog(null,
                "💧 Bem-vindo ao Lembrete de Água! 💧\n\n" +
                "Configure o intervalo e clique em 'Iniciar Lembretes'.\n" +
                "Mantenha-se hidratado! 😊",
                "Bem-vindo!",
                JOptionPane.INFORMATION_MESSAGE);
        });
    }
}
