package feeCalculator;

import java.time.Duration;
import java.time.LocalDateTime;

public class FeeCalculator {
    private static final double RATE_COMPACT = 2.0;
    private static final double RATE_REGULAR = 5.0;
    private static final double RATE_HANDICAPPED = 2.0;
    private static final double RATE_RESERVED = 10.0;
    
    public double calculateParkingFee(LocalDateTime entryTime, LocalDateTime exitTime, 
                                      String spotType, String vehicleType) {
        
        // 1. Calculate duration in hours (rounded UP to nearest hour)
        long hours = calculateDurationInHours(entryTime, exitTime);
        
        // 2. Apply handicapped rules
        if (vehicleType.equalsIgnoreCase("HandicappedVehicle")) {
            // Handicapped vehicle in handicapped spot = FREE
            if (spotType.equalsIgnoreCase("handicapped")) {
                return 0.0; // FREE
            }
            // Handicapped vehicle in any other spot = RM 2/hour
            return hours * 2.0;
        }
        
        // 3. Regular fee calculation for non-handicapped vehicles
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
    
    public static void main(String[] args) {
        FeeCalculator calculator = new FeeCalculator();
        LocalDateTime now = LocalDateTime.now();
        
        System.out.println("=== Testing Simplified Fee Calculator ===");
        
        // Test 1: Handicapped in Handicapped spot = FREE
        double fee = calculator.calculateParkingFee(
            now.minusHours(3), now, "Handicapped", "HandicappedVehicle");
        System.out.println("1. Handicapped in Handicapped spot (3 hours): RM " + fee);
        
        // Test 2: Handicapped in Regular spot = RM 2/hour
        fee = calculator.calculateParkingFee(
            now.minusHours(3), now, "Regular", "HandicappedVehicle");
        System.out.println("2. Handicapped in Regular spot (3 hours): RM " + fee);
        
        // Test 3: Regular car in Regular spot = RM 5/hour
        fee = calculator.calculateParkingFee(
            now.minusHours(3), now, "Regular", "Car");
        System.out.println("3. Regular car in Regular spot (3 hours): RM " + fee);
    }
}