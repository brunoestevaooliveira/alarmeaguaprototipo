import java.util.Scanner;

public class LembreteAgua {
    public static void main(String[] args) throws InterruptedException {
        Scanner scanner = new Scanner(System.in);

        System.out.println("=== Lembrete para Tomar Água ===");
        System.out.print("Em quantos minutos você quer ser lembrado para tomar água? ");
        int minutos = scanner.nextInt();

        System.out.println("Ok! Vou te lembrar em " + minutos + " minutos.");
        // Converte minutos para milissegundos
        long tempoEspera = minutos * 60 * 1000;

        Thread.sleep(tempoEspera);

        // Alarme: mensagem e beep sonoro
        System.out.println("\n\n⏰ Hora de tomar água! ⏰");
        java.awt.Toolkit.getDefaultToolkit().beep();

        scanner.close();
    }
}