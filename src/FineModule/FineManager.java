package FineModule;

public class FineManager {

    private static FineScheme currentScheme = new FixedFine(); 

    public FineManager() {
    }

    public static void setFineScheme(FineScheme scheme) {
        currentScheme = scheme;
    }

    public double calculateFine(int durationHours) {
        if (durationHours <= 24) return 0;
        int hoursOverstayed = durationHours - 24;
        return currentScheme.calculateFine(hoursOverstayed);
    }
}