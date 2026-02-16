package FineModule;

public class FineManager {

    // holds the active fine strategy used system-wide (strategy pattern)
    private static FineScheme currentScheme = new FixedFine(); 

    public FineManager() {
    }

    // allows admin module to change the fine calculation strategy at runtime
    public static void setFineScheme(FineScheme scheme) {
        currentScheme = scheme;
    }
    
    public static FineScheme getCurrentScheme() {
        return currentScheme;
    }

    // returns a concrete fine scheme object based on stored name from DB
    public static FineScheme getSchemeByName(String name) {
        if (name == null) return new FixedFine(); 
        switch (name.toUpperCase()) {
            case "HOURLY": return new HourlyFine();
            case "PROGRESSIVE": return new ProgressiveFine();
            case "FIXED": 
            default: return new FixedFine();
        }
    }

    // uses the currently active scheme to calculate overstaying fines
    public double calculateFine(int durationHours) {
        return calculateFine(currentScheme, durationHours, false);
    }

    // core fine calculation logic
    // if in violation then fine starts immediately
    // if normal overstaying occurs then the first 24 hours are free, fine applies after that
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
