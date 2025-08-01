import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Classe utilitária para o aplicativo de lembrete de água
 */
public class WaterUtils {
    
    /**
     * Calcula a quantidade recomendada de água por dia baseada no peso
     * Fórmula: 35ml por kg de peso corporal
     */
    public static int calculateDailyWaterGoal(double weightKg) {
        return (int) (weightKg * 35);
    }
    
    /**
     * Calcula quantos copos são necessários para atingir a meta
     */
    public static int calculateCupsNeeded(int dailyGoalMl, int cupSizeMl) {
        return (int) Math.ceil((double) dailyGoalMl / cupSizeMl);
    }
    
    /**
     * Formata a quantidade de água de forma legível
     */
    public static String formatWaterAmount(int amountMl) {
        if (amountMl >= 1000) {
            double liters = amountMl / 1000.0;
            return String.format("%.1f litros", liters);
        } else {
            return amountMl + "ml";
        }
    }
    
    /**
     * Gera uma mensagem motivacional baseada no progresso
     */
    public static String getMotivationalMessage(int percentage) {
        if (percentage >= 100) {
            return "🎉 Excelente! Meta atingida!";
        } else if (percentage >= 80) {
            return "👏 Muito bem! Quase lá!";
        } else if (percentage >= 60) {
            return "💪 Bom progresso! Continue!";
        } else if (percentage >= 40) {
            return "🚰 Está no caminho certo!";
        } else if (percentage >= 20) {
            return "💧 Vamos lá, você consegue!";
        } else {
            return "🌟 Comece agora, cada gota conta!";
        }
    }
    
    /**
     * Calcula o próximo horário de lembrete
     */
    public static String getNextReminderTime(int intervalMinutes) {
        LocalDateTime next = LocalDateTime.now().plusMinutes(intervalMinutes);
        return next.format(DateTimeFormatter.ofPattern("HH:mm"));
    }
    
    /**
     * Verifica se é um horário adequado para lembrete (evita madrugada)
     */
    public static boolean isAppropriateTimeForReminder() {
        int hour = LocalDateTime.now().getHour();
        return hour >= 6 && hour <= 22; // Entre 6h e 22h
    }
    
    /**
     * Gera dicas de hidratação
     */
    public static String getHydrationTip() {
        String[] tips = {
            "💡 Beba um copo de água ao acordar para ativar o metabolismo!",
            "💡 Mantenha uma garrafa de água sempre à vista!",
            "💡 Adicione rodelas de limão ou pepino para variar o sabor!",
            "💡 Beba água antes, durante e após exercícios físicos!",
            "💡 A sede é um sinal tardio - beba antes de sentir sede!",
            "💡 Alimentos como melancia e pepino também hidratam!",
            "💡 Evite bebidas com cafeína em excesso - elas desidratam!",
            "💡 Acompanhe a cor da urina - deve estar clara!",
            "💡 Beba mais água em dias quentes ou quando estiver doente!",
            "💡 Configure lembretes regulares - seu corpo agradece!"
        };
        
        int randomIndex = (int) (Math.random() * tips.length);
        return tips[randomIndex];
    }
}
