package FineModule;

public class FineManager {

    private static FineScheme currentScheme = new FixedFine(); 

    public FineManager() {
    }

    public static void setFineScheme(FineScheme scheme) {
        currentScheme = scheme;
    }
    
    public static FineScheme getCurrentScheme() {
        return currentScheme;
    }

    public static FineScheme getSchemeByName(String name) {
        if (name == null) return new FixedFine(); 
        switch (name.toUpperCase()) {
            case "HOURLY": return new HourlyFine();
            case "PROGRESSIVE": return new ProgressiveFine();
            case "FIXED": 
            default: return new FixedFine();
        }
    }

    public double calculateFine(int durationHours) {
        return calculateFine(currentScheme, durationHours, false);
    }

    public double calculateFine(FineScheme scheme, int durationHours, boolean isViolation) {
        int hoursChargeable;

        if (isViolation) {
            hoursChargeable = durationHours;
        } else {
            if (durationHours <= 24) return 0.0;
            hoursChargeable = durationHours - 24;
        }
        
        if (hoursChargeable <= 0) return 0.0;

        return scheme.calculateFine(hoursChargeable);
    }
}