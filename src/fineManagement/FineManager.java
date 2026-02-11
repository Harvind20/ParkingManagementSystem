package fineManagement;

public class FineManager {

    private FineScheme currentScheme;

    public FineManager() {
        // Default scheme when system starts
        currentScheme = new FixedFine();
    }

    // Admin can change fine scheme later
    public void setFineScheme(FineScheme scheme) {
        this.currentScheme = scheme;
    }

    // Main method used by ExitSystem
    public double calculateFine(int durationHours) {

        // No fine if parked 24 hours or less
        if (durationHours <= 24) {
            return 0;
        }

        // Calculate only the extra hours beyond 24
        int hoursOverstayed = durationHours - 24;

        // Use the selected scheme to calculate fine
        return currentScheme.calculateFine(hoursOverstayed);
    }
}