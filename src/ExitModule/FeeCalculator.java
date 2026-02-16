package ExitModule;
import java.time.Duration;
import java.time.LocalDateTime;

public class FeeCalculator {
    private static final double RATE_COMPACT = 2.0;
    private static final double RATE_REGULAR = 5.0;
    private static final double RATE_HANDICAPPED = 2.0;
    private static final double RATE_RESERVED = 10.0;
    
    // Main method used by ExitSystem to compute the final parking fee
    public double calculateParkingFee(LocalDateTime entryTime, LocalDateTime exitTime, 
                                      String spotType, String vehicleType) {
        
        long hours = calculateDurationInHours(entryTime, exitTime);
        
        if (vehicleType.equalsIgnoreCase("Handicapped")) {

            // EXACT match with enum name
            if (spotType.equalsIgnoreCase("HANDICAPPED")) {
                return 0.0; // FREE
            }

            return hours * RATE_HANDICAPPED; // RM2/hr everywhere else
        }

        
        double hourlyRate = getHourlyRateForSpotType(spotType);
        return hours * hourlyRate;
    }
    
    private long calculateDurationInHours(LocalDateTime entryTime, LocalDateTime exitTime) {
        Duration duration = Duration.between(entryTime, exitTime);
        long totalMinutes = duration.toMinutes();
        
        if (totalMinutes <= 0) {
            return 1; 
        }
        
        long hours = totalMinutes / 60;
        long remainingMinutes = totalMinutes % 60;
        
        if (remainingMinutes > 0) {
            hours++;
        }
        
        return hours;
    }

    private double getHourlyRateForSpotType(String spotType) {
        switch (spotType.toLowerCase()) {
            case "compact":
                return RATE_COMPACT;
            case "regular":
                return RATE_REGULAR;
            case "handicapped":
                return RATE_HANDICAPPED;
            case "reserved":
                return RATE_RESERVED;
            default:
                return RATE_REGULAR;
        }
    }
}