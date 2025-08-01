import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * Versão simplificada para testar se o Swing funciona
 */
public class TestSwing extends JFrame {
    public TestSwing() {
        setTitle("💧 Teste do Swing - Lembrete de Água");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 200);
        setLayout(new FlowLayout());
        
        JLabel label = new JLabel("✅ Interface gráfica funcionando!");
        JButton button = new JButton("💧 Clique aqui!");
        
        button.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "🎉 GUI está funcionando perfeitamente!");
        });
        
        add(label);
        add(button);
        
        setLocationRelativeTo(null);
        setVisible(true);
    }
    
    public static void main(String[] args) {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            System.out.println("⚠️ Aviso: " + e.getMessage());
        }
        
        SwingUtilities.invokeLater(() -> {
            System.out.println("🚀 Iniciando interface gráfica...");
            new TestSwing();
        });
    }
}
