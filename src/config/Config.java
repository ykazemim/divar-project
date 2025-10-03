package config;

public class Config {
    private static boolean showExecutionTime = true;
    
    public static boolean isShowExecutionTime() {
        return showExecutionTime;
    }
    
    public static void setShowExecutionTime(boolean show) {
        showExecutionTime = show;
    }
    
    public static void toggleExecutionTime() {
        showExecutionTime = !showExecutionTime;
    }
}
